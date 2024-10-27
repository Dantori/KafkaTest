package ru.trofimov.ws.product_microservice.service;

import ru.trofimov.ws.product_microservice.service.dto.CreateProductDto;

import java.util.concurrent.ExecutionException;

public interface ProductService {

    String createProductSynchronously(CreateProductDto createProductDto);
    String createProductAsynchronously(CreateProductDto createProductDto) throws ExecutionException, InterruptedException;
}
