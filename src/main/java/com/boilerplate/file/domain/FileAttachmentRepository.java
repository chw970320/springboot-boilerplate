package com.boilerplate.file.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileAttachmentRepository extends JpaRepository<FileAttachment, Long> {

    /**
     * 서버에 저장된 파일명으로 파일 첨부 정보를 조회합니다.
     *
     * @param storedFileName 서버에 저장된 고유한 파일명
     * @return Optional<FileAttachment> 파일 첨부 정보
     */
    Optional<FileAttachment> findByStoredFileName(String storedFileName);
}
