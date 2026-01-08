package com.example.backend.mapper;

import com.example.backend.dto.BeneficioDTO;
import com.example.backend.model.Beneficio;

public class BeneficioMapper {

    public static BeneficioDTO toDTO(Beneficio beneficio) {
        return new BeneficioDTO(
                beneficio.getId(),
                beneficio.getNome(),
                beneficio.getDescricao(),
                beneficio.getValor(),
                beneficio.getAtivo()
        );
    }
}