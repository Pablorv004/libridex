package com.main.libridex.components.logger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

@Component("userLogger")
public class UserLogger {
    private static final Log logger = LogFactory.getLog(UserLogger.class);

    public void registered(String email) {
        logger.info("Successfully Registered: " + email);
    }

    public void failedToRegister(String email, Exception e) {
        logger.error("Failed to Register: " + email + ". Here's the exception: "  + e.toString());
    }

    public void loggedIn(String email) {
        logger.info("Successfully Logged In: " + email);
    }

    public void failedToLogIn(String email, Exception e) {
        logger.error("Failed to Log In: " + email + ". Here's the exception: "  + e.toString());
    }

    public void loggedOut(String email) {
        logger.info("Successfully Logged Out: " + email);
    }

    public void failedToLogOut(String email, Exception e) {
        logger.error("Failed to Log Out: " + email + ". Here's the exception: "  + e.toString());
    }

    public void updatedProfile(String email) {
        logger.info("Successfully Updated Profile: " + email);
    }

    public void failedToUpdateProfile(String email, Exception e) {
        logger.error("Failed to Update Profile: " + email + ". Here's the exception: "  + e.toString());
    }

    public void deleted(String email) {
        logger.info("Successfully Deleted User: " + email);
    }

    public void failedToDelete(String email, Exception e) {
        logger.error("Failed to Delete: " + email + ". Here's the exception: "  + e.toString());
    }

    public void changedStatus(String email) {
        logger.info("Successfully Changed Activation Status of User: " + email);
    }

    public void failedToChangeStatus(String email, Exception e) {
        logger.error("Failed to Change Status of User: " + email + ". Here's the exception: "  + e.toString());
    }
}
