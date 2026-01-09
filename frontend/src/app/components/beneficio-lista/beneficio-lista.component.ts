import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { BeneficioService } from '../../services/beneficio.service';
import { Beneficio } from '../../models/beneficio.model';

@Component({
  selector: 'app-beneficio-lista',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './beneficio-lista.component.html',
  styleUrl: './beneficio-lista.component.css'
})
export class BeneficioListaComponent implements OnInit {

  beneficios: Beneficio[] = [];
  mensagemErro: string = '';

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
}
