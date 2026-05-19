import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms'; 
import { HttpEvent, HttpEventType } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { AlimentacaoService } from '../../services/alimentacao/alimentacao-service';
import { BancoQuestoesService } from '../../services/banco-questoes/banco-questoes';
import { BancoQuestao } from '../../models/banco-questao.model';
import { Observable } from 'rxjs';

export type CampoEdicao = 'enunciado' | 'resposta' | 'a' | 'b' | 'c' | 'd' | 'e';

export interface EstadoEdicaoExtraida {
  indexQuestao: number;
  isOriginal: boolean; 
  campo: CampoEdicao;
}

@Component({
  selector: 'app-alimentacao',
  standalone: true,
  imports: [CommonModule, FormsModule], 
  templateUrl: './alimentacao.html',
  styleUrls: ['./alimentacao.scss']
})
export class Alimentacao {
  public arquivo: File | null = null;
  public topico: string = 'Redes TCP/IP'; 
  public isHovering = false;
  public uploadProgress: number | null = null;
  public isProcessing = false; 
  public isProcessingRag = false;
  public modalQuestoesAberta = false;
  public questoesExtraidas: any[] = []; 
  public questoesAprovadas = new Set<number>();
  public editando: EstadoEdicaoExtraida | null = null;
  public isArquivoContexto = false;
  public arquivoContexto: File | null = null;
  public arquivoQuestoes: File | null = null;
  public nivelSelecionado: string = 'UNIVERSITARIO_INTERMEDIARIO';
  public fonteContexto: string = '';
  public idPdfCadastrado: string | null = null;

  constructor(
    private alimentacaoService: AlimentacaoService,
    private bancoQuestoesService: BancoQuestoesService,
    private toastr: ToastrService
  ) {}

onFileSelected(event: any, tipo: 'contexto' | 'questoes'): void {
    const file = event.target?.files?.[0];
    if (file?.type === 'application/pdf') {
      if (tipo === 'contexto') {
        this.arquivoContexto = file;
        this.isArquivoContexto = true;
      } else {
        this.arquivoQuestoes = file;
        this.arquivo = file; 
        this.isArquivoContexto = false;
      }
    } else {
      this.toastr.error('Por favor, selecione apenas arquivos PDF.', 'Formato Inválido');
    }
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault(); 
    this.isHovering = true; 
  }

  onDragLeave(event: DragEvent): void {
    event.preventDefault();
    this.isHovering = false; 
  }


  onDrop(event: DragEvent, tipo: 'contexto' | 'questoes'): void {
    event.preventDefault();
    const file = event.dataTransfer?.files[0];
    if (file?.type === 'application/pdf') {
      if (tipo === 'contexto') {
        this.arquivoContexto = file;
        this.isArquivoContexto = true;
      } else {
        this.arquivoQuestoes = file;
        this.arquivo = file;
        this.isArquivoContexto = false;
      }
    }
    this.isHovering = false;
  }

  removerArquivo(tipo: 'contexto' | 'questoes'): void {
    if (tipo === 'contexto') {
      this.arquivoContexto = null;
      this.isArquivoContexto = false;
    } else {
      this.arquivoQuestoes = null;
      this.arquivo = null;
    }
    this.uploadProgress = null;
    this.isProcessing = false;
  }


  chamarUploadContexto(arquivo: any, topico:any, nivel: any): Observable<HttpEvent<any>> {
    console.log("Iniciando upload de contexto para o tópico:", topico);
    this.arquivo = arquivo;
    this.isArquivoContexto = true;
    this.isProcessingRag = true;
    return this.alimentacaoService.uploadPdf(arquivo,topico, nivel,this.fonteContexto);
  }


  iniciarUpload(tipo: 'contexto' | 'questoes'): void {
    const arquivoAlvo = tipo === 'contexto' ? this.arquivoContexto : this.arquivoQuestoes;
    if (!arquivoAlvo) return;

    this.isProcessing = (tipo === 'questoes');
    this.isProcessingRag = (tipo === 'contexto');
    this.uploadProgress = 0;
    
    const upload$ = tipo === 'contexto' 
      ? this.chamarUploadContexto(arquivoAlvo, this.topico, this.nivelSelecionado
      )
      : this.alimentacaoService.uploadQuestoes(arquivoAlvo);

    upload$.subscribe({
      next: (event) => {
        if (event.type === HttpEventType.UploadProgress) {
          this.uploadProgress = Math.round(100 * (event.loaded / (event.total || 1)));
        } else if (event.type === HttpEventType.Response) {
          this.isProcessing = false;
          this.isProcessingRag = false;
          this.uploadProgress = 100;

          if (tipo === 'questoes') {
            this.questoesExtraidas = event.body;
            this.prepararRevisao(); 
          } else {
            this.toastr.success('Contexto alimentado com sucesso!', 'Sucesso');
            this.arquivoContexto = null;
          }
        }
      },
      error: (err) => {
        this.toastr.error('Erro no processamento do arquivo.');
        this.isProcessing = false;
        this.isProcessingRag = false;
        this.uploadProgress = null;
      }
    });
  }
  

  prepararRevisao() {
    this.questoesAprovadas.clear();
    this.questoesExtraidas.forEach((_, i) => this.questoesAprovadas.add(i));
    this.modalQuestoesAberta = true;
  }

  toggleAprovacao(index: number) {
    this.questoesAprovadas.has(index) ? this.questoesAprovadas.delete(index) : this.questoesAprovadas.add(index);
  }

  marcarTodas() {
    this.questoesExtraidas.forEach((_, i) => this.questoesAprovadas.add(i));
  }

  desmarcarTodas() {
    this.questoesAprovadas.clear();
  }

  ativarEdicao(index: number, campo: string, isOriginal: boolean): void {
    this.editando = { indexQuestao: index, campo: campo as CampoEdicao, isOriginal };
  }

  isEditando(index: number, campo: string, isOriginal: boolean): boolean {
    return this.editando?.indexQuestao === index && 
           this.editando?.campo === campo && 
           this.editando?.isOriginal === isOriginal;
  }

  salvarEdicao(): void {
    this.editando = null;
  }

confirmarESalvarNoBanco() {
  const questoesParaSalvar: BancoQuestao[] = [];

  this.questoesExtraidas.forEach((par, i) => {
    if (this.questoesAprovadas.has(i)) {
      questoesParaSalvar.push(this.converterParaBancoQuestao(par));
      if (par.questaoInspirada) {
        questoesParaSalvar.push(this.converterParaBancoQuestao(par.questaoInspirada));
      }
    }
  });

  if (questoesParaSalvar.length === 0) {
    this.toastr.warning("Nenhuma questão selecionada.");
    return;
  }

  this.bancoQuestoesService.cadastrarPdfOrigem(this.arquivoQuestoes!).subscribe({
    next: (pdfCadastrado) => {
      this.idPdfCadastrado = pdfCadastrado.id;

      questoesParaSalvar.forEach(q => q.arquivoOrigem = this.idPdfCadastrado!);
      questoesParaSalvar.forEach(q => q.origem = "GERADO_POR_PDF");
      questoesParaSalvar.forEach(q => {
        this.bancoQuestoesService.cadastrarQuestao(q).subscribe();
      });

      this.toastr.success(`${questoesParaSalvar.length} questões enviadas para o banco!`);
      this.modalQuestoesAberta = false;
      this.arquivoQuestoes = null;
      this.arquivo = null;
    },
    error: () => {
      this.toastr.error('Erro ao cadastrar PDF de origem. Questões não foram salvas.');
    }
  });
}

  private converterParaBancoQuestao(q: any): BancoQuestao {
    return {
      topico: q.topico || this.topico,
      enunciado: q.enunciado,
      tipo: "MULTIPLA_ESCOLHA_5",
      alternativas: {
        a: q.alternativas?.A || q.alternativas?.a,
        b: q.alternativas?.B || q.alternativas?.b,
        c: q.alternativas?.C || q.alternativas?.c,
        d: q.alternativas?.D || q.alternativas?.d,
        e: q.alternativas?.E || q.alternativas?.e
      },
      respostaCorreta: q.respostaCorreta,
      conceito: q.conceito || "",
      comentarioTecnico: q.comentarioTecnico || "",
      competencia: q.competencia || "",
      nivel: q.nivel || "UNIVERSITARIO_INTERMEDIARIO",
      dataCriacao: new Date().toISOString().split('.')[0],
      origem: "",
      arquivoOrigem: q.arquivoId
    };
  }

  downloadPdfOriginal() {
    if (this.arquivo) {
      const url = window.URL.createObjectURL(this.arquivo);
      const a = document.createElement('a');
      a.href = url;
      a.download = this.arquivo.name;
      a.click();
      window.URL.revokeObjectURL(url);
    }
  }
}