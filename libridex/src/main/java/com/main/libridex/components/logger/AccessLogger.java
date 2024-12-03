package com.main.libridex.components.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("accessLogger")
public class AccessLogger {
    private static final Log logger = LogFactory.getLog(AccessLogger.class);

    public void accessed(String where) {
        logger.info("Successfully Accessed: " + where);
    }

    public void left(String where) {
        logger.info("Successfully Left: " + where);
    }

    public void failed(String where, Exception e) {
        logger.error("Failed to Access: " + where + ". Here's the exception: "  + e.toString());
    }

    public void redirected(String where) {
        logger.info("Successfully Redirected: " + where);
    }


    
    
}
