package com.fivoriteam.fileserver.property;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.MediaType;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

    public String getFileType(String mediaType){
//        switch(mediaType) {
//            case mediaType.IMAGE_JPEG:
//            case mediaType.IMAGE_PNG:
//            case mediaType.IMAGE_GIF:
//                return "images";
//            default:
//                return "docs";
//        }
        return "images";
    }
}
