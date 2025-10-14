package com.boilerplate.file.service;

import com.boilerplate.file.domain.FileAttachment;
import com.boilerplate.file.domain.FileAttachmentRepository;
import com.boilerplate.file.exception.FileStorageException;
import com.boilerplate.file.exception.InvalidFileExtensionException;
import com.boilerplate.file.exception.MyFileNotFoundException;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileAttachmentRepository fileAttachmentRepository;

    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;

    @Value("${file.allowed-extensions:}")
    private List<String> allowedExtensions;

    private Path fileStorageLocation;

    @PostConstruct
    public void init() {
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public FileAttachment storeFile(MultipartFile file) {
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        String fileExtension = getFileExtension(originalFileName);

        if (originalFileName.contains("..")) {
            throw new FileStorageException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }

        if (!CollectionUtils.isEmpty(allowedExtensions) && !allowedExtensions.contains(fileExtension.toLowerCase())) {
            throw new InvalidFileExtensionException("File with extension ." + fileExtension + " is not allowed. Allowed extensions are: " + String.join(", ", allowedExtensions));
        }

        String storedFileName = UUID.randomUUID().toString() + "_" + originalFileName;

        try {
            Path targetLocation = this.fileStorageLocation.resolve(storedFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            FileAttachment fileAttachment = FileAttachment.builder()
                    .fileName(originalFileName)
                    .storedFileName(storedFileName)
                    .fileType(file.getContentType())
                    .extension(fileExtension)
                    .fileSize(file.getSize())
                    .uploadDir(this.fileStorageLocation.toString())
                    .build();

            return fileAttachmentRepository.save(fileAttachment);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFileName + ". Please try again!", ex);
        }
    }

    @Transactional(readOnly = true)
    public FileAttachment getFile(String storedFileName) {
        return fileAttachmentRepository.findByStoredFileName(storedFileName)
                .orElseThrow(() -> new MyFileNotFoundException("File not found with name: " + storedFileName));
    }

    public Resource loadFileAsResource(String storedFileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(storedFileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + storedFileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + storedFileName, ex);
        }
    }

    private String getFileExtension(String fileName) {
        return Optional.ofNullable(fileName)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(fileName.lastIndexOf(".") + 1))
                .orElse("");
    }
}
