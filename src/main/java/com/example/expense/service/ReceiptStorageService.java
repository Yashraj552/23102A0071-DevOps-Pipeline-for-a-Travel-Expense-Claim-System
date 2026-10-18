package com.example.expense.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

/** Checks and stores receipt files on disk. Files are saved under a generated name. */
@Service
public class ReceiptStorageService {

    static final long MAX_SIZE_BYTES = 2L * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("pdf", "jpg", "jpeg", "png");

    private final Path uploadDir;

    public ReceiptStorageService(@Value("${app.upload-dir:uploads}") String uploadDir) {
        this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    /** Returns an error message, or null when the receipt is acceptable. */
    public String validate(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return "Receipt is required";
        }
        if (file.getSize() > MAX_SIZE_BYTES) {
            return "Receipt must be 2 MB or smaller";
        }
        if (!ALLOWED_EXTENSIONS.contains(extensionOf(file.getOriginalFilename()))) {
            return "Receipt must be a PDF, JPG or PNG file";
        }
        return null;
    }

    public String store(MultipartFile file) throws IOException {
        Files.createDirectories(uploadDir);
        String name = UUID.randomUUID() + "." + extensionOf(file.getOriginalFilename());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, uploadDir.resolve(name), StandardCopyOption.REPLACE_EXISTING);
        }
        return name;
    }

    public Resource load(String name) {
        try {
            Path file = uploadDir.resolve(name).normalize();
            if (file.startsWith(uploadDir)) {
                Resource resource = new UrlResource(file.toUri());
                if (resource.exists() && resource.isReadable()) {
                    return resource;
                }
            }
        } catch (MalformedURLException e) {
            // fall through to not found
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Receipt not found");
    }

    private static String extensionOf(String filename) {
        if (filename == null) {
            return "";
        }
        int dot = filename.lastIndexOf('.');
        return dot < 0 ? "" : filename.substring(dot + 1).toLowerCase(Locale.ROOT);
    }
}