package com.ecommerce.backend.service;

import com.ecommerce.backend.FileUploadComponent;
import com.ecommerce.backend.domain.entity.ProductImage;
import com.ecommerce.backend.domain.request.ProductImageRequest;
import com.ecommerce.backend.repository.ProductImageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductImageService {
    @Value("${productImageLocation}")
    private String productImageLocation;

    private final FileUploadComponent fileUploadComponent;
    private final ProductImageRepository productImageRepository;

    public void saveProductImage(ProductImageRequest.CreateRequest request, MultipartFile multipartFile) throws IOException {
        final String originalFilename = multipartFile.getOriginalFilename(); // cat.jpg

        // 파일 업로드
        if(StringUtils.hasText(originalFilename)){
            fileUploadComponent.uploadFile(productImageLocation, originalFilename, multipartFile.getBytes());

            final String copyFileName = fileUploadComponent.makeCopyFileName(originalFilename);
            final String fileUploadAbsPath = fileUploadComponent.makeAbsPath(productImageLocation, copyFileName);
            final ProductImage productImage = request.toProductImage();

            productImage.updateProductImage(originalFilename, copyFileName, fileUploadAbsPath);
            productImageRepository.save(productImage);
        }
    }
}
