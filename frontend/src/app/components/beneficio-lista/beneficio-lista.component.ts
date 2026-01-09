import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-beneficio-lista',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './beneficio-lista.component.html',
  styleUrl: './beneficio-lista.component.css'
})
export class BeneficioListaComponent implements OnInit {

  beneficios: Beneficio[] = [];
  mensagemErro: string = '';

  beneficioOrigem: Beneficio | null = null;
  idDestino: number | null = null;
  valorTransferencia: number = 0;
  mensagemSucesso: string = '';

  constructor(private beneficioService: BeneficioService) {}

  ngOnInit(): void {
    this.carregarBeneficios();
  }

  carregarBeneficios(): void {
    this.beneficioService.listarTodos().subscribe({
      next: (dados) => {
        this.beneficios = dados;
      },
      error: (err) => {
        this.mensagemErro = 'Erro ao carregar benefícios. Verifique se o backend está rodando.';
        console.error(err);
      }
    });
  }

  selecionarOrigem(beneficio: Beneficio): void {
    this.beneficioOrigem = beneficio;
    this.idDestino = null;
    this.mensagemSucesso = '';
    this.mensagemErro = '';
  }

  executarTransferencia(): void {
    if (!this.beneficioOrigem || !this.idDestino || this.valorTransferencia <= 0) {
      this.mensagemErro = 'Preencha todos os campos corretamente.';
      return;
    }

    this.beneficioService.transferir(this.beneficioOrigem.id, this.idDestino, this.valorTransferencia)
      .subscribe({
        next: (res) => {
          this.mensagemSucesso = res;
          this.beneficioOrigem = null;
          this.valorTransferencia = 0;
          this.carregarBeneficios();
        },
        error: (err) => {
          this.mensagemErro = err.error || 'Erro ao realizar transferência.';
        }
      });
  }

  cancelar(): void {
    this.beneficioOrigem = null;
    this.mensagemErro = '';
  }
}
