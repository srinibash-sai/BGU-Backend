package com.anup.bgu.image.service.impl;

import com.anup.bgu.exceptions.models.InvalidImageException;
import com.anup.bgu.image.service.ImageService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
public class ImageServiceImpl implements ImageService {

    private final String uploadPath = "." + File.separator + "uploads" + File.separator;
    private final String paymentsPath = uploadPath + "PaymentsScreenshot" + File.separator;
    private final String eventPath = uploadPath + "EventPoster" + File.separator;

    @PostConstruct
    void init() {
        creatFilePath(eventPath);
        creatFilePath(paymentsPath);
    }

    @Override
    public String saveImage(MultipartFile file, String EventId) {

        final String fileName = file.getOriginalFilename(); // get the current filename of image
        final String extension = extractFileExtension(fileName);

        log.debug("saveImage() -> extension: {}", extension);

        validateFileExtension(extension);

        byte[] imageByte;
        try {
            imageByte = file.getBytes();
        } catch (IOException e) {
            throw new InvalidImageException("Image Processing Error! Please Upload again.");
        }

        final String newFileName = EventId + "." + extension;
        final String filePath = eventPath + newFileName;
        log.debug("saveImage() -> filePath: {}", filePath);

        saveFile(imageByte, filePath);

        return filePath;
    }

    @Override
    public byte[] getImage(String imagePath) {
        try {
            Path path = Paths.get(imagePath);
            return Files.readAllBytes(path);
        } catch (Exception e) {
            log.warn("getImage() -> filePath: {}", imagePath);
            throw new InvalidImageException("Internal Server Error! Image does not exist.");
        }
    }

    @Override
    public String savePaymentImage(MultipartFile file, String EventId, String paymentId) {
        final String fileName = file.getOriginalFilename(); // get the current filename of image
        final String extension = extractFileExtension(fileName);

        log.debug("saveImage() -> extension: {}", extension);

        validateFileExtension(extension);

        byte[] imageByte;
        try {
            imageByte = file.getBytes();
        } catch (IOException e) {
            throw new InvalidImageException("Image Processing Error! Please Upload again.");
        }

        final String newFileName = paymentId + "." + extension;
        creatFilePath(paymentsPath + EventId + File.separator);

        final String filePath = paymentsPath + EventId + File.separator + newFileName;
        log.debug("saveImage() -> filePath: {}", filePath);

        saveFile(imageByte, filePath);

        return filePath;
    }

    private void saveFile(byte[] file, String targetPath) {
        final int MAX_RETRIES = 3;
        int retryCount = 0;

        while (retryCount < MAX_RETRIES) {
            try {
                Path path = Paths.get(targetPath);
                log.debug("saveFile() -> Retry attempt: {}/{} , path: {}", retryCount + 1, MAX_RETRIES, path);
                Files.write(path, file);

                return;
            } catch (IOException e) {
                retryCount++;
                log.warn("saveFile() -> IOException on attempt {}: {}", retryCount, e.getMessage(), e);
                if (retryCount == MAX_RETRIES) {
                    throw new RuntimeException("Failed to save file after " + MAX_RETRIES + " attempts.", e);
                }
            }
        }
    }

    private void validateFileExtension(String extension) {
        if (!extension.equalsIgnoreCase("jpg")
                && !extension.equalsIgnoreCase("png")
                && !extension.equalsIgnoreCase("jpeg")
        ) {
            throw new InvalidImageException("Invalid image Extensio! Only JPG, JPEG and PNG are allowed");
        }
    }

    private String extractFileExtension(String fileName) {
        if (fileName == null) {
            throw new InvalidImageException("Invalid image! Can't detect file name.");
        }
        int extentionDot = fileName.lastIndexOf(".");
        if (extentionDot == -1) {
            throw new InvalidImageException("Invalid image! Invalid File Format.");
        }
        return fileName.substring(extentionDot + 1).toLowerCase();
    }

    private void creatFilePath(String targetFolderPath) {
        File targetFolder = new File(targetFolderPath);
        if (!targetFolder.exists()) {
            log.debug("createFilePath() -> targetFolderPath: {}", targetFolderPath);
            boolean created = targetFolder.mkdirs();
            if (!created) {
                log.warn("createFolder() -> failed to create target folder: {}", targetFolderPath);
                throw new InvalidImageException("Some error occurred while creating the folder");
            }
        }
    }
}