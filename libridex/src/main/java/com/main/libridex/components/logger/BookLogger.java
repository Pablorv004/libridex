package com.main.libridex.components.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("bookLogger")
public class BookLogger {
    private static final Log logger = LogFactory.getLog(BookLogger.class);

    public void added(String title) {
        logger.info("Successfully Added Book: " + title);
    }

    public void failedToAdd(String title, Exception e) {
        logger.error("Failed to Add Book: " + title + ". Here's the exception: "  + e.toString());
    }

    public void updated(String title) {
        logger.info("Successfully Updated Book: " + title);
    }

    public void failedToUpdate(String title, Exception e) {
        logger.error("Failed to Update Book: " + title + ". Here's the exception: "  + e.toString());
    }

    public void deleted(String title) {
        logger.info("Successfully Deleted Book: " + title);
    }

    public void failedToDelete(String title, Exception e) {
        logger.error("Failed to Delete Book: " + title + ". Here's the exception: "  + e.toString());
    }
    
}
