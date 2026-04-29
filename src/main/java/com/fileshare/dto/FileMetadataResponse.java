package com.fileshare.dto;

import com.fileshare.domain.entity.FileRecord;
import com.fileshare.domain.enums.FileStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class FileMetadataResponse {

    private Long id;
    private String originalName;
    private String contentType;
    private Long size;
    private FileStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static FileMetadataResponse from(FileRecord record) {
        return FileMetadataResponse.builder()
                .id(record.getId())
                .originalName(record.getOriginalName())
                .contentType(record.getContentType())
                .size(record.getSize())
                .status(record.getStatus())
                .createdAt(record.getCreatedAt())
                .updatedAt(record.getUpdatedAt())
                .build();
    }
}
