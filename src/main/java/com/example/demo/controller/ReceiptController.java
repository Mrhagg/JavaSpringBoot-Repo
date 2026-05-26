package com.example.demo.controller;

import com.example.demo.dto.ReceiptData;
import com.example.demo.service.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/receipt")
@RequiredArgsConstructor
public class ReceiptController {
    private final GeminiService geminiService;

    @PostMapping(value = "/scan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReceiptData> scan(@RequestParam("file") MultipartFile file) throws Exception {
        return ResponseEntity.ok(geminiService.scanReceipt(file.getBytes(), file.getContentType()));
    }
}
