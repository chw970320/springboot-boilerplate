package com.boilerplate.file.domain;

import com.boilerplate.common.AbstractContainerTest;
import com.boilerplate.config.TestJpaConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Import(TestJpaConfig.class)
class FileAttachmentRepositoryTest extends AbstractContainerTest { // AbstractContainerTest 상속

    @Autowired
    private FileAttachmentRepository fileAttachmentRepository;

    @BeforeEach
    void setUp() {
        fileAttachmentRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("파일 첨부 정보 저장 테스트")
    void testSaveFileAttachment() {
        // Given
        FileAttachment attachment = FileAttachment.builder()
                .fileName("test_file.txt")
                .storedFileName("unique_test_file_name.txt") // storedFileName 사용
                .uploadDir("/uploads") // uploadDir 사용
                .fileSize(1024L)
                .fileType("text/plain")
                .extension("txt") // extension 필드 추가
                .build();

        // When
        FileAttachment saved = fileAttachmentRepository.save(attachment);

        // Then
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getFileName()).isEqualTo("test_file.txt");
        assertThat(saved.getStoredFileName()).isEqualTo("unique_test_file_name.txt");
    }

    @Test
    @DisplayName("저장된 파일명으로 파일 첨부 정보 조회 테스트")
    void testFindByStoredFileName() {
        // Given
        FileAttachment attachment = FileAttachment.builder()
                .fileName("find_me.pdf")
                .storedFileName("unique_find_me_name.pdf") // storedFileName 사용
                .uploadDir("/uploads") // uploadDir 사용
                .fileSize(2048L)
                .fileType("application/pdf")
                .extension("pdf") // extension 필드 추가
                .build();
        fileAttachmentRepository.save(attachment);

        // When
        Optional<FileAttachment> found = fileAttachmentRepository.findByStoredFileName("unique_find_me_name.pdf"); // findByStoredFileName 사용

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFileName()).isEqualTo("find_me.pdf");
        assertThat(found.get().getStoredFileName()).isEqualTo("unique_find_me_name.pdf");
    }

    @Test
    @DisplayName("존재하지 않는 파일명으로 파일 첨부 정보 조회 시 빈 Optional 반환 테스트")
    void testFindByStoredFileNameNotFound() {
        // When
        Optional<FileAttachment> found = fileAttachmentRepository.findByStoredFileName("non_existent_unique_name.jpg"); // findByStoredFileName 사용

        // Then
        assertThat(found).isEmpty();
    }
}
