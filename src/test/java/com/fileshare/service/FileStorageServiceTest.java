package com.fileshare.service;

import com.fileshare.domain.entity.FileRecord;
import com.fileshare.domain.enums.FileStatus;
import com.fileshare.exception.FileNotFoundException;
import com.fileshare.repository.FileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {

    @Mock
    private FileRepository fileRepository;

    @Mock
    private EncryptionService encryptionService;

    @InjectMocks
    private FileStorageService fileStorageService;

    @Test
    void save_shouldEncryptAndPersistWithActiveStatus() throws IOException {
        byte[] content = "file content".getBytes();
        byte[] encrypted = "encrypted".getBytes();
        MockMultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", content);

        when(encryptionService.encrypt(content)).thenReturn(encrypted);
        when(fileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        FileRecord saved = fileStorageService.save(file);

        assertThat(saved.getStatus()).isEqualTo(FileStatus.ACTIVE);
        assertThat(saved.getEncryptedContent()).isEqualTo(encrypted);
        assertThat(saved.getOriginalName()).isEqualTo("test.txt");
        verify(encryptionService).encrypt(content);
        verify(fileRepository).save(any());
    }

    @Test
    void restore_shouldDecryptAndReturnContent() {
        byte[] encrypted = "encrypted".getBytes();
        byte[] decrypted = "original".getBytes();
        FileRecord record = FileRecord.builder()
                .id(1L).status(FileStatus.ACTIVE).encryptedContent(encrypted).build();

        when(fileRepository.findById(1L)).thenReturn(Optional.of(record));
        when(encryptionService.decrypt(encrypted)).thenReturn(decrypted);

        byte[] result = fileStorageService.restore(1L);

        assertThat(result).isEqualTo(decrypted);
    }

    @Test
    void delete_shouldMarkRecordAsDeleted() {
        FileRecord record = FileRecord.builder().id(1L).status(FileStatus.ACTIVE).build();
        when(fileRepository.findById(1L)).thenReturn(Optional.of(record));
        when(fileRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        fileStorageService.delete(1L);

        ArgumentCaptor<FileRecord> captor = ArgumentCaptor.forClass(FileRecord.class);
        verify(fileRepository).save(captor.capture());
        assertThat(captor.getValue().getStatus()).isEqualTo(FileStatus.DELETED);
    }

    @Test
    void listAll_shouldReturnOnlyActiveFiles() {
        List<FileRecord> active = List.of(FileRecord.builder().status(FileStatus.ACTIVE).build());
        when(fileRepository.findByStatus(FileStatus.ACTIVE)).thenReturn(active);

        List<FileRecord> result = fileStorageService.listAll();

        assertThat(result).isEqualTo(active);
    }

    @Test
    void search_shouldDelegateToRepository() {
        List<FileRecord> found = List.of(FileRecord.builder().originalName("doc.pdf").build());
        when(fileRepository.findByOriginalNameContainingIgnoreCaseAndStatus("doc", FileStatus.ACTIVE))
                .thenReturn(found);

        List<FileRecord> result = fileStorageService.search("doc");

        assertThat(result).isEqualTo(found);
    }

    @Test
    void findActiveById_shouldThrowWhenNotFound() {
        when(fileRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> fileStorageService.findActiveById(99L))
                .isInstanceOf(FileNotFoundException.class);
    }

    @Test
    void findActiveById_shouldThrowWhenDeleted() {
        FileRecord deleted = FileRecord.builder().id(1L).status(FileStatus.DELETED).build();
        when(fileRepository.findById(1L)).thenReturn(Optional.of(deleted));

        assertThatThrownBy(() -> fileStorageService.findActiveById(1L))
                .isInstanceOf(FileNotFoundException.class);
    }
}
