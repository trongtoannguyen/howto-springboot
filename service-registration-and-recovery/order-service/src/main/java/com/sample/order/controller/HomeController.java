package com.sample.order.controller;

import com.netflix.discovery.EurekaClient;
import com.sample.common.PaymentRequest;
import com.sample.order.entity.Order;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class HomeController {


    private final EurekaClient eurekaClient;
    private final RestClient restClient;

    public HomeController(EurekaClient eurekaClient, RestClient.Builder restClientBuilder) {
        this.eurekaClient = eurekaClient;
        this.restClient = restClientBuilder.build();
    }

    @GetMapping("/")
    public String index(Model model) {
        var homeUrl = getPaymentServiceHomeUrl();
        var walletsEndpoint = homeUrl + "wallets";
        String body = restClient.get().uri(walletsEndpoint).retrieve().body(String.class);
        model.addAttribute("walletCount", body);
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
    public String order(@Valid @RequestBody Order order, @Valid @RequestBody PaymentRequest.WalletInfo cardInfo) {
        PaymentRequest paymentRequest = new PaymentRequest(order.getId(), order.getAmount(), cardInfo);
        // call payment service to pay order
        var homeUrl = getPaymentServiceHomeUrl();
        var paymentEndpoint = homeUrl + "checkout";
        return restClient.post().uri(paymentEndpoint)
                .body(paymentRequest)
                .retrieve().body(String.class);
    }

    private String getPaymentServiceHomeUrl() {
        var instanceInfo = eurekaClient.getNextServerFromEureka("payment-service", false);
        return instanceInfo.getHomePageUrl();
    }
}
