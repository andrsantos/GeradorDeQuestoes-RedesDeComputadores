import { Component, OnInit } from '@angular/core';
import { Prova } from '../../../models/prova.model';
import { Observable } from 'rxjs/internal/Observable';
import { ProvaService } from '../../../services/prova/prova-service';
import { GerarQuestaoRequest } from '../../../models/gerar-questao-request.model';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { TopicoQuantidade } from '../../../models/topico-quantidade.model';
import { shareReplay } from 'rxjs/operators';
import { BancoQuestoesService } from '../../../services/banco-questoes/banco-questoes';
import { BancoQuestao } from '../../../models/banco-questao.model';


export type CampoEdicao = 'enunciado' | 'resposta' | 'a' | 'b' | 'c' | 'd' | 'e';

export interface EstadoEdicao {
  indexQuestao: number;
  campo: CampoEdicao;
}

@Component({
  selector: 'app-gerador-prova',
  imports: [CommonModule, FormsModule],
  templateUrl: './gerador-automatico.html',
  styleUrl: './gerador-automatico.scss',
  standalone: true
})


export class GeradorAutomatico implements OnInit {
  
  public topicosDisponiveis: string[] = [];
  public topicosSelecionados: TopicoQuantidade[] = [];
  public isDropdownOpen = false;
  prova$: Observable<Prova> | null = null;
  provaId: string | null = null;
  topico: string = "Modelo OSI";
  quantidade: number = 5;
  isLoadingCriar = false;
  isLoadingAdicionar = false;
  isLoadingFinalizar = false;
  descartandoIndex: number | null = null;
  public editando: EstadoEdicao | null = null;
  modalAberta = false;
  questaoSelecionada: any = null;
  questoesCadastradas = new Set<number>();


  constructor(private provaService: ProvaService, private toastr: ToastrService,
    private bancoQuestoesService: BancoQuestoesService
  ) { }

  ngOnInit(): void {
    this.onCriarProva();
    this.provaService.getTopicosDisponiveis().subscribe(topicos => {
      this.topicosDisponiveis = topicos;
    });
  }
  
  toggleDropdown(): void {
        this.isDropdownOpen = !this.isDropdownOpen;
  }

  isTopicoSelecionado(topico: string): boolean {
    return this.topicosSelecionados.some(t => t.topico === topico);
  }

  onToggleTopico(topico: string, event: any): void {
    const isChecked = event.target.checked;

    if (isChecked) {
      const jaExiste = this.topicosSelecionados.some(t => t.topico === topico);
      
      if (!jaExiste) {
        this.topicosSelecionados.push({
          topico: topico,
          quantidade: 5,
          quantidadeDificeis: 0,
          quantidadeFaceis: 0,
          quantidadeMedias: 0
        });
      }
    } else {
      this.onRemoverTopico(topico);
    }
  }

  onRemoverTopico(topicoParaRemover: string): void {
    this.topicosSelecionados = this.topicosSelecionados.filter(
      t => t.topico !== topicoParaRemover
    );
  }

  get listaTopicosFormatada(): string {
    if (this.topicosSelecionados.length === 0) {
      return "Nenhum tópico selecionado.";
    }
    return this.topicosSelecionados.map(t => t.topico).join(', ');
  }

  onCriarProva() {

      this.isLoadingCriar = true; 
      this.prova$ = this.provaService.criarProva().pipe(
        shareReplay(1) 
      );

      this.prova$.subscribe({
        next: p => {
          this.provaId = p.id;
          this.isLoadingCriar = false; 
        },
        error: () => this.isLoadingCriar = false 
      });
    }

  onGerarProvaAutomatica() {

    if (!this.provaId || this.topicosSelecionados.length === 0) {
      alert("Por favor, adicione pelo menos um tópico."); 
      return;
    }
    
    this.isLoadingAdicionar = true; 
    
    console.log("Topicos selecionados para geração automática:", this.topicosSelecionados);
    
    this.prova$ = this.provaService.adicionarQuestoesAutomatico(
      this.provaId, 
      this.topicosSelecionados
    ).pipe(
      shareReplay(1) 
    );
    
    this.prova$.subscribe({
      next: () => {
        this.isLoadingAdicionar = false;
      },
      error: () => this.isLoadingAdicionar = false
    });

   this.prova$.forEach(prova => {
      console.log("Prova atualizada após geração automática:", prova.questoes);
    });

  }

  onDescartarQuestao(indice: number) {
        if (!this.provaId) return;
        this.descartandoIndex = indice; 

        this.prova$ = this.provaService.descartarQuestao(this.provaId, indice);
        this.prova$.subscribe({
          next: () => this.descartandoIndex = null, 
          error: () => this.descartandoIndex = null 
        });
  
      }

  onFinalizarProva() {
    if (!this.provaId) {
      alert("Nenhuma prova ativa.");
      return;
    }
    this.isLoadingFinalizar = true;
    this.provaService.finalizarProvaPdf(this.provaId).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);

        const a = document.createElement('a');
        a.href = url;
        a.download = `prova_${this.provaId}.pdf`; 
        
        document.body.appendChild(a);
        a.click();

        window.URL.revokeObjectURL(url);
        a.remove();

        this.prova$ = null;
        this.provaId = null;
        this.isLoadingFinalizar = false;
        this.toastr.success("Prova gerada com sucesso!", 'Sucesso!');
      },
      error: (err) => {
        console.error("Erro ao finalizar a prova:", err);
        alert("Falha ao gerar o PDF da prova.");
        this.isLoadingFinalizar = false;
      }
    });
  }

  atualizarTotal(item: TopicoQuantidade): void {
  if (item.quantidadeDificeis < 0) item.quantidadeDificeis = 0;
  if (item.quantidadeMedias < 0) item.quantidadeMedias = 0;
  if (item.quantidadeFaceis < 0) item.quantidadeFaceis = 0;
  item.quantidade = item.quantidadeDificeis + item.quantidadeMedias + item.quantidadeFaceis;
  }

  ativarEdicao(index: number, campo: string): void {
    this.editando = { indexQuestao: index, campo: campo as CampoEdicao };
  }

  isEditando(index: number, campo: string): boolean {
    return this.editando?.indexQuestao === index && this.editando?.campo === campo;
  }
  salvarEdicao(): void {
    this.editando = null;
    this.toastr.info("Alteração salva localmente.");
  }
  abrirComentarios(questao: any) {
  this.questaoSelecionada = questao;
  this.modalAberta = true;
  }
  fecharModal() {
  this.modalAberta = false;
  this.questaoSelecionada = null;
  }

  cadastrarQuestao(questao: any, index: number) {

    const bancoQuestao = this.converterParaBancoQuestao(questao);

    this.bancoQuestoesService.cadastrarQuestao(bancoQuestao)
      .subscribe({
        next: () => {
          this.questoesCadastradas.add(index);
          console.log("Questões cadastradas", this.questoesCadastradas);
          this.toastr.success("Questão cadastrada no banco!", "Sucesso");
        },
        error: (err) => {
          console.error("Erro ao cadastrar questão:", err);
          this.toastr.error("Erro ao cadastrar questão", "Erro");
        }
      });
  }

  converterParaBancoQuestao(questao: any): BancoQuestao {

  return {
    topico: questao.topico || "Geral",

    enunciado: questao.enunciado,

    tipo: "MULTIPLA_ESCOLHA_5",

    alternativas: {
      a: questao.alternativas?.a,
      b: questao.alternativas?.b,
      c: questao.alternativas?.c,
      d: questao.alternativas?.d,
      e: questao.alternativas?.e
    },

    respostaCorreta: questao.respostaCorreta,

    conceito: questao.conceito || "",

    comentarioTecnico: questao.comentarioTecnico || "",

    competencia: questao.competencia || "",

    nivel: "UNIVERSITARIO_INTERMEDIARIO",

    dataCriacao: new Date().toISOString().split('.')[0],

    origem: "GERADO_POR_IA"

  };

  }



}
