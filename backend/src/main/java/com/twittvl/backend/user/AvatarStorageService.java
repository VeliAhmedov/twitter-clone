package com.twittvl.backend.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private final static Map<String, String> IMAGE_TYPES = Map.of(
            "image/jpeg", "jpg",
            "image/png", "png",
            "image/gif", "gif"
    );
    private final Path directory;

    public AvatarStorageService(
            @Value("${app.upload-dir:uploads/avatars}") String uploadDirectory) {
        directory = Paths.get(uploadDirectory).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("image is required");
        }
        if (file.getSize() > 5 * 1024 * 1024){
            throw new IllegalArgumentException("image size must not exceed 5MB");
        }

        String type = file.getContentType();

        if (!IMAGE_TYPES.containsKey(type)) {
            throw new IllegalArgumentException("Only JPEG, PNG, and GIF images can be stored");
        }

        Files.createDirectories(directory);

        String fileName = UUID.randomUUID() + "." + IMAGE_TYPES.get(type);
        file.transferTo(directory.resolve(fileName));
        return fileName;
    }

}
