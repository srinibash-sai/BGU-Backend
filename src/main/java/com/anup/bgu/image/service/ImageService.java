package com.anup.bgu.image.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImageService {
    String saveImage(MultipartFile file, String EventId);
    byte[] getImage(String imagePath);

}
