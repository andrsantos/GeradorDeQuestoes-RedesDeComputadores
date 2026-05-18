import { TestBed } from '@angular/core/testing';

import { IntegracaoAvaliar } from './integracao-avaliar';

describe('IntegracaoAvaliar', () => {
  let service: IntegracaoAvaliar;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IntegracaoAvaliar);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
