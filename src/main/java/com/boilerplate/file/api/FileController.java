package com.boilerplate.file.api;

import com.boilerplate.core.common.ApiResponse;
import com.boilerplate.file.domain.FileAttachment;
import com.boilerplate.file.dto.FileUploadResponse;
import com.boilerplate.file.service.FileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "File", description = "파일 업로드/다운로드 API")
public class FileController {

    private final FileStorageService fileStorageService;

    @PostMapping("/upload")
    @Operation(summary = "파일 업로드", description = "단일 파일을 서버에 업로드합니다.")
    public ResponseEntity<ApiResponse<FileUploadResponse>> uploadFile(@RequestParam("file") MultipartFile file) {
        FileAttachment attachment = fileStorageService.storeFile(file);

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/files/download/")
                .path(attachment.getStoredFileName())
                .toUriString();

        FileUploadResponse response = new FileUploadResponse(
                attachment.getStoredFileName(),
                attachment.getFileName(),
                fileDownloadUri,
                attachment.getFileType(),
                attachment.getFileSize()
        );

        return ResponseEntity.ok(ApiResponse.success("File uploaded successfully", response));
    }

    @GetMapping("/download/{fileName:.+}")
    @Operation(summary = "파일 다운로드", description = "저장된 파일을 다운로드합니다.")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName, HttpServletRequest request) {
        FileAttachment attachment = fileStorageService.getFile(fileName);
        Resource resource = fileStorageService.loadFileAsResource(attachment.getStoredFileName());

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        String encodedFileName = URLEncoder.encode(attachment.getFileName(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}
