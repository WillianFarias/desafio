import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BeneficioListaComponent } from './components/beneficio-lista/beneficio-lista.component'; // Importe aqui

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, BeneficioListaComponent],
  template: '<app-beneficio-lista></app-beneficio-lista>',
})
export class AppComponent {
  title = 'frontend';
}
