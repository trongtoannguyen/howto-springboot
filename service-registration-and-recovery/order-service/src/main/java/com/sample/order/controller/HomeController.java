package com.sample.order.controller;

import com.netflix.discovery.EurekaClient;
import com.sample.common.OrderRequest;
import com.sample.common.PaymentRequest;
import com.sample.common.PaymentResponse;
import com.sample.order.entity.Item;
import com.sample.order.entity.Order;
import com.sample.order.entity.Product;
import com.sample.order.repository.ProductRepository;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;

@Controller
public class HomeController {


    private static final Logger log = LoggerFactory.getLogger(HomeController.class);
    private final EurekaClient eurekaClient;
    private final RestClient restClient;
    private final ProductRepository productRepository;

    public HomeController(EurekaClient eurekaClient, RestClient.Builder restClientBuilder, ProductRepository productRepository) {
        this.eurekaClient = eurekaClient;
        this.restClient = restClientBuilder.build();
        this.productRepository = productRepository;
    }

    @GetMapping("/")
    public String index(Model model) {
        var products = productRepository.findAll();
        var homeUrl = getPaymentServiceHomeUrl();
        var walletsEndpoint = homeUrl + "wallets";
        String body = restClient.get().uri(walletsEndpoint).retrieve().body(String.class);
        model.addAttribute("walletCount", body);
        model.addAttribute("products", products);
        return "index";
    }

    @GetMapping("/health")
    @ResponseBody // to return plain text in stead of html
    public String health() {
        return "I am order-service. <br/>" +
                "My health is good. <br/>" +
                "I can communicate with payment-service.";
    }

    @GetMapping("/fund")
    public String fund(RedirectAttributes attributes) {
        var homeUrl = getPaymentServiceHomeUrl();
        var fundEndpoint = homeUrl + "fund";
        attributes.addFlashAttribute("walletInfo", restClient.get()
                .uri(fundEndpoint)
                .retrieve()
                .body(String.class));

        // redirect url to / path of home with model attributes
        return "redirect:/";
    }

    @GetMapping("/balance")
    public String balance(@Valid @RequestParam Long walletNumber, RedirectAttributes attributes) {
        var homeUrl = getPaymentServiceHomeUrl();
        var balanceEndpoint = homeUrl + "balance?walletNumber=" + walletNumber;
        var balance = restClient.get().uri(balanceEndpoint)
                .retrieve().body(String.class);
        // set balance in thymeleaf file
        attributes.addFlashAttribute("balance", balance);
        return "redirect:/";
    }

    //    order new iPhone
    @PostMapping("/order")
    @ResponseBody
    public ResponseEntity<String> order(@Valid @RequestBody OrderRequest orderRequest) {
        var productId = Long.valueOf(orderRequest.getProductId());
        Product product = productRepository.findById(productId).orElseThrow();
        Item item = new Item(product.getId().toString(), orderRequest.getQuantity());
        var price = product.getPrice();
        var amount = price * orderRequest.getQuantity();

        Order order = new Order(item, BigDecimal.valueOf(amount));
        // save order into persistence layer

        // prepare payment request
        var paymentRequest = new PaymentRequest(
                order.getId(),
                order.getAmount(),
                new PaymentRequest.WalletInfo(
                        Long.parseLong(orderRequest.getWalletNumber()),
                        orderRequest.getCvv()
                )
        );

        // call payment service to pay order
        var homeUrl = getPaymentServiceHomeUrl();
        var paymentEndpoint = homeUrl + "checkout";
        PaymentResponse rp;
        try {
            rp = restClient.post().uri(paymentEndpoint)
                    .body(paymentRequest)
                    .retrieve().body(PaymentResponse.class);
            if (rp == null) {
                return ResponseEntity.internalServerError().body("Payment service did not respond.");
            }
            if (rp.getStatus().equals("200")) {
                log.info("Order {} has been paid {}", order.getId(), order.getAmount());
                return ResponseEntity.ok("You just purchased " + order.getId() + " for $" + amount);
            } else if (rp.getStatus().equals("404")) {
                return ResponseEntity.status(404).body("Wallet not found for order " + order.getId() + ". Reason: " + rp.getMessage());
            } else {
                return ResponseEntity.status(402).body("Payment failed for order " + order.getId() + ". Reason: " + rp.getMessage());
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error occurred while processing payment: " + e.getMessage());
        }
    }

    private String getPaymentServiceHomeUrl() {
        var instanceInfo = eurekaClient.getNextServerFromEureka("payment-service", false);
        return instanceInfo.getHomePageUrl();
    }
}
