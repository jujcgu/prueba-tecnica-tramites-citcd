package com.citcd.demo.storage;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileSystemStorageService implements StorageService {

	private final Path rootLocation;

	@Autowired
	public FileSystemStorageService(StorageProperties properties) {
		if (properties.getLocation().trim().isEmpty()) {
			throw new StorageException("File upload location can not be Empty.");
		}
		this.rootLocation = Paths.get(properties.getLocation());
	}

	@Override
	public void store(MultipartFile file) {
		try {
			if (file == null || file.isEmpty())
				throw new StorageException("Failed to store empty file.");

			String originalName = file.getOriginalFilename() == null ? "file" : file.getOriginalFilename();
			Path destinationFile = this.rootLocation.resolve(Paths.get(originalName)).normalize().toAbsolutePath();

			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Cannot store file outside current directory.");
			}

			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (IOException e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public StoredFileInfo storeAndGetInfo(MultipartFile file) {
		try {
			if (file == null || file.isEmpty())
				throw new StorageException("Failed to store empty file.");

			String original = StringUtils
					.cleanPath(file.getOriginalFilename() == null ? "file" : file.getOriginalFilename());
			String ext = StringUtils.getFilenameExtension(original);
			String key = UUID.randomUUID() + (ext == null || ext.isBlank() ? "" : "." + ext.toLowerCase());

			Path destinationFile = this.rootLocation.resolve(key).normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageException("Cannot store file outside current directory.");
			}

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			try (InputStream is = file.getInputStream(); DigestInputStream dis = new DigestInputStream(is, md)) {
				Files.copy(dis, destinationFile); // no overwrite
			}

			String sha256 = toHex(md.digest());
			String mime = normalizeMime(file.getContentType(), destinationFile, original);

			return new StoredFileInfo(key, original, file.getSize(), mime, sha256);

		} catch (FileAlreadyExistsException e) {
			return storeAndGetInfo(file);
		} catch (Exception e) {
			throw new StorageException("Failed to store file.", e);
		}
	}

	@Override
	public StoredFileInfo getInfo(String storageKey) {
		try {
			Path p = load(storageKey).normalize().toAbsolutePath();
			if (!p.getParent().equals(this.rootLocation.toAbsolutePath())) {
				throw new StorageFileNotFoundException("Invalid storageKey: " + storageKey);
			}
			if (!Files.exists(p)) {
				throw new StorageFileNotFoundException("File not found: " + storageKey);
			}

			long size = Files.size(p);
			String mime = normalizeMime(null, p, storageKey);

			MessageDigest md = MessageDigest.getInstance("SHA-256");
			try (InputStream is = Files.newInputStream(p); DigestInputStream dis = new DigestInputStream(is, md)) {
				dis.transferTo(java.io.OutputStream.nullOutputStream());
			}
			String sha256 = toHex(md.digest());

			return new StoredFileInfo(storageKey, storageKey, size, mime, sha256);

		} catch (IOException e) {
			throw new StorageException("Failed to read stored file info: " + storageKey, e);
		} catch (Exception e) {
			throw new StorageException("Failed to compute stored file info: " + storageKey, e);
		}
	}

	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1).filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		} catch (IOException e) {
			throw new StorageException("Failed to read stored files", e);
		}
	}

	@Override
	public Path load(String filename) {
		return rootLocation.resolve(filename);
	}

	@Override
	public Resource loadAsResource(String filename) {
		try {
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable())
				return resource;
			throw new StorageFileNotFoundException("Could not read file: " + filename);
		} catch (MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocation);
		} catch (IOException e) {
			throw new StorageException("Could not initialize storage", e);
		}
	}

	private static String normalizeMime(String requestMime, Path file, String nameFallback) {
		String mime = (requestMime == null) ? "" : requestMime.trim();
		if (mime.isBlank() || "application/octet-stream".equalsIgnoreCase(mime)) {
			try {
				String probed = Files.probeContentType(file);
				if (probed != null && !probed.isBlank())
					return probed;
			} catch (Exception ignored) {
			}
			String guessed = java.net.URLConnection.guessContentTypeFromName(nameFallback);
			return (guessed == null || guessed.isBlank()) ? "application/octet-stream" : guessed;
		}
		return mime;
	}

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes)
			sb.append(String.format("%02x", b));
		return sb.toString();
	}
}
