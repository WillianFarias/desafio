package com.example.backend.service;

import com.example.backend.model.Beneficio;
import com.example.backend.repository.BeneficioRepository;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
class TransferenciaServiceTest {

    @Autowired
    private TransferenciaService transferenciaService;

    @Autowired
    private BeneficioRepository beneficioRepository;

    @Test
    void deveTransferirQuandoSaldoSuficiente() {
        // given
        Beneficio origem = beneficioRepository.findById(1L).orElseThrow();
        Beneficio destino = beneficioRepository.findById(2L).orElseThrow();

        BigDecimal saldoOrigemAntes = origem.getValor();
        BigDecimal saldoDestinoAntes = destino.getValor();

        // when
        transferenciaService.transferir(1L, 2L, new BigDecimal("10.00"));

        // then
        Beneficio origemDepois = beneficioRepository.findById(1L).orElseThrow();
        Beneficio destinoDepois = beneficioRepository.findById(2L).orElseThrow();

        assertThat(origemDepois.getValor())
                .isEqualByComparingTo(saldoOrigemAntes.subtract(new BigDecimal("10.00")));

        assertThat(destinoDepois.getValor())
                .isEqualByComparingTo(saldoDestinoAntes.add(new BigDecimal("10.00")));
    }

    @Test
    void deveFalharQuandoSaldoInsuficiente() {
        // given
        Beneficio origem = beneficioRepository.findById(1L).orElseThrow();
        BigDecimal saldoOrigem = origem.getValor();

        // when / then
        assertThatThrownBy(() ->
                transferenciaService.transferir(1L, 2L, saldoOrigem.add(BigDecimal.ONE))
        )
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Saldo insuficiente");
    }

    @Test
    void deveFazerRollbackQuandoTransferenciaFalha() {
        // given
        Beneficio origemAntes = beneficioRepository.findById(1L).orElseThrow();
        Beneficio destinoAntes = beneficioRepository.findById(2L).orElseThrow();

        BigDecimal saldoOrigemAntes = origemAntes.getValor();
        BigDecimal saldoDestinoAntes = destinoAntes.getValor();

        // when
        try {
            transferenciaService.transferir(1L, 2L, saldoOrigemAntes.add(BigDecimal.TEN));
        } catch (Exception ignored) {}

        // then
        Beneficio origemDepois = beneficioRepository.findById(1L).orElseThrow();
        Beneficio destinoDepois = beneficioRepository.findById(2L).orElseThrow();

        assertThat(origemDepois.getValor()).isEqualByComparingTo(saldoOrigemAntes);
        assertThat(destinoDepois.getValor()).isEqualByComparingTo(saldoDestinoAntes);
    }

    @Test
    void deveDetectarConcorrenciaComOptimisticLocking() throws InterruptedException {
        // given
        Beneficio origem = beneficioRepository.findById(1L).orElseThrow();
        BigDecimal saldoInicial = origem.getValor();

        // Garante que há saldo suficiente para ambas as transferências
        if (saldoInicial.compareTo(new BigDecimal("20")) < 0) {
            origem.setValor(new BigDecimal("100"));
            beneficioRepository.saveAndFlush(origem);
        }

        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        CountDownLatch startSignal = new CountDownLatch(1);

        AtomicInteger sucessos = new AtomicInteger(0);
        AtomicInteger falhas = new AtomicInteger(0);

        // when - duas threads tentam transferir do mesmo benefício simultaneamente
        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    // Aguarda sinal para todas começarem juntas
                    startSignal.await();

                    // Tenta fazer a transferência
                    transferenciaService.transferir(1L, 2L, new BigDecimal("10.00"));
                    sucessos.incrementAndGet();

                } catch (ObjectOptimisticLockingFailureException | OptimisticLockException e) {
                    // Esperado: uma das threads deve falhar por conflito de versão
                    falhas.incrementAndGet();
                } catch (Exception e) {
                    // Outras exceções não esperadas
                    System.err.println("Erro inesperado: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        // Libera todas as threads ao mesmo tempo
        startSignal.countDown();

        // Aguarda todas terminarem
        latch.await();
        executor.shutdown();

        // then
        System.out.println("Sucessos: " + sucessos.get());
        System.out.println("Falhas (Optimistic Lock): " + falhas.get());

        // Pelo menos uma deve ter falhado por conflito de versão
        assertThat(falhas.get())
                .withFailMessage("Esperava-se que pelo menos uma thread falhasse por Optimistic Locking")
                .isGreaterThan(0);

        // Pelo menos uma deve ter sucesso
        assertThat(sucessos.get())
                .withFailMessage("Esperava-se que pelo menos uma thread tivesse sucesso")
                .isGreaterThan(0);

        // Total deve ser igual ao número de threads
        assertThat(sucessos.get() + falhas.get()).isEqualTo(numThreads);
    }
}