package com.anup.bgu.excel.dto;

public record ExcelData(
        byte[] data,
        String filename
) {
}
