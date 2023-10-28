package com.ecommerce_apis.application.serviceimpl;

import com.ecommerce_apis.domain.service.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public String uploadImage(String path, MultipartFile file) throws IOException {

        //file name
        String name = file.getOriginalFilename(); //ex: abc.png

        String randomId = UUID.randomUUID().toString();
        String fileName = null;
        if (name != null) {
            fileName = randomId.concat(name.substring(name.lastIndexOf(".")));
        }

        String filePath = path + File.separator + fileName;

        File f = new File(path);
        if (!f.exists()) {
            f.mkdir();
        }

        Files.copy(file.getInputStream(), Paths.get(filePath));

        return fileName;
    }

    @Override
    public InputStream getResource(String path, String fileName) throws FileNotFoundException {
        String fullPath = path + File.separator + fileName;
        return new FileInputStream(fullPath);
    }
}
