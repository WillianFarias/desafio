import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BeneficioListaComponent } from './beneficio-lista.component';

describe('BeneficioListaComponent', () => {
  let component: BeneficioListaComponent;
  let fixture: ComponentFixture<BeneficioListaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [BeneficioListaComponent]
    })
    .compileComponents();
    
    fixture = TestBed.createComponent(BeneficioListaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
