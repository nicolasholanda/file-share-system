package com.fileshare.controller;

import com.fileshare.repository.FileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FileRepository fileRepository;

    @BeforeEach
    void cleanUp() {
        fileRepository.deleteAll();
    }

    @Test
    void listFiles_shouldReturnFilesView() throws Exception {
        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(view().name("files"))
                .andExpect(model().attributeExists("files"));
    }

    @Test
    void uploadFile_shouldPersistAndRedirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "hello.txt", "text/plain", "Hello World".getBytes());

        mockMvc.perform(multipart("/files/upload").file(file))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/files"));

        assertThat(fileRepository.findAll()).hasSize(1);
        assertThat(fileRepository.findAll().get(0).getOriginalName()).isEqualTo("hello.txt");
    }

    @Test
    void downloadFile_shouldReturnDecryptedContent() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "data.txt", "text/plain", "Secret content".getBytes());

        mockMvc.perform(multipart("/files/upload").file(file));
        Long id = fileRepository.findAll().get(0).getId();

        MvcResult result = mockMvc.perform(get("/files/{id}/download", id))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"data.txt\""))
                .andReturn();

        assertThat(result.getResponse().getContentAsByteArray()).isEqualTo("Secret content".getBytes());
    }

    @Test
    void deleteFile_shouldSoftDeleteAndRedirect() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "remove.txt", "text/plain", "bye".getBytes());

        mockMvc.perform(multipart("/files/upload").file(file));
        Long id = fileRepository.findAll().get(0).getId();

        mockMvc.perform(post("/files/{id}/delete", id))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/files"));

        mockMvc.perform(get("/files"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("files", org.hamcrest.Matchers.hasSize(0)));
    }

    @Test
    void searchFiles_shouldReturnMatchingResults() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file", "report-2024.pdf", "application/pdf", "pdf content".getBytes());
        mockMvc.perform(multipart("/files/upload").file(file));

        mockMvc.perform(get("/files/search").param("name", "report"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"))
                .andExpect(model().attribute("results", org.hamcrest.Matchers.hasSize(1)));
    }

    @Test
    void downloadFile_nonExistentId_shouldReturn404() throws Exception {
        mockMvc.perform(get("/files/{id}/download", 999L))
                .andExpect(status().isNotFound());
    }
}
