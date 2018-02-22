package com.aventstack.klov.controllers;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aventstack.klov.domain.Project;
import com.aventstack.klov.domain.Report;
import com.aventstack.klov.domain.Status;
import com.aventstack.klov.domain.Test;
import com.aventstack.klov.repository.ProjectRepository;
import com.aventstack.klov.repository.ReportRepository;
import com.aventstack.klov.repository.TestRepository;
import com.aventstack.klov.viewdefs.Color;

import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.BeansWrapperBuilder;
import freemarker.template.TemplateHashModel;

@Controller
@Scope("session")
public class ReportViewController {

	@Autowired
	private TestRepository<Test, String> testRepo;

	@Autowired
	private ReportRepository<Report, String> reportRepo;

	@Autowired
	private ProjectRepository<Project, String> projectRepo;

	@GetMapping("/builds")
	public String builds(Map<String, Object> model, Pageable pageable, HttpSession session) {
		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		BeansWrapper bw = new BeansWrapperBuilder(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
				.build();
		TemplateHashModel statics = bw.getStaticModels();
		model.put("statics", statics);

		List<Project> projectList = projectRepo.findAll();
		model.put("projectList", projectList);

		Page<Report> reports = reportRepo.findByProjectOrderByStartTimeDesc(project, pageable);
		int pages = (int) Math.ceil(reportRepo.countByProject(project) / (double) (pageable.getPageSize()));
		model.put("pages", pages);

		List<Report> reportList = reports.getContent();
		model.put("reportList", reportList);
		model.put("isBDD", false);
		if (!reportList.isEmpty()) {
			List<Test> testList = testRepo.findByReportAndLevel(new ObjectId(reportList.get(0).getId()), 0);
			model.put("isBDD", testList.get(0).getBdd());
		}

		// To generate my list
		List<String> versions = Arrays.asList("3.5.18.0", "3.6.19.0", "3.7.20.0");
		List<String> features = Arrays.asList("Quick Sanity","Aggregation", "Client Risk", "Dealer Intervention", "Desk Collaboration","Institutional Trading", "Internal Risk","OMS","Pricing","Retail Trading","Sales Negotiation","Voice Trading");
		Map<String, Map<String, Boolean>> stateByVersionByFeature = new HashMap<String, Map<String, Boolean>>();
		for (String version : versions) {
			if (!stateByVersionByFeature.containsKey(version)) {
				stateByVersionByFeature.put(version, new HashMap<String, Boolean>());
			}
			for (Report reportO : reportList) {
				if (reportO.getName().equals(version)) {
					// Check if module Have One of all the features
					for (String feature : features) {
						if (!stateByVersionByFeature.get(version).containsKey(feature)) {
							stateByVersionByFeature.get(version).put(feature,null);
						}
						if (stateByVersionByFeature.get(version).get(feature)==null) {
							int passCount = 0;
							int failCount = 0;
							for (Test testO : testRepo
									.findByReportAndLevelAndNameContaining(new ObjectId(reportO.getId()), 0, feature)) {
								switch (testO.getStatus()) {
								case "pass":
									passCount++;
									break;
								case "fail":
									failCount++;
									break;
								default:
									break;
								}
							}
							if (passCount > 0 && failCount == 0) {
								stateByVersionByFeature.get(version).put(feature, true);
							} else if (failCount > 0) {
								stateByVersionByFeature.get(version).put(feature, false);
							} else if (passCount == 0 && failCount == 0) {
								stateByVersionByFeature.get(version).put(feature, null);
							}
						}
					}
				}
			}
		}
		model.put("versions", versions);
		model.put("features", features);
		model.put("stateByVersionByFeature", stateByVersionByFeature);
		// end

		model.put("page", reports.getNumber());
		model.put("prettyTime", new PrettyTime());
		model.put("user", session.getAttribute("user"));

		return "builds";
	}

	@GetMapping("/build")
	public String build(@RequestParam("id") String id, @RequestParam("status") Optional<String> status,
			HttpSession session, Map<String, Object> model) {
		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		Report report = reportRepo.findById(id);

		List<Test> testList;
		if (status.isPresent())
			testList = testRepo.findByReportAndLevelAndStatus(new ObjectId(report.getId()), 0, status.get());
		else
			testList = testRepo.findByReportAndLevel(new ObjectId(report.getId()), 0);

		model.put("Color", new Color());
		model.put("report", report);
		model.put("testList", testList);
		model.put("statusList", Status.getAllowedTestStatusHierarchy());

		model.put("isBDD", false);
		if (!testList.isEmpty())
			model.put("isBDD", testList.get(0).getBdd());

		return "build";
	}

	@GetMapping("/build-summary")
	public String buildSummary(@RequestParam("id") String id, @RequestParam("status") Optional<String> status,
			HttpSession session, Map<String, Object> model) {
		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		BeansWrapper bw = new BeansWrapperBuilder(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
				.build();
		TemplateHashModel statics = bw.getStaticModels();
		model.put("statics", statics);

		model.put("prettyTime", new PrettyTime());

		List<Project> projectList = projectRepo.findAll();
		model.put("projectList", projectList);

		Report report = reportRepo.findById(id);

		List<Test> testList;
		if (status.isPresent())
			testList = testRepo.findByReportAndLevelAndStatus(new ObjectId(report.getId()), 0, status.get());
		else
			testList = testRepo.findByReportAndLevel(new ObjectId(report.getId()), 0);

		model.put("Color", new Color());
		model.put("report", report);
		model.put("testList", testList);
		model.put("statusList", Status.getAllowedTestStatusHierarchy());

		model.put("isBDD", false);
		if (!testList.isEmpty())
			model.put("isBDD", testList.get(0).getBdd());

		return "build-summary";
	}

}
