package com.twittvl.backend.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.UUID;

@Service
public class AvatarStorageService {

    private final static Set<String> imageTypes = Set.of("image/jpeg", "image/png", "image/gif");
    private final Path directory;

    public AvatarStorageService(
            @Value("${app.upload-dir:uploads/avatars}") String uploadDir) {
        directory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    public String store(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty() || file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("image can't exceed 5MB");
        }

        String type = file.getContentType();
        if (!imageTypes.contains(type)) {
            throw new IllegalArgumentException("Only JPEG, PNG images can be stored");
        }

        String extension = switch (type){
            case "image/jpeg" -> "jpg";
            case "image/png" -> "png";
            case "image/gif" -> "gif";
            default -> throw new IllegalArgumentException("Unsupported image type");
        };

        Files.createDirectories(directory);

        String fileName = UUID.randomUUID() + extension;
        file.transferTo(directory.resolve(fileName));
        return fileName;
    }

}
