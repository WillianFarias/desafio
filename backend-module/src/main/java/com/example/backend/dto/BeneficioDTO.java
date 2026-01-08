package com.example.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;

@Schema(description = "Objeto de transferência de dados para Benefícios")
public class BeneficioDTO {

    @Schema(description = "Identificador único do benefício", example = "1")
    private Long id;

    @Schema(description = "Nome do benefício", example = "Vale Alimentação")
    private String nome;

    @Schema(description = "Descrição detalhada do benefício", example = "Crédito mensal para alimentação")
    private String descricao;

    @Schema(description = "Valor atual disponível no benefício", example = "550.00")
    private BigDecimal valor;

    @Schema(description = "Indica se o benefício está ativo para uso", example = "true")
    private Boolean ativo;

    public BeneficioDTO() {}

    public BeneficioDTO(Long id, String nome, String descricao, BigDecimal valor, Boolean ativo) {
        this.id = id;
        this.nome = nome;
        this.descricao = descricao;
        this.valor = valor;
        this.ativo = ativo;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public Boolean getAtivo() {
        return ativo;
    }
}