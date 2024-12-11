package com.main.libridex.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.main.libridex.config.StorageProperties;
import com.main.libridex.exceptions.StorageException;
import com.main.libridex.exceptions.StorageFileNotFoundException;
import com.main.libridex.service.StorageService;

@Service("storageService")
public class StorageServiceImpl implements StorageService {

	private final Path rootLocation;

	public StorageServiceImpl(StorageProperties properties) {
		if (properties.getLocation().trim().length() == 0) {
			throw new StorageException("File upload location can not be Empty.");
		}
		this.rootLocation = Paths.get(properties.getLocation());
	}

	/**
	 * Stores a file in the specified directory based on the type parameter.
	 * The file is saved with a name based on the provided ID and its original
	 * extension.
	 *
	 * @param file the file to store
	 * @param id   the identifier to use in the file name
	 * @param type the type of the file (e.g., "User" or "Book") to determine the
	 *             subdirectory
	 */
	@Override
	public void store(MultipartFile file, int id, String type) {
		try {
			if (file.isEmpty()) {
				throw new StorageException("Failed to store empty file.");
			}

			// First we obtain the extension of the file
			String originalFilename = file.getOriginalFilename();
			String extension = "";
			if (originalFilename != null && originalFilename.contains(".")) {
				extension = originalFilename.substring(originalFilename.lastIndexOf("."));
			}

			// Then we obtain the complete route of the file, it'll be the absolute path
			// including
			// the name of the file
			Path destinationFile = determineFilePathAndName(id, type, extension);

			if (!destinationFile.getParent()
					.equals(this.rootLocation.resolve(type.toLowerCase()).normalize().toAbsolutePath())) {
				throw new StorageException("Cannot store file outside current directory.");
			}

			// If the directory does not exist, we create it
			if (!Files.exists(destinationFile.getParent())) {
				Files.createDirectories(destinationFile.getParent());
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	/**
	 * Determines the file path and name based on the provided id, type, and
	 * extension.
	 *
	 * @param id        the identifier used to generate the filename
	 * @param type      the type of the entity (e.g., "User", "Book") to determine
	 *                  the subdirectory and filename pattern
	 * @param extension the file extension to be appended to the filename
	 * @return the complete file path including the directory and the generated
	 *         filename
	 */
	private Path determineFilePathAndName(int id, String type, String extension) {
		String newFilename = "";
		String subDir = "";
		switch (type) {
			case "User": {
				newFilename = "user_image_" + id + extension;
				subDir = "users";
				break;
			}
			case "Book": {
				newFilename = "book_image_" + id + extension;
				subDir = "books";
				break;
			}
			default: {
				newFilename = "default_image.png";
				subDir = "default";
				break;
			}
		}

		Path destinationDir = this.rootLocation.resolve(subDir).normalize().toAbsolutePath();
		return destinationDir.resolve(newFilename).normalize().toAbsolutePath();
	}

	/**
	 * Loads all files from the storage location.
	 *
	 * @return a stream of paths representing all files in the storage location
	 */
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation)
					.filter(path -> !path.equals(this.rootLocation) && !Files.isDirectory(path))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	/**
	 * Loads all user images from the storage location.
	 *
	 * @return a stream of paths representing all user images in the storage
	 *         location
	 */
	public Stream<Path> loadAllUserImages() {
		try {
			Path userDir = this.rootLocation.resolve("users").normalize().toAbsolutePath();
			return Files.walk(userDir, 1)
					.filter(path -> !path.equals(userDir) && !Files.isDirectory(path))
					.map(userDir::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read user images", e);
		}
	}

	/**
	 * Loads all book images from the storage location.
	 *
	 * @return a stream of paths representing all book images in the storage
	 *         location
	 */
	public Stream<Path> loadAllBookImages() {
		try {
			Path bookDir = this.rootLocation.resolve("books").normalize().toAbsolutePath();
			return Files.walk(bookDir, 1)
					.filter(path -> !path.equals(bookDir) && !Files.isDirectory(path))
					.map(bookDir::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read book images", e);
		}
	}

	/**
	 * Loads a specific file by its filename.
	 *
	 * @param filename the name of the file to load
	 * @return the path to the file
	 */
	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	/**
	 * Loads a specific file as a resource by its filename.
	 *
	 * @param filename the name of the file to load
	 * @return the resource representing the file
	 */
	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			} else {
				throw new StorageFileNotFoundException("Could not read file: " + filename);
			}
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	/**
	 * Deletes a specific file by its filename.
	 *
	 * @param filename the name of the file to delete
	 */
	@Override
	public void delete(String filename) {
		try {
			Path file = load(filename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new StorageException("Failed to delete file: " + filename, e);
		}
	}

	/**
	 * Deletes all files from the storage location.
	 */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	/**
	 * Initializes the storage location by creating the root directory if it does
	 * not exist.
	 */
	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}
}