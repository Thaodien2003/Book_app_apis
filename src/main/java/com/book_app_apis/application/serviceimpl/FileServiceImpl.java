package com.book_app_apis.application.serviceimpl;

import com.book_app_apis.domain.service.FileService;
import com.cloudinary.Cloudinary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Map;

@Service
public class FileServiceImpl implements FileService {
    private static final Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);
    private final MessageSource messageSource;
    private final Cloudinary cloudinary;

    public FileServiceImpl(MessageSource messageSource, Cloudinary cloudinary) {
        this.messageSource = messageSource;
        this.cloudinary = cloudinary;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public String uploadImage(MultipartFile file) {
        try {
            Map data = this.cloudinary.uploader().upload(file.getBytes(), Map.of());
            String imageUrl = (String) data.get("secure_url");
            String uploadLogInfo = messageSource.getMessage("upload.log.info", null, LocaleContextHolder.getLocale());
            logger.info(uploadLogInfo + " - " +imageUrl);
            return imageUrl;
        } catch (IOException e) {
            String uploadImage = messageSource.getMessage("upload.image", null, LocaleContextHolder.getLocale());
            String uploadLogError = messageSource.getMessage("upload.log.error", null, LocaleContextHolder.getLocale());
            logger.error(uploadLogError);
            throw new RuntimeException(uploadImage);
        }
    }
}
