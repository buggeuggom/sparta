package com.sparta.msa.lesson.domain.vector.controller;

import com.sparta.msa.lesson.domain.vector.dto.request.SimilaritySearchRequest;
import com.sparta.msa.lesson.domain.vector.dto.response.DocumentUploadResponse;
import com.sparta.msa.lesson.domain.vector.dto.response.SimilaritySearchResponse;
import com.sparta.msa.lesson.domain.vector.service.VectorDocumentService;
import com.sparta.msa.lesson.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vector-document")
public class VectorDocumentController {

    private final VectorDocumentService vectorDocumentService;

    // 1. 파일 업로드 및 벡터화
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<DocumentUploadResponse> uploadDocument(
            @RequestPart("file") MultipartFile file) throws IOException {
        return ApiResponse.ok(vectorDocumentService.uploadDocument(file));
    }

    // 2. 특정 문서 내에서 유사도 검색
    @GetMapping("/similarity")
    public ApiResponse<SimilaritySearchResponse> searchInDocument(SimilaritySearchRequest request) {
        return ApiResponse.ok(
                vectorDocumentService.similaritySearchByDocument(request.getDocumentId(),
                        request.getQuery(), request.getTopK()));
    }

    // 3. 문서 삭제 (DB & Vector Store) 서비스의 deleteDocument()와 대응됩니다.
    @DeleteMapping("/{documentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ApiResponse<Void> deleteDocument(@PathVariable UUID documentId) {
        vectorDocumentService.deleteDocument(documentId);
        return ApiResponse.ok();
    }
}