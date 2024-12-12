package com.main.libridex.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("storage")
public class StorageProperties {
    private String book_storage = "images/books";

    public String getBook_storage() {
        return book_storage;
    }

    public void setBook_storage(String book_storage) {
        this.book_storage = book_storage;
    }

    
}
