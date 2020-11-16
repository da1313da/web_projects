package com.example.projects.blogengine.controllers;

import com.example.projects.blogengine.api.response.TagsListResponse;
import com.example.projects.blogengine.data.GeneralInfoDao;
import com.example.projects.blogengine.model.GlobalSettings;
import com.example.projects.blogengine.repository.GlobalSettingsRepository;
import com.example.projects.blogengine.service.GeneralResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ApiGeneralController {

    @Autowired
    private GeneralInfoDao generalInfo;

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private GeneralResponseService generalResponseService;

    @GetMapping("/api/init")
    public GeneralInfoDao getGeneralInfo(){
        return generalInfo;
    }

    @GetMapping("/api/settings")
    public ResponseEntity<?> getGlobalSettings(){
        List<GlobalSettings> settings = (List<GlobalSettings>) globalSettingsRepository.findAll();
        Map<String, String> responseBody = new HashMap<>();
        for (GlobalSettings s : settings) {
            responseBody.put(s.getCode(), s.getValue());
        }
        return new ResponseEntity<>(responseBody, HttpStatus.OK);
    }

    @GetMapping("/api/tag")
    public TagsListResponse getTagList(@RequestParam(name = "query", required = false) String query){
        return generalResponseService.getTagList(query);
    }
}
