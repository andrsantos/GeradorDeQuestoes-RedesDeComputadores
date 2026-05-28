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
import { ConceitoConfig } from '../../../models/conceito-config.model'; // <-- IMPORTANTE

export type CampoEdicao = 'enunciado' | 'resposta' | 'a' | 'b' | 'c' | 'd' | 'e';

export interface EstadoEdicao {
  indexQuestao: number;
  campo: CampoEdicao;
}

export interface BlocoGeracao extends TopicoQuantidade {
  subtopicos?: ConceitoConfig[]; 
}

@Component({
  selector: 'app-gerador-prova',
  imports: [CommonModule, FormsModule],
  templateUrl: './gerador-automatico.html',
  styleUrl: './gerador-automatico.scss',
  standalone: true
})
export class GeradorAutomatico implements OnInit {
  
  public assuntosDisponiveis: string[] = []; 
  public assuntoSelecionadoCombo: string = '';

  public isTopicosLoaded: boolean = false; 
  
  public conceitosDisponiveisCombo: string[] = [];
  public conceitosSelecionadosCombo: string[] = [];
  public isConceitosDropdownOpen = false;

  public topicosSelecionados: BlocoGeracao[] = []; 
  prova$: Observable<Prova> | null = null;
  provaId: string | null = null;
  
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
    this.provaService.getTopicosDisponiveis().subscribe({
          next: (topicos) => {
            this.assuntosDisponiveis = topicos;
            this.isTopicosLoaded = true;
          },
          error: (err) => {
            console.error("Erro ao buscar tópicos", err);
            this.isTopicosLoaded = true; 
          }
        });
  }
  

  onAssuntoChange(): void {
      this.conceitosSelecionadosCombo = [];
      this.isConceitosDropdownOpen = false;

      if (this.assuntoSelecionadoCombo) {
        this.buscarConceitosDoBackend(this.assuntoSelecionadoCombo);
      } else {
        this.conceitosDisponiveisCombo = [];
      }
    }

  buscarConceitosDoBackend(assunto: string): void {
    this.provaService.getConceitosPorTopico(assunto).subscribe({
      next: (conceitos) => {
        this.conceitosDisponiveisCombo = conceitos;
      },
      error: (err) => {
        console.error('Erro ao buscar conceitos para o assunto:', err);
        this.toastr.error('Erro ao carregar os conceitos específicos deste assunto.', 'Falha na Comunicação');
        this.conceitosDisponiveisCombo = [];
      }
    });
  }

  toggleConceitosDropdown(): void {
    if (this.assuntoSelecionadoCombo) {
      this.isConceitosDropdownOpen = !this.isConceitosDropdownOpen;
    }
  }

  isConceitoComboSelecionado(conceito: string): boolean {
    return this.conceitosSelecionadosCombo.includes(conceito);
  }

  onToggleConceitoCombo(conceito: string, event: any): void {
    if (event.target.checked) {
      this.conceitosSelecionadosCombo.push(conceito);
    } else {
      this.conceitosSelecionadosCombo = this.conceitosSelecionadosCombo.filter(c => c !== conceito);
    }
  }

  adicionarFiltroProva(): void {
      if (!this.assuntoSelecionadoCombo) return;

      if (this.conceitosSelecionadosCombo.length === 0) {
        const jaExiste = this.topicosSelecionados.some(t => t.topico === this.assuntoSelecionadoCombo && (!t.subtopicos || t.subtopicos.length === 0));
        
        if (jaExiste) {
          this.toastr.warning(`A configuração geral para '${this.assuntoSelecionadoCombo}' já foi adicionada.`);
        } else {
          this.topicosSelecionados.push({
            topico: this.assuntoSelecionadoCombo,
            subtopicos: [], 
            quantidade: 5, quantidadeDificeis: 0, quantidadeFaceis: 0, quantidadeMedias: 0
          });
        }
      } 
      else {
        this.conceitosSelecionadosCombo.forEach(conceito => {
          const jaExiste = this.topicosSelecionados.some(t => 
            t.topico === this.assuntoSelecionadoCombo && 
            t.subtopicos && 
            t.subtopicos.some(s => s.conceito === conceito) 
          );

          if (!jaExiste) {
            this.topicosSelecionados.push({
              topico: this.assuntoSelecionadoCombo, 
              // 3. ATUALIZADO: Estrutura do objeto completo
              subtopicos: [{
                 conceito: conceito,
                 quantidadeFaceis: 0,
                 quantidadeMedias: 0,
                 quantidadeDificeis: 0,
                 quantidade: 0
              }], 
              quantidade: 5, quantidadeDificeis: 0, quantidadeFaceis: 0, quantidadeMedias: 0
            });
          }
        });
      }

    this.assuntoSelecionadoCombo = '';
    this.conceitosDisponiveisCombo = [];
    this.conceitosSelecionadosCombo = [];
    this.isConceitosDropdownOpen = false;
  }

  onRemoverTopico(topicoParaRemover: string): void {
    this.topicosSelecionados = this.topicosSelecionados.filter(
      t => t.topico !== topicoParaRemover
    );
  }

  onRemoverTopicoPorIndex(index: number): void {
    this.topicosSelecionados.splice(index, 1);
  }

  get listaTopicosFormatada(): string {
    if (this.topicosSelecionados.length === 0) {
      return "Nenhum filtro aplicado.";
    }
    return this.topicosSelecionados.map(t => {
      const subs = t.subtopicos && t.subtopicos.length > 0 ? ` (${t.subtopicos.map(s => s.conceito).join(', ')})` : ' (Geral)';
      return t.topico + subs;
    }).join(' | ');
  }

 onCriarProva() {
      this.isLoadingCriar = true; 
      this.prova$ = this.provaService.criarProva().pipe(shareReplay(1));
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
    
    const payloadAgrupado: BlocoGeracao[] = [];

    this.topicosSelecionados.forEach(itemTela => {
      let topicoPai = payloadAgrupado.find(t => t.topico === itemTela.topico);
      
      if (!topicoPai) {
        topicoPai = {
          topico: itemTela.topico,
          quantidade: 0, quantidadeFaceis: 0, quantidadeMedias: 0, quantidadeDificeis: 0,
          subtopicos: [] 
        };
        payloadAgrupado.push(topicoPai);
      }

      topicoPai.quantidade += itemTela.quantidade;
      topicoPai.quantidadeFaceis += itemTela.quantidadeFaceis;
      topicoPai.quantidadeMedias += itemTela.quantidadeMedias;
      topicoPai.quantidadeDificeis += itemTela.quantidadeDificeis;

      if (itemTela.subtopicos && itemTela.subtopicos.length > 0) {
        topicoPai.subtopicos!.push(itemTela.subtopicos[0]);
      }
    });

    this.prova$ = this.provaService.adicionarQuestoesAutomatico(
      this.provaId, 
      payloadAgrupado
    ).pipe(shareReplay(1));
    
    this.prova$.subscribe({
      next: (prova) => {
        this.isLoadingAdicionar = false;
        this.toastr.success('Questões geradas com sucesso!', 'Sucesso');
      },
      error: (err) => {
        if (err.error && err.error.erro) {
          this.toastr.error(err.error.erro, 'Erro');
        } else {
          this.toastr.error('Erro inesperado ao processar a requisição.', 'Erro');
        }
        this.isLoadingAdicionar = false;
      }
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
    if (!this.provaId) return;
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
        alert("Falha ao gerar o PDF da prova.");
        this.isLoadingFinalizar = false;
      }
    });
  }

  atualizarTotal(item: BlocoGeracao): void {
    if (item.quantidadeDificeis < 0) item.quantidadeDificeis = 0;
    if (item.quantidadeMedias < 0) item.quantidadeMedias = 0;
    if (item.quantidadeFaceis < 0) item.quantidadeFaceis = 0;
    item.quantidade = item.quantidadeDificeis + item.quantidadeMedias + item.quantidadeFaceis;

    if (item.subtopicos && item.subtopicos.length > 0) {
      item.subtopicos[0].quantidadeFaceis = item.quantidadeFaceis;
      item.subtopicos[0].quantidadeMedias = item.quantidadeMedias;
      item.subtopicos[0].quantidadeDificeis = item.quantidadeDificeis;
      item.subtopicos[0].quantidade = item.quantidade;
    }
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
    this.bancoQuestoesService.cadastrarQuestao(bancoQuestao).subscribe({
        next: () => {
          this.questoesCadastradas.add(index);
          this.toastr.success("Questão cadastrada no banco!", "Sucesso");
        },
        error: (err) => this.toastr.error("Erro ao cadastrar questão", "Erro")
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
      nivel: questao.nivel,
      dataCriacao: new Date().toISOString().split('.')[0],
      origem: "GERADO_POR_IA",
      arquivoOrigem: undefined
    };
  }
}