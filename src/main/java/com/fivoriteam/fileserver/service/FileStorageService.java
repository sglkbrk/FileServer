package com.fivoriteam.fileserver.service;


import com.fivoriteam.fileserver.exception.FileStorageException;
import com.fivoriteam.fileserver.exception.MyFileNotFoundException;
import com.fivoriteam.fileserver.property.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {
    private  String filePath;
    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) {
        this.filePath = fileStorageProperties.getUploadDir();
    }

    public Path getFilePath(String usid){
        if(usid == null) throw new FileStorageException("Yüklenen dosyaların depolanacağı dizin oluşturulamadı Personel id boş");
        Path  fileStorageLocation = Paths.get(this.filePath + "/" + usid ).toAbsolutePath().normalize();
        try {
            Files.createDirectories(fileStorageLocation);
            return fileStorageLocation;
        } catch (Exception ex) {
            throw new FileStorageException("Yüklenen dosyaların depolanacağı dizin oluşturulamadı", ex);
        }
    }

    public String storeFile(MultipartFile file,String usid) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Dosya adı gecersiz yol dizisi iceriyor:" + fileName);
            }
            Path targetLocation = getFilePath(usid).resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException ex) {
            throw new FileStorageException("Dosya Oluşturulamadı" + fileName + ". Lutfen Tekrar Deneyin", ex);
        }
    }

    public Resource loadFileAsResource(String fileName,String usid) {
        try {
            Path filePath = getFilePath(usid).resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("Dosya Yok" + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("Dosya yok" + fileName, ex);
        }
    }

}

