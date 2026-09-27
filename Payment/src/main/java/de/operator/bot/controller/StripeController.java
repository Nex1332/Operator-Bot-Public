package de.operator.bot.controller;

import com.stripe.exception.SignatureVerificationException;
import de.operator.bot.service.MainService;
import lombok.extern.log4j.Log4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j
@RestController
@RequestMapping("/operator-bot")
public class StripeController {
    private final MainService mainService;

    public StripeController(MainService mainService) {
        this.mainService = mainService;
    }

    @PostMapping("/webhook")
    public ResponseEntity<String> handleStripeEvent(@RequestBody String payload, @RequestHeader("Stripe-Signature") String sigHeader) {
        try {
            mainService.processStripeEvent(payload, sigHeader);
            return ResponseEntity.ok("OK");
        } catch (SignatureVerificationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
    }
}
