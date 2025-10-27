package com.sample.payment.controller;

import com.netflix.discovery.EurekaClient;
import com.sample.common.PaymentRequest;
import com.sample.payment.entity.Wallet;
import com.sample.payment.repository.WalletRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

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
        wallet.addBalance(10000);

        // save into persistence layer
        walletRepository.save(wallet);
        return wallet.toString();
    }

    // method to pay your order
    @PostMapping("/checkout")
    public String checkout(@RequestBody PaymentRequest paymentRequest) {
        //check wallet info
        PaymentRequest.WalletInfo walletInfo = paymentRequest.getWalletInfo();
        Wallet wallet = walletRepository.findByWalletNumberAndCvv(walletInfo.getWalletNumber(), walletInfo.getCvv()).orElseThrow();
        if (wallet.getBalance().compareTo(paymentRequest.getAmount()) < 0) {
            return "Insufficient balance.";
        }

        // pay order
        wallet.setBalance(wallet.getBalance().subtract(paymentRequest.getAmount()));
        return "Order placed successfully.\n" + paymentRequest;
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
