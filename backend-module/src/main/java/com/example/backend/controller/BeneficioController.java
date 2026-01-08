package com.example.backend.controller;

import com.example.backend.model.Beneficio;
import com.example.backend.service.BeneficioService;
import com.example.backend.service.TransferenciaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/beneficios")
@CrossOrigin(origins = "*")
@Tag(name = "Benefícios", description = "Endpoints para consulta de benefícios e transferência de valores")
public class BeneficioController {

    @Autowired
    private BeneficioService beneficioService;

    @Autowired
    private TransferenciaService transferenciaService;

    @Operation(
            summary = "Listar benefícios",
            description = "Retorna a lista de todos os benefícios cadastrados.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Lista retornada com sucesso",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Beneficio.class))
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<Beneficio>> listarTodos() {
        return ResponseEntity.ok(beneficioService.listarTodos());
    }

    @Operation(
            summary = "Buscar benefício por ID",
            description = "Retorna um benefício específico pelo seu identificador.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Benefício encontrado",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Beneficio.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Benefício não encontrado",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Beneficio> buscarPorId(
            @Parameter(description = "ID do benefício", example = "1", required = true)
            @PathVariable Long id
    ) {
        return beneficioService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Realizar transferência entre benefícios",
            description = "Transfere um valor do benefício de origem para o de destino. " +
                    "Valida saldo, parâmetros e aplica controle de concorrência (optimistic locking).",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Transferência realizada com sucesso",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Requisição inválida (ex.: saldo insuficiente, IDs inválidos, valor <= 0)",
                            content = @Content(mediaType = "text/plain")
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Erro inesperado",
                            content = @Content(mediaType = "text/plain")
                    )
            }
    )
    @PostMapping("/transferir")
    public ResponseEntity<String> transferir(
            @Parameter(description = "ID do benefício de origem", example = "1", required = true)
            @RequestParam Long fromId,

            @Parameter(description = "ID do benefício de destino", example = "2", required = true)
            @RequestParam Long toId,

            @Parameter(description = "Valor a transferir", example = "10.00", required = true)
            @RequestParam BigDecimal amount
    ) {
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