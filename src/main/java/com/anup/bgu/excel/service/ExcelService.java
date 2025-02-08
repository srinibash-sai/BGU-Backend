package com.anup.bgu.excel.service;

import com.anup.bgu.excel.dto.ExcelData;
import com.anup.bgu.registration.dto.RegistrationResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ExcelService {
    ExcelData soloToExcel(List<RegistrationResponse> registration,String eventName);
    ExcelData teamToExcel(List<RegistrationResponse> registration,String eventName);
    List<String> emailExcelToList(MultipartFile file);
}
