package com.aventstack.klov.controllers;

import java.util.Map;
import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.aventstack.klov.domain.Project;
import com.aventstack.klov.domain.Setting;
import com.aventstack.klov.domain.UserSafe;
import com.aventstack.klov.repository.SettingRepository;

@Controller
@Scope("session")
public class SettingsController {

    @SuppressWarnings("unused")
    @Autowired
    private SettingRepository<Setting, String> settingRepo;
    
    @GetMapping("/account/settings")
    public String settings(HttpSession session, Map<String, Object> model) {
        return defaultRedirect(session, "settings", model);
    }
    
    @GetMapping("account/settings/security")
    public String security(HttpSession session, Map<String, Object> model) {
        return defaultRedirect(session, "settings.security", model);
    }
    
    @GetMapping("account/settings/params")
    public String params(HttpSession session, Map<String, Object> model) {
        return defaultRedirect(session, "settings.params", model);
    }
    
    @PostMapping("account/settings/params")
    public String submitParams(HttpSession session, Map<String, Object> model) {
    	
    	System.out.println(model.get("versions"));
    	
        return defaultRedirect(session, "settings.params", model);
    }
    
    private String defaultRedirect(HttpSession session, String path, Map<String, Object> model) {
        UserSafe user = (UserSafe) session.getAttribute("user");
        if (user == null || user.getName() == null || user.getName().isEmpty())
            return "redirect:/auth/signon";
        
        Optional<Project> project = Optional.ofNullable((Project) session.getAttribute("project"));
        model.put("project", project);
        
        return path;
    }
    
}
