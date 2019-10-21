package com.aventstack.klov.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.bson.types.ObjectId;
import org.ocpsoft.prettytime.PrettyTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.aventstack.klov.domain.Category;
import com.aventstack.klov.domain.Environment;
import com.aventstack.klov.domain.Feature;
import com.aventstack.klov.domain.Project;
import com.aventstack.klov.domain.Report;
import com.aventstack.klov.domain.Status;
import com.aventstack.klov.domain.Test;
import com.aventstack.klov.domain.VersionComparator;
import com.aventstack.klov.repository.CategoryRepository;
import com.aventstack.klov.repository.EnvironmentRepository;
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
	private CategoryRepository<Report, String> categoryRepo;

	@Autowired
	private ProjectRepository<Project, String> projectRepo;

	@GetMapping("/builds")
	public String builds(Map<String, Object> model, Pageable pageable,
			@RequestParam("category") Optional<String> category, HttpSession session) {
		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		BeansWrapper bw = new BeansWrapperBuilder(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
				.build();
		TemplateHashModel statics = bw.getStaticModels();
		model.put("statics", statics);

		List<Project> projectList = projectRepo.findAll();
		model.put("projectList", projectList);

		List<String> categoryList = categoryRepo.findAll().parallelStream().map(Category::getName).distinct()
				.sorted(new VersionComparator()).collect(Collectors.toList());
		model.put("categoryList", categoryList);

		if (!category.isPresent()) {

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

			model.put("page", reports.getNumber());

		} else {
			model.put("reportList", reportRepo.findByCategoryNameList(category.get()));
			model.put("isBDD", false);
			model.put("pages", 0);
			model.put("page", 0);
		}

		model.put("prettyTime", new PrettyTime());
		model.put("user", session.getAttribute("user"));

		return "builds";
	}

	@GetMapping("/tests")
	public String tests(Map<String, Object> model, Pageable pageable,
			@RequestParam("category") Optional<String> category, @RequestParam("name") Optional<String> name,
			HttpSession session) {
		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		BeansWrapper bw = new BeansWrapperBuilder(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
				.build();
		TemplateHashModel statics = bw.getStaticModels();
		model.put("statics", statics);

		List<Project> projectList = projectRepo.findAll();
		model.put("projectList", projectList);

		List<String> categoryList = categoryRepo.findAll().parallelStream().map(Category::getName).distinct()
				.sorted(new VersionComparator()).collect(Collectors.toList());
		model.put("categoryList", categoryList);
		model.put("nameList", Feature.values());

		Page<Test> tests = null;

		if (category.isPresent() && name.isPresent()) {
			tests = testRepo.findByCategoryNameListInAndLevelAndNameContainingOrderByEndTimeDesc(category.get(),
					Integer.valueOf(0), name.get(), pageable);
		} else if (category.isPresent()) {
			tests = testRepo.findByCategoryNameListInAndLevelAndNameContainingOrderByEndTimeDesc(category.get(),
					Integer.valueOf(0), "", pageable);
		} else if (name.isPresent()) {
			tests = testRepo.findByLevelAndNameContainingOrderByEndTimeDesc(Integer.valueOf(0), name.get(), pageable);
		} else {
			tests = testRepo.findByLevelOrderByEndTimeDesc(Integer.valueOf(0), pageable);
		}
		int pages = (int) Math.ceil(testRepo.countByLevelByCategoryByName(Optional.of(0), category, name)
				/ (double) (pageable.getPageSize()));
		model.put("pages", pages);

		List<Test> testList = tests.getContent();

		Map<String, Report> reportMap = new HashMap<>();

		testList.parallelStream().forEach(test -> {
			reportMap.put(test.getReport(), reportRepo.findById(test.getReport()));
			Environment env = new Environment();
		});

		model.put("testList", testList);
		model.put("reportMap", reportMap);

		model.put("isBDD", false);

		model.put("page", tests.getNumber());

		model.put("prettyTime", new PrettyTime());
		model.put("user", session.getAttribute("user"));

		return "tests";
	}

	@GetMapping("/lastReports")
	public String lastReports(Map<String, Object> model, Pageable pageable, @RequestParam("dv") Optional<List<String>> displayedVersions, HttpSession session) {

		Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
		model.put("project", project);

		BeansWrapper bw = new BeansWrapperBuilder(freemarker.template.Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS)
				.build();
		TemplateHashModel statics = bw.getStaticModels();
		model.put("statics", statics);

		if(displayedVersions.isPresent()) {
			Map<String,String> tempDvs=new HashMap<>();
			displayedVersions.get().forEach(dv->tempDvs.put(dv.split("\\.")[0] + "." + dv.split("\\.")[1]+".",dv));
			displayedVersions.get().clear();
			displayedVersions.get().addAll(tempDvs.values());
			StringBuilder dvs=new StringBuilder();
			displayedVersions.get().stream().forEach(dv -> dvs.append("dv=" + dv + "&"));
			model.put("dvs", dvs.toString());
		}

		List<String> versions = Collections.synchronizedList(new ArrayList<String>());
		List<String> emptyVersions = Collections.synchronizedList(new ArrayList<String>());
		genVersionPart().parallelStream().forEach(v -> {
			Report report=null;
			if(displayedVersions.isPresent()){
				Optional<String> versionToShow = displayedVersions.get().parallelStream().filter(displayedVersion -> displayedVersion.startsWith(v)).findFirst();
				if(versionToShow.isPresent()){
					report=reportRepo.findFirstByNameOrderByEndTimeDesc(versionToShow.get());
				}
				if(report==null){
					report=reportRepo.findFirstByNameStartingWithOrderByEndTimeDesc(v);
				}
			}else {
				report=reportRepo.findFirstByNameStartingWithOrderByEndTimeDesc(v);
			}

			if (report != null) {
				synchronized (versions) {
					versions.add(report.getName());
				}
			}
		});
		Collections.sort(versions, new VersionComparator());

		Map<String, Report> reportMap = new HashMap<>();

		Map<String, Map<String, Test>> stateByVersionByFeature = new HashMap<String, Map<String, Test>>();
		versions.parallelStream().forEach(version -> {
			for (Feature feature : Feature.values()) {
				stateByVersionByFeature.putIfAbsent(version, new HashMap<String, Test>());

				Test test = testRepo.findFirstByCategoryNameListInAndLevelAndNameContainingOrderByEndTimeDesc(version,
						0, feature.getQueryName());
				if (test != null) {
					reportMap.put(test.getReport(), reportRepo.findById(test.getReport()));
				}

				stateByVersionByFeature.get(version).put(feature.getQueryName(), test);
			}
			if (stateByVersionByFeature.get(version).values().parallelStream().allMatch(test -> test == null)) {
				if(!(displayedVersions.isPresent() && displayedVersions.get().contains(version))){
					emptyVersions.add(version);
				}
			}
		});

		versions.removeAll(emptyVersions);

		List<String> categoryList = Collections.synchronizedList(categoryRepo.findAll().parallelStream().map(Category::getName).distinct()
				.sorted(new VersionComparator()).collect(Collectors.toList()));
		Map<String,List<String>> similarVersionsMap = new ConcurrentHashMap<>();
		versions.parallelStream().forEach(v->{
			similarVersionsMap.put(v,categoryList.parallelStream().filter(sv->sv.startsWith(v.split("\\.")[0] + "." + v.split("\\.")[1]+".")).sorted(new VersionComparator()).collect(Collectors.toList()));
		});

		model.put("similarVersionsMap", similarVersionsMap);

		model.put("versions", versions);
		model.put("reportMap", reportMap);

		model.put("features", Feature.values());
		model.put("stateByVersionByFeature", stateByVersionByFeature);
		// end

		model.put("prettyTime", new PrettyTime());
		model.put("user", session.getAttribute("user"));

		return "lastReports";

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

	Set<String> genVersionPart() {
		Set<String> versions = Collections.synchronizedSet(new TreeSet<>(new VersionComparator()));
		categoryRepo.findAll().parallelStream().forEach(cat -> {
			String name = cat.getName();
			if (name.split("\\.").length >= 4) {
				synchronized (versions) {
					versions.add(name.split("\\.")[0] + "." + name.split("\\.")[1]+".");
				}
			}
		});
		return versions;
	}

}
