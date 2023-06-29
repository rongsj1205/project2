package com.example.project2.controller;


import com.alibaba.fastjson.JSONObject;
import com.example.project2.service.ManageCompanyService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping("/company")
@Slf4j
public class CompanyController {

    @Autowired
    private ManageCompanyService manageCompany;

    @RequestMapping("/query")
    @ResponseBody
    //@CrossOrigin  //跨域访问问题，采用该注解；前端则采用nginx
    public JSONObject getAllCompanies() {
        PageHelper.startPage(1,5);
        JSONObject jsonObject = new JSONObject();
        List companylist = manageCompany.queryCompanylist();
        jsonObject.put("companylist", companylist);
        return jsonObject;
    }



}
