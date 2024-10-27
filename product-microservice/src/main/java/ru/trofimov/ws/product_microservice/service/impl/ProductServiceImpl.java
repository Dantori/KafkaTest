package ru.trofimov.ws.product_microservice.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import ru.trofimov.ws.core.ProductCreatedEvent;
import ru.trofimov.ws.product_microservice.service.ProductService;
import ru.trofimov.ws.product_microservice.service.dto.CreateProductDto;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class ProductServiceImpl implements ProductService {

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;
    private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());

    public ProductServiceImpl(KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public String createProductSynchronously(CreateProductDto createProductDto) {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                createProductDto.getTitle(),
                createProductDto.getPrice(),
                createProductDto.getQuantity());

        CompletableFuture<SendResult<String, ProductCreatedEvent>> future = kafkaTemplate.send(
                "product-created-events-topic",
                productId,
                productCreatedEvent);

        future.whenComplete(((result, exception) -> {
            if (exception != null) {
                LOGGER.error("Failed to sent message: {}", exception.getMessage());
            } else {
                LOGGER.info("Message sent successfully: {}", result.getRecordMetadata());
            }
        }));

        LOGGER.info("Return: {}", productId);

        return productId;
    }

    @Override
    public String createProductAsynchronously(CreateProductDto createProductDto) throws ExecutionException,
            InterruptedException {
        String productId = UUID.randomUUID().toString();

        ProductCreatedEvent productCreatedEvent = new ProductCreatedEvent(
                productId,
                createProductDto.getTitle(),
                createProductDto.getPrice(),
                createProductDto.getQuantity());

        SendResult<String, ProductCreatedEvent> result = kafkaTemplate.send(
                "product-created-events-topic",
                productId,
                productCreatedEvent)
                .get();

        LOGGER.info("Topic: {}", result.getRecordMetadata().topic());
        LOGGER.info("Partition: {}", result.getRecordMetadata().partition());
        LOGGER.info("Offset: {}", result.getRecordMetadata().offset());
        LOGGER.info("Return: {}", productId);

        return productId;
    }
}
