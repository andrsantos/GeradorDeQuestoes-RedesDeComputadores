import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CadastroPrompt } from './cadastro-prompt';

describe('CadastroPrompt', () => {
  let component: CadastroPrompt;
  let fixture: ComponentFixture<CadastroPrompt>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroPrompt]
    })
    .compileComponents();

    fixture = TestBed.createComponent(CadastroPrompt);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
