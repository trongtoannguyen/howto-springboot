package com.sample.payment.controller;

import com.netflix.discovery.EurekaClient;
import com.sample.common.PaymentRequest;
import com.sample.common.PaymentResponse;
import com.sample.payment.entity.Wallet;
import com.sample.payment.repository.WalletRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@RestController
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final RestClient restClient;
    private final EurekaClient discoveryClient;
    private final WalletRepository walletRepository;

    public HomeController(EurekaClient discoveryClient, RestClient.Builder restClientBuilder, WalletRepository walletRepository) {
        this.discoveryClient = discoveryClient;
        restClient = restClientBuilder.build();
        this.walletRepository = walletRepository;
    }

    @GetMapping("/")
    public String index() {
        return "I am payment-service. <br/>" +
                "Click <a href='/hello-eureka-order-service'>here</a> to call order-service";
    }

    // check wallet balance
    @GetMapping("/balance")
    public String balance(@RequestParam Long walletNumber) {
        var wallet = walletRepository.findByWalletNumber(walletNumber).orElse(null);
        if (wallet == null) {
            return "Wallet not found.";
        }
        return wallet.getBalance().toString();
    }

    // fund your new wallet
    @GetMapping("/fund")
    public String fund() {
        Wallet wallet = new Wallet();
        wallet.addBalance(BigDecimal.valueOf(10000));

        // save into persistence layer
        walletRepository.save(wallet);
        return wallet.toString();
    }

    // method to pay your order
    @PostMapping("/checkout")
    public PaymentResponse checkout(@Valid @RequestBody PaymentRequest paymentRequest) {
        try {
            //check wallet info
            PaymentRequest.WalletInfo walletInfo = paymentRequest.getWalletInfo();
            var walletOpt = walletRepository.findByWalletNumberAndCvv(walletInfo.getWalletNumber(), walletInfo.getCvv());
            if (walletOpt.isEmpty()) {
                return new PaymentResponse(
                        "404",
                        paymentRequest.getOrderId(),
                        "Wallet not found."
                );
            }

            Wallet wallet = walletOpt.get();
            if (wallet.getBalance().compareTo(paymentRequest.getAmount()) < 0) {
                return new PaymentResponse(
                        "402",
                        paymentRequest.getOrderId(),
                        "Insufficient balance."
                );
            }

            // pay order
            wallet.deductBalance(paymentRequest.getAmount());
            walletRepository.save(wallet);
            return new PaymentResponse(
                    "200",
                    paymentRequest.getOrderId(),
                    "Payment successful."
            );

        } catch (Exception e) {
            return new PaymentResponse(
                    "500",
                    paymentRequest.getOrderId(),
                    "Payment processing failed: " + e.getMessage()
            );
        }
    }

    @GetMapping("/wallets")
    public String wallets() {
        long count = walletRepository.count();
        return String.valueOf(count);
    }

    @GetMapping("/hello-eureka-order-service")
    public String hello() {
        var url = getOrderHomeUrl() + "/health";
        return restClient.get().uri(url).retrieve().body(String.class);
    }

    private String getOrderHomeUrl() {
        var instanceInfo = discoveryClient.getNextServerFromEureka("order-service", false);
        return instanceInfo.getHomePageUrl();
    }
}
