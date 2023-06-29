package com.example.project2.mapper;

import com.example.project2.PO.CompanyInformation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CompanyMapper {
    List<CompanyInformation> getAllCompanies();
}
