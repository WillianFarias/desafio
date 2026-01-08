package com.example.backend.service;

import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficioService {

    @Autowired
    private BeneficioRepository repository;

    public List<Beneficio> listarTodos() {
        return repository.findAll();
    }

    public Optional<Beneficio> buscarPorId(Long id) {
        return repository.findById(id);
    }

    @Transactional
    public Beneficio salvar(Beneficio beneficio) {
        return repository.save(beneficio);
    }

    @Transactional
    public void deletar(Long id) {
        repository.deleteById(id);
    }

    // Aqui você chamará o seu BeneficioEjbService para a transferência
    public void realizarTransferencia(Long fromId, Long toId, java.math.BigDecimal amount) {
        // Integração com EJB virá aqui
    }
}