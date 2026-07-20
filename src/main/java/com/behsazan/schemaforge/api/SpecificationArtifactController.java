package com.behsazan.schemaforge.api;

import com.behsazan.schemaforge.application.ArtifactGenerationService;
import com.behsazan.schemaforge.application.GeneratedZip;
import java.io.IOException;
import java.util.Locale;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/specification")
public class SpecificationArtifactController {
    private static final MediaType ZIP = MediaType.parseMediaType("application/zip");

    private final ArtifactGenerationService service;

    public SpecificationArtifactController(ArtifactGenerationService service) {
        this.service = service;
    }

    @PostMapping(value = "/word", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/zip")
    public ResponseEntity<byte[]> generateWord(@RequestParam("file") MultipartFile file) throws IOException {
        requireFile(file, ".docx");
        GeneratedZip result = service.generateFromWord(file.getOriginalFilename(), file.getInputStream());
        return response(result);
    }

    @PostMapping(value = "/zip", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/zip")
    public ResponseEntity<byte[]> generateZip(@RequestParam("file") MultipartFile file) throws IOException {
        requireFile(file, ".zip");
        GeneratedZip result = service.generateFromZip(file.getOriginalFilename(), file.getInputStream());
        return response(result);
    }

    private void requireFile(MultipartFile file, String extension) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Input file must not be empty");
        }
        String name = file.getOriginalFilename();
        if (name == null || !name.toLowerCase(Locale.ROOT).endsWith(extension)) {
            throw new IllegalArgumentException("Expected a " + extension + " file");
        }
    }

    private ResponseEntity<byte[]> response(GeneratedZip result) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(ZIP);
        headers.setContentDisposition(ContentDisposition.attachment().filename(result.fileName()).build());
        headers.setContentLength(result.content().length);
        return ResponseEntity.ok().headers(headers).body(result.content());
    }
}
