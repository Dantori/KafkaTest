package ru.trofimov.ws.product_microservice.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.trofimov.ws.product_microservice.service.ProductService;
import ru.trofimov.ws.product_microservice.service.dto.CreateProductDto;

import java.util.Date;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/sync")
    public ResponseEntity<String> createSynchronously(@RequestBody CreateProductDto createProductDto) {
        String productId = productService.createProductSynchronously(createProductDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @PostMapping("/async")
    public ResponseEntity<Object> createAsynchronously(@RequestBody CreateProductDto createProductDto) {
        String productId;
        try {
            productId = productService.createProductAsynchronously(createProductDto);
        } catch (ExecutionException | InterruptedException e) {
            LOGGER.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorMessage(new Date(), e.getMessage()));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
