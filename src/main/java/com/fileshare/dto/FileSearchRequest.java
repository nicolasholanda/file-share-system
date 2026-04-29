package com.fileshare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class FileSearchRequest {

    @NotBlank
    @Size(min = 1, max = 255)
    private String name;
}
