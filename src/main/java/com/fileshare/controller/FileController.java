package com.fileshare.controller;

import com.fileshare.dto.FileMetadataResponse;
import com.fileshare.service.FileStorageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    @GetMapping
    public String listFiles(Model model) {
        List<FileMetadataResponse> files = fileStorageService.listAll()
                .stream()
                .map(FileMetadataResponse::from)
                .toList();
        model.addAttribute("files", files);
        return "files";
    }

    @GetMapping("/upload")
    public String uploadForm() {
        return "upload";
    }

    @PostMapping("/upload")
    public String upload(@RequestParam("file") MultipartFile file,
                         RedirectAttributes redirectAttributes) throws IOException {
        fileStorageService.save(file);
        redirectAttributes.addFlashAttribute("success", "File uploaded successfully.");
        return "redirect:/files";
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> download(@PathVariable Long id) {
        var record = fileStorageService.findActiveById(id);
        byte[] content = fileStorageService.restore(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + record.getOriginalName() + "\"")
                .contentType(MediaType.parseMediaType(record.getContentType()))
                .body(content);
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        fileStorageService.delete(id);
        redirectAttributes.addFlashAttribute("success", "File deleted successfully.");
        return "redirect:/files";
    }

    @GetMapping("/search")
    public String search(@RequestParam(required = false) String name, Model model) {
        if (name != null && !name.isBlank()) {
            List<FileMetadataResponse> results = fileStorageService.search(name)
                    .stream()
                    .map(FileMetadataResponse::from)
                    .toList();
            model.addAttribute("results", results);
            model.addAttribute("query", name);
        }
        return "search";
    }
}
