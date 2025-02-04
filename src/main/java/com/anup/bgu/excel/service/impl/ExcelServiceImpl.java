package com.anup.bgu.excel.service.impl;

import com.anup.bgu.excel.dto.ExcelData;
import com.anup.bgu.excel.service.ExcelService;
import com.anup.bgu.registration.dto.RegistrationResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Slf4j
@Service
public class ExcelServiceImpl implements ExcelService {
    @Override
    public ExcelData soloToExcel(List<RegistrationResponse> registration, String eventName) {
        Workbook workbook = new XSSFWorkbook();  // Create a new workbook
        Sheet sheet = workbook.createSheet("Registration Data");

        // Create header row for registration data
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Email", "Phone", "Student Type", "Gender", "Registration Date", "College Name", "Transaction ID", "Screenshot"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Row counter for the sheet
        int currentRow = 1;

        // Loop through each RegistrationResponse object
        for (RegistrationResponse registrationResponse : registration) {
            // Add a new row for registration data
            Row dataRow = sheet.createRow(currentRow);
            dataRow.createCell(0).setCellValue(registrationResponse.getId());
            dataRow.createCell(1).setCellValue(registrationResponse.getName());
            dataRow.createCell(2).setCellValue(registrationResponse.getEmail());
            dataRow.createCell(3).setCellValue(registrationResponse.getPhone());
            dataRow.createCell(4).setCellValue(registrationResponse.getStudentType());
            dataRow.createCell(5).setCellValue(registrationResponse.getGender());
            dataRow.createCell(6).setCellValue(registrationResponse.getRegistrationDate());
            dataRow.createCell(7).setCellValue(registrationResponse.getCollegeName());
            dataRow.createCell(9).setCellValue(registrationResponse.getScreenshot());

            if (registrationResponse.getTransactionId() != null)
                dataRow.createCell(8).setCellValue(registrationResponse.getTransactionId());
            else
                dataRow.createCell(8).setCellValue("N/A");

            // Adjust column widths for registration details
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Increment row counter for next row
            currentRow++;
        }

        // Convert the workbook to a byte array
        byte[] excelData = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            excelData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Define the filename
        String filename = eventName + "-registration-data";

        // Return the Excel data and filename
        return new ExcelData(excelData, filename);
    }

    @Override
    public ExcelData teamToExcel(List<RegistrationResponse> registration, String eventName) {
        Workbook workbook = new XSSFWorkbook();  // Create a new workbook
        Sheet sheet = workbook.createSheet("Registration Data");

        // Create header row for registration data
        Row headerRow = sheet.createRow(0);
        String[] headers = {"ID", "Name", "Leader Name", "Team Name", "Email", "Phone", "Student Type", "Gender", "Registration Date", "College Name", "Transaction ID", "Screenshot"};

        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // Row counter for the sheet
        int currentRow = 1;

        // Loop through each RegistrationResponse object
        for (RegistrationResponse registrationResponse : registration) {
            // Add a new row for registration data
            Row dataRow = sheet.createRow(currentRow);
            dataRow.createCell(0).setCellValue(registrationResponse.getId());
            dataRow.createCell(1).setCellValue(registrationResponse.getName());
            dataRow.createCell(2).setCellValue(registrationResponse.getLeaderName());
            dataRow.createCell(3).setCellValue(registrationResponse.getTeamName());
            dataRow.createCell(4).setCellValue(registrationResponse.getEmail());
            dataRow.createCell(5).setCellValue(registrationResponse.getPhone());
            dataRow.createCell(6).setCellValue(registrationResponse.getStudentType());
            dataRow.createCell(7).setCellValue(registrationResponse.getGender());
            dataRow.createCell(8).setCellValue(registrationResponse.getRegistrationDate());
            dataRow.createCell(9).setCellValue(registrationResponse.getCollegeName());
            dataRow.createCell(11).setCellValue(registrationResponse.getScreenshot());

            if (registrationResponse.getTransactionId() != null)
                dataRow.createCell(10).setCellValue(registrationResponse.getTransactionId());
            else
                dataRow.createCell(10).setCellValue("N/A");

            // Adjust column widths for registration details
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Increment row counter for next row
            currentRow++;

            // Add Team Members section if available
            List<RegistrationResponse.Member> teamMembers = registrationResponse.getTeamMembers();
            if (teamMembers != null && !teamMembers.isEmpty()) {
                // Add "Team Members" header
                Row teamMembersHeaderRow = sheet.createRow(currentRow);
                teamMembersHeaderRow.createCell(0).setCellValue("Team Members");
                teamMembersHeaderRow.getCell(0).setCellStyle(createBoldCellStyle(workbook));  // Make header bold

                // Add column headers for team members
                Row teamMemberColumns = sheet.createRow(currentRow + 1);
                teamMemberColumns.createCell(0).setCellValue("Member Name");
                teamMemberColumns.createCell(1).setCellValue("Member Email");

                // Iterate through the team members and add rows for each member
                currentRow += 2;  // Skip two rows for the "Team Members" header and column headers
                for (RegistrationResponse.Member member : teamMembers) {
                    Row memberRow = sheet.createRow(currentRow);
                    memberRow.createCell(0).setCellValue(member.getName());
                    memberRow.createCell(1).setCellValue(member.getEmail());
                    currentRow++;
                }

                // Adjust columns for team members
                sheet.autoSizeColumn(0);
                sheet.autoSizeColumn(1);
            }

            // Add a blank row after each registration and its team members
            currentRow++;
        }

        // Convert the workbook to a byte array
        byte[] excelData = null;
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            workbook.write(byteArrayOutputStream);
            excelData = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            try {
                workbook.close();
            } catch (IOException e) {
                throw new RuntimeException(e.getMessage());
            }
        }

        // Define the filename
        String filename = eventName + "-registration-data";

        // Return the Excel data and filename
        return new ExcelData(excelData, filename);
    }


    // Create a bold cell style for headers
    private static CellStyle createBoldCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        Font boldFont = workbook.createFont();
        boldFont.setBold(true);
        cellStyle.setFont(boldFont);
        return cellStyle;
    }
}
