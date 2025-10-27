package com.sample.order;

import com.sample.order.entity.Product;
import com.sample.order.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DbInit implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(DbInit.class);
    private final ProductRepository productRepository;

    public DbInit(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize database with sample data
        List<Product> products = List.of(
                new Product("iPhone", 999.99),
                new Product("Tesla", 5999.99),
                new Product("Bitcoin", 4600.00),
                new Product("T-shirt", 19.99)
        );

        if (productRepository.findAll().isEmpty()) {
            productRepository.saveAll(products);
            log.info("Database initialized with sample products.");
        }
    }
}
