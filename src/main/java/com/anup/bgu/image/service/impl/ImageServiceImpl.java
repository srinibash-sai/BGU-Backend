package com.anup.bgu.image.service.impl;

import com.anup.bgu.exceptions.models.InvalidImageException;
import com.anup.bgu.image.service.ImageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ImageServiceImpl implements ImageService {

    private final String uploadPath = "." + File.separator + "uploads" + File.separator;
    private final String paymentsPath = uploadPath + "PaymentsScreenshot" + File.separator;
    private final String eventPath = uploadPath + "EventPoster" + File.separator;

    @Override
    public String saveImage(MultipartFile file, String EventId) {

        final String fileName = file.getOriginalFilename(); // get the current filename of image
        final String extension = extractFileExtension(fileName);

        validateFileExtension(extension);

        byte[] imageByte;
        try {
            imageByte = file.getBytes();
        } catch (IOException e) {
            throw new InvalidImageException("Image Processing Error! Please Upload again.");
        }

        final String newFileName = EventId + "." + extension;
        final String filePath = eventPath + newFileName;

        saveFile(imageByte,filePath);

        return filePath;
    }

    @Async
    private void saveFile(byte[] file, String targetPath) {
        int RETRY_THRESHOLD = 3;
        while (RETRY_THRESHOLD > 0) {
            try {
                Path path = Paths.get(targetPath);
                Files.write(path, file);
                return;
            } catch (IOException e) {

            }
            RETRY_THRESHOLD--;
        }
        throw new InvalidImageException("Some error occurred while saving image");
    }

    private void validateFileExtension(String extension)
    {
        if (!extension.equalsIgnoreCase("jpg")
                && !extension.equalsIgnoreCase("png")
                && !extension.equalsIgnoreCase("jpeg")
        )
        {
            throw new InvalidImageException("Invalid image Extensio! Only JPG, JPEG and PNG are allowed");
        }
    }

    private String extractFileExtension(String fileName)
    {
        if (fileName == null) {
            throw new InvalidImageException("Invalid image! Can't detect file name.");
        }
        int extentionDot = fileName.lastIndexOf(".");
        if (extentionDot == -1) {
            throw new InvalidImageException("Invalid image! Invalid File Format.");
        }
        return fileName.substring(extentionDot + 1).toLowerCase();
    }
}
