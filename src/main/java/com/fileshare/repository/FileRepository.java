package com.fileshare.repository;

import com.fileshare.domain.entity.FileRecord;
import com.fileshare.domain.enums.FileStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FileRepository extends JpaRepository<FileRecord, Long> {

    List<FileRecord> findByStatus(FileStatus status);

    List<FileRecord> findByOriginalNameContainingIgnoreCaseAndStatus(String name, FileStatus status);
}
