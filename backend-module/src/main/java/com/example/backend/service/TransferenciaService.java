package com.example.backend.service;

import com.example.backend.model.Beneficio;
import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransferenciaService {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void transferir(Long fromId, Long toId, BigDecimal amount) {
        if (fromId.equals(toId)) {
            throw new IllegalArgumentException("Contas de origem e destino devem ser diferentes.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O valor deve ser maior que zero.");
        }

        Long primeiroId = fromId < toId ? fromId : toId;
        Long segundoId = fromId < toId ? toId : fromId;

        Beneficio primeiro = em.find(Beneficio.class, primeiroId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);
        Beneficio segundo = em.find(Beneficio.class, segundoId, LockModeType.OPTIMISTIC_FORCE_INCREMENT);

        if (primeiro == null || segundo == null) {
            throw new IllegalArgumentException("Benefício de origem ou destino não encontrado.");
        }

        Beneficio origem = fromId.equals(primeiroId) ? primeiro : segundo;
        Beneficio destino = toId.equals(primeiroId) ? primeiro : segundo;

        if (origem.getValor().compareTo(amount) < 0) {
            throw new IllegalStateException("Saldo insuficiente. Saldo atual: " + origem.getValor());
        }

        origem.setValor(origem.getValor().subtract(amount));
        destino.setValor(destino.getValor().add(amount));
    }
}