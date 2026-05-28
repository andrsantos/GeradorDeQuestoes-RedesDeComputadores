import { ConceitoConfig } from "./conceito-config.model";

export interface TopicoQuantidade {
  topico: string;
  quantidade: number;
  quantidadeDificeis: number;
  quantidadeFaceis: number;
  quantidadeMedias: number;
}