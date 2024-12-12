package com.main.libridex.utils;

import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import com.main.libridex.controller.FileController;

public class ResourceLoader {

    public static String loadResource(String resource) {
        if(!resource.isEmpty()){
            return MvcUriComponentsBuilder
                            .fromMethodName(FileController.class, "serveFile", resource)
                            .build()
                            .toUriString();
        }
        
        return MvcUriComponentsBuilder
            .fromMethodName(FileController.class, "serveFile", "default_image.png")
            .build()
            .toUriString();
    }
    
}
