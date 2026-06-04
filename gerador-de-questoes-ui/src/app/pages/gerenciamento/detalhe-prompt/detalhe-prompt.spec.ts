import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalhePrompt } from './detalhe-prompt';

describe('DetalhePrompt', () => {
  let component: DetalhePrompt;
  let fixture: ComponentFixture<DetalhePrompt>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DetalhePrompt]
    })
    .compileComponents();

    fixture = TestBed.createComponent(DetalhePrompt);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
