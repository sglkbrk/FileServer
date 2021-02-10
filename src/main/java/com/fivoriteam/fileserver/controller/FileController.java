package com.fivoriteam.fileserver.controller;

import com.fivoriteam.fileserver.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/downloadFile/{usid}/{fileName:.+}")
    @Cacheable("Resource")
    public ResponseEntity<Resource> downloadFile(@PathVariable String usid , @PathVariable String fileName, HttpServletRequest request) {

        Resource resource = fileStorageService.loadFileAsResource(fileName,usid);
        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Dosya Uzantisi gecersiz");
        }
        if (contentType == null) contentType = "application/octet-stream";
        MediaType mediaType = MediaType.parseMediaType(contentType);
        if(contentType == "image/png" || contentType == "image/jpeg" || contentType == "image/gif" ){
            return ResponseEntity.ok().contentType(mediaType).body(resource);
        }else{
            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
        }
    }

    @PostMapping("/uploadFile/{userid}")
    public String uploadFile(@RequestParam("file")  MultipartFile file ,@PathVariable String userid) {
        String fileName = fileStorageService.storeFile(file,userid);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("downloadFile/" + userid + "/")
                .path(fileName)
                .toUriString();
        return fileDownloadUri;
    }
}
