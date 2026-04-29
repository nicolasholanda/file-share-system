package com.fileshare.service;

import com.fileshare.domain.entity.FileRecord;
import com.fileshare.domain.enums.FileStatus;
import com.fileshare.repository.FileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FileStorageService {

    private final FileRepository fileRepository;
    private final EncryptionService encryptionService;

    public FileRecord save(MultipartFile file) throws IOException {
        byte[] encrypted = encryptionService.encrypt(file.getBytes());

        FileRecord record = FileRecord.builder()
                .originalName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .size(file.getSize())
                .encryptedContent(encrypted)
                .status(FileStatus.ACTIVE)
                .build();

        return fileRepository.save(record);
    }

    public byte[] restore(Long id) {
        FileRecord record = findActiveById(id);
        return encryptionService.decrypt(record.getEncryptedContent());
    }

    public void delete(Long id) {
        FileRecord record = findActiveById(id);
        record.setStatus(FileStatus.DELETED);
        fileRepository.save(record);
    }

    public List<FileRecord> listAll() {
        return fileRepository.findByStatus(FileStatus.ACTIVE);
    }

    public List<FileRecord> search(String name) {
        return fileRepository.findByOriginalNameContainingIgnoreCaseAndStatus(name, FileStatus.ACTIVE);
    }

    public FileRecord findActiveById(Long id) {
        return fileRepository.findById(id)
                .filter(r -> r.getStatus() == FileStatus.ACTIVE)
                .orElseThrow(() -> new com.fileshare.exception.FileNotFoundException(id));
    }
}
