package com.pocketpay.controller;

import com.pocketpay.entity.Document;
import com.pocketpay.enums.DocumentType;
import com.pocketpay.service.DocumentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaTypeFactory;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadDocument(
            @Parameter(hidden = true) Authentication authentication,
            @RequestParam("file") MultipartFile file,
            @RequestParam("type") DocumentType type) {

        Document doc = documentService.uploadDocument(authentication.getName(), file, type);

        return ResponseEntity.ok("File uploaded successfully. ID: " + doc.getId());
    }

    @GetMapping
    public ResponseEntity<List<Document>> getMyDocuments(
            @Parameter(hidden = true) Authentication authentication) {

        return ResponseEntity.ok(documentService.getMyDocuments(authentication.getName()));
    }

    @GetMapping("/download/{documentId}")
    public ResponseEntity<Resource> downloadDocument(
            @Parameter(hidden = true) Authentication authentication,
            @PathVariable Long documentId) {

        Document doc = documentService.getDocument(documentId, authentication.getName());
        Resource resource = documentService.loadFileAsResource(doc.getFilename());

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(resource).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + doc.getFilename() + "\"")
                .body(resource);
    }
}
