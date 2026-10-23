
package com.twittvl.backend.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/uploads/avatars")
public class AvatarController {

    private final Path directory;

    public AvatarController(
            @Value("${app.upload-dir:uploads/avatars}") String uploadDir) {
        directory = Paths.get(uploadDir).toAbsolutePath().normalize();
    }

    @GetMapping("/{filename:.+}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename)
            throws Exception {

        if (!filename.matches("[a-fA-F0-9-]+\\.(jpg|jpeg|png|gif)")) {
            return ResponseEntity.badRequest().build();
        }

        Path file = directory.resolve(filename).normalize();

        if (!file.startsWith(directory) || !file.toFile().isFile()) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new UrlResource(file.toUri());

        String type = switch (
                filename.substring(filename.lastIndexOf('.') + 1).toLowerCase()) {
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "jpg", "jpeg" -> "image/jpeg";
            default -> throw new IllegalArgumentException("Unsupported image type");
        };

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(type))
                .header("X-Content-Type-Options", "nosniff")
                .body(resource);
    }
}
