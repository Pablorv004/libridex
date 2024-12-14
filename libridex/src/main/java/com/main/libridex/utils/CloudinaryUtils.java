package com.main.libridex.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import io.github.cdimascio.dotenv.Dotenv;

public class CloudinaryUtils {

    private static Cloudinary instance;

    private CloudinaryUtils() {
    }

    public static Cloudinary getInstance() {
        if (instance == null) {
            if (instance == null) {
                Dotenv dotenv = Dotenv.load();
                String cloudinaryUrl = dotenv.get("CLOUDINARY_URL");

                if (cloudinaryUrl == null || cloudinaryUrl.isEmpty()) {
                    throw new IllegalArgumentException("CLOUDINARY_URL is not set in the .env file");
                }

                instance = new Cloudinary(cloudinaryUrl);
                System.out.println("[CLOUDINARY] Initialized with cloud: " + instance.config.cloudName);
            }
        }

        return instance;
    }

    /**
     * Uploads an image to Cloudinary and returns the secure URL of the uploaded image.
     *
     * @param multipartFile the image file to be uploaded
     * @param id the identifier used to generate the filename
     * @param type the type of the entity (e.g., "User", "Book") to determine the filename
     * @return the secure URL of the uploaded image
     */
    public static String uploadImage(MultipartFile multipartFile, int id, String type) {
        Cloudinary cloudinary = getInstance();
        String filename = determineFilename(id, type, multipartFile);

        File file = new File(filename);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        String imageUrl = "";
        try {
            Map params1 = ObjectUtils.asMap(
                    "use_filename", true,
                    "unique_filename", false,
                    "overwrite", true,
                    "invalidate", true,
                    "public_id", filename);

            imageUrl = cloudinary.uploader().upload(file, params1).get("secure_url").toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (file.exists()) {
                file.delete();
            }
        }

        return imageUrl;
    }

    /**
     * Extracts the public ID from a Cloudinary URL.
     *
     * @param url the URL of the image
     * @return the public ID of the image
     */
    private static String getPublicId(String url) {
        String[] parts = url.split("/");
        String fileNameWithExtension = parts[parts.length - 1];
        String fileNameWithoutExtension = fileNameWithExtension.substring(0, fileNameWithExtension.lastIndexOf('.'));

        return fileNameWithoutExtension;
    }

    /**
     * Deletes an image from Cloudinary based on the provided URL.
     *
     * @param url the URL of the image to be deleted
     */
    public static void deleteImage(String url) {
        String publicId = getPublicId(url);
        if (!publicId.equals("default_image.png")) {
            Cloudinary cloudinary = CloudinaryUtils.getInstance();

            try {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determines the file name based on the provided id, type, and file.
     *
     * @param id the identifier used to generate the filename
     * @param type the type of the entity (e.g., "User", "Book") to determine the filename
     * @param multipartFile the image file to be uploaded
     * @return the generated filename
     */
    private static String determineFilename(int id, String type, MultipartFile multipartFile) {
        // Then we set the name
        String newFilename = "";
        switch (type) {
            case "User": {
                newFilename = "user_image_" + id;
                break;
            }
            case "Book": {
                newFilename = "book_image_" + id;
                break;
            }
            default: {
                newFilename = "default_image";
                break;
            }
        }

        return newFilename;
    }
}
