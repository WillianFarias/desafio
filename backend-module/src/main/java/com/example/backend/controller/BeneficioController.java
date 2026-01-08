package com.example.backend.controller;

import com.example.backend.model.Beneficio;
import com.example.backend.service.BeneficioService;
import com.example.ejb.BeneficioEjbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.backend.service.TransferenciaService;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/beneficios")
@CrossOrigin(origins = "*")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private TransferenciaService transferenciaService;

    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(
            @RequestParam Long fromId,
            @RequestParam Long toId,
            @RequestParam BigDecimal amount) {
        try {
            transferenciaService.transferir(fromId, toId, amount);
            return ResponseEntity.ok("Transferência de R$ " + amount + " realizada com sucesso!");
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Erro inesperado: " + e.getMessage());
        }
    }
}
