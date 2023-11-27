package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.service.FileService;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final Logger logger = Logger.getLogger(FileServiceImpl.class);

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        try {
            // File name
            String name = file.getOriginalFilename();

            if (name == null) {
                throw new IllegalArgumentException("Invalid file name");
            }

            String randomId = UUID.randomUUID().toString();
            String fileName = randomId.concat(name.substring(name.lastIndexOf(".")));

            String filePath = path + File.separator + fileName;

            File directory = new File(path);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            Files.copy(file.getInputStream(), Paths.get(filePath));

            logger.info("Uploaded image with file name: " + fileName);

            return fileName;
        } catch (FileAlreadyExistsException e) {
            logger.error("File already exists: {}" + e.getMessage());
            throw e;
        } catch (IOException e) {
            logger.error("Failed to upload image: {}" + e.getMessage());
            throw e;
        }
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        try {
            String fullPath = path + File.separator + fileName;
            InputStream inputStream = new FileInputStream(fullPath);

            logger.info("Retrieved resource with file name: " + fileName);

            return inputStream;
        } catch (FileNotFoundException e) {
            logger.error("File not found: " + e.getMessage());
            throw e;
        }
    }
}
