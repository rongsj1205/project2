package com.example.project2.service.impl;

import com.example.project2.PO.CompanyInformation;
import com.example.project2.mapper.CompanyMapper;
import com.example.project2.service.ManageCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
@Slf4j
public class ManageCompanyImpl implements ManageCompanyService {

    @Autowired
    private CompanyMapper companyMapper;

    @Override
    public List queryCompanylist() {
        List<CompanyInformation>    allCompanies = companyMapper.getAllCompanies();
        log.info("已注册公司：" + allCompanies);
        return allCompanies;
    }
}
