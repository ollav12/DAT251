package com.example.demo.controller;

import com.example.demo.model.Cosmetics;
import com.example.demo.service.CosmeticsService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cosmetics")
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class CosmeticsController {
    private final CosmeticsService cosmeticsService;

    public record PurchaseRequest(String name) {}

    public CosmeticsController(CosmeticsService cosmeticsService) {
        this.cosmeticsService = cosmeticsService;
    }

    @GetMapping(
            value = "/inventory",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Cosmetics>> getInventory(@RequestParam Long userId) {
        var cosmetics = cosmeticsService.getInventory(userId);
        return ResponseEntity.ok().body(cosmetics);
    }

    @GetMapping(
            value = "/shop",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<List<Cosmetics>> getShop() {
        var cosmetics = cosmeticsService.getShop();
        return ResponseEntity.ok().body(cosmetics);
    }

    @PostMapping(
            value = "/purchaseCosmetics",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Cosmetics> purchaseCosmetics(@RequestParam Long userId, @RequestBody PurchaseRequest request) {
        return ResponseEntity.ok(cosmeticsService.purchaseCosmetics(userId, request.name()));
    }

    @PutMapping(
            value = "/equip/{cosmeticId}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Cosmetics> equipCosmetics(@RequestParam Long userId, @PathVariable Long cosmeticId) {
        return ResponseEntity.ok(cosmeticsService.equipCosmetics(userId, cosmeticId));
    }

}
