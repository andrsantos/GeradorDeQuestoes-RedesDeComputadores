import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroDocumento } from './cadastro-documento';

describe('CadastroDocumento', () => {
  let component: CadastroDocumento;
  let fixture: ComponentFixture<CadastroDocumento>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroDocumento]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroDocumento);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
