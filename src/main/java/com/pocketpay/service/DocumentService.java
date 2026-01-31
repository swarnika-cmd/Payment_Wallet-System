package com.pocketpay.service;

import com.pocketpay.entity.Document;
import com.pocketpay.entity.User;
import com.pocketpay.enums.DocumentType;
import com.pocketpay.repository.DocumentRepository;
import com.pocketpay.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentService {

    private final Path fileStorageLocation;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentService(@Value("${file.upload-dir}") String uploadDir,
            DocumentRepository documentRepository,
            UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @Transactional
    public Document uploadDocument(String mobileNumber, MultipartFile file, DocumentType type) {
        // 1. Validate User
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2. Normalize Filename
        String originalFileName = StringUtils.cleanPath(file.getOriginalFilename());
        if (originalFileName.contains("..")) {
            throw new RuntimeException("Sorry! Filename contains invalid path sequence " + originalFileName);
        }

        // 3. Generate Unique Filename to avoid collisions
        String fileExtension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            fileExtension = originalFileName.substring(i);
        }
        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;

        try {
            // 4. Save File to Disk
            Path targetLocation = this.fileStorageLocation.resolve(uniqueFileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            // 5. Save Metadata to DB
            Document document = new Document(
                    user,
                    type,
                    uniqueFileName, // Store unique name on disk
                    targetLocation.toString(),
                    file.getContentType(),
                    file.getSize());

            return documentRepository.save(document);

        } catch (IOException ex) {
            throw new RuntimeException("Could not store file " + uniqueFileName + ". Please try again!", ex);
        }
    }

    public Resource loadFileAsResource(String filename) {
        try {
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("File not found " + filename);
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("File not found " + filename, ex);
        }
    }

    public Document getDocument(Long documentId, String mobileNumber) {
        Document doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document not found"));

        // Security Check: Does this doc belong to this user?
        if (!doc.getUser().getMobileNumber().equals(mobileNumber)) {
            throw new RuntimeException("Unauthorized to access this document");
        }
        return doc;
    }

    public List<Document> getMyDocuments(String mobileNumber) {
        User user = userRepository.findByMobileNumber(mobileNumber)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return documentRepository.findByUserId(user.getId());
    }
}
