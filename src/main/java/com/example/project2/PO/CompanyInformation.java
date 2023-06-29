package com.example.project2.PO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CompanyInformation {

    private int companyId;
    private String companyName;
    private String companyRegisteredAddress;
    private String companyMainBusiness;
    private String companyEmployeeBenefits;
    private String companyEmployeeStaffPromotionMechanisms;
    private String companyEmployeeMedicalInsurance;
    private String companyEmployeeSalary;
    private String companyEmployeeOtherBenefits;




}
