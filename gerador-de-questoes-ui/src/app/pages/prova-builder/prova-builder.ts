import { Component, OnInit } from '@angular/core';
import { TopicoQuantidade } from '../../models/topico-quantidade.model';
import { firstValueFrom, Observable, shareReplay } from 'rxjs';
import { Prova } from '../../models/prova.model';
import { ProvaService } from '../../services/prova/prova-service';
import { ToastrService } from 'ngx-toastr';
import { BancoQuestoesService } from '../../services/banco-questoes/banco-questoes';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { QuestaoFormatoAvaliarDTO } from '../../models/questao-formato-avaliar.model';
import { IntegracaoAvaliarService } from '../../services/integracao-avaliar/integracao-avaliar';
import { ConceitoConfig } from '../../models/conceito-config.model';
import { Router } from '@angular/router';
import { NotificationService } from '../../services/notification/notification-service';

export type CampoEdicao = 'enunciado' | 'resposta' | 'a' | 'b' | 'c' | 'd' | 'e';

export interface EstadoEdicao {
  indexQuestao: number;
  campo: CampoEdicao;
}

export interface BlocoGeracao extends TopicoQuantidade {
  subtopicos?: ConceitoConfig[];
}

@Component({
  selector: 'app-prova-builder',
  imports: [CommonModule, FormsModule],
  templateUrl: './prova-builder.html',
  styleUrl: './prova-builder.scss',
})
export class ProvaBuilder implements OnInit {

  // Filtros Cascata
  public assuntosDisponiveis: string[] = [];
  public assuntoSelecionadoCombo: string = '';
  public conceitosDisponiveisCombo: string[] = [];
  public conceitosSelecionadosCombo: string[] = [];
  public isConceitosDropdownOpen = false;

  public topicosSelecionados: BlocoGeracao[] = [];

  provaId: string | null = null;
  prova$: Observable<Prova> | null = null;
  isLoadingFinalizar = false;
  isLoadingAdicionar = false;
  isLoadingCriar = false;

  public editando: EstadoEdicao | null = null;
  descartandoIndex: number | null = null;
  modalAberta = false;
  questaoSelecionada: any = null;
  provaFormatoAvaliar: QuestaoFormatoAvaliarDTO[] = [];
  formatoAvaliarResultado: string = '';

  constructor(private provaService: ProvaService, private toastr: ToastrService,
    private bancoQuestoesService: BancoQuestoesService,
    private integracaoAvaliarService: IntegracaoAvaliarService,
    private router: Router,
    private notificationService: NotificationService

  ) { }

  ngOnInit(): void {
    this.onCriarProva();
    this.provaService.getTopicosDisponiveis().subscribe(topicos => {
        this.assuntosDisponiveis = topicos;
      });
  }

  isConceitoComboSelecionado(conceito: string): boolean {
    return this.conceitosSelecionadosCombo.includes(conceito);
  }

  onAssuntoChange(): void {
    this.conceitosSelecionadosCombo = [];
    this.isConceitosDropdownOpen = false;
    if (this.assuntoSelecionadoCombo) {
      this.provaService.getConceitosPorTopico(this.assuntoSelecionadoCombo).subscribe(c => this.conceitosDisponiveisCombo = c);
    }
  }

  toggleConceitosDropdown(): void { if (this.assuntoSelecionadoCombo) this.isConceitosDropdownOpen = !this.isConceitosDropdownOpen; }

  onToggleConceitoCombo(conceito: string, event: any): void {
    if (event.target.checked) this.conceitosSelecionadosCombo.push(conceito);
    else this.conceitosSelecionadosCombo = this.conceitosSelecionadosCombo.filter(c => c !== conceito);
  }

  adicionarFiltroProva(): void {
    if (!this.assuntoSelecionadoCombo) return;
    
    const conceitos = this.conceitosSelecionadosCombo.length > 0 ? this.conceitosSelecionadosCombo : [""];
    
    conceitos.forEach(c => {
       this.topicosSelecionados.push({
         topico: this.assuntoSelecionadoCombo,
         subtopicos: c ? [{ 
           conceito: c, 
           quantidadeFaceis: 0, 
           quantidadeMedias: 0, 
           quantidadeDificeis: 0, 
           quantidade: 0 
         }] : [],
         quantidade: 0, quantidadeDificeis: 0, quantidadeFaceis: 0, quantidadeMedias: 0
       });
    });

    this.assuntoSelecionadoCombo = ''; this.conceitosSelecionadosCombo = [];
  }

  onRemoverTopicoPorIndex(index: number): void { this.topicosSelecionados.splice(index, 1); }

  get listaTopicosFormatada(): string {
    return this.topicosSelecionados.map(t => t.subtopicos?.length ? `${t.topico} (${t.subtopicos[0].conceito})` : t.topico).join(' | ');
  }

onGerarProvaBanco() {
    if (!this.provaId || this.topicosSelecionados.length === 0) return;
    this.isLoadingAdicionar = true;

    const payloadAgrupado: BlocoGeracao[] = [];

    this.topicosSelecionados.forEach(itemTela => {
      let topicoPai = payloadAgrupado.find(t => t.topico === itemTela.topico);
      
      if (!topicoPai) {
        topicoPai = {
          topico: itemTela.topico,
          quantidade: 0,
          quantidadeFaceis: 0,
          quantidadeMedias: 0,
          quantidadeDificeis: 0,
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

    console.log("Payload Agrupado Perfeito para o Java:", payloadAgrupado);

    this.prova$ = this.provaService.gerarProvaBanco(this.provaId, payloadAgrupado).pipe(shareReplay(1));
    
    this.prova$.subscribe({
      next: () => {
        this.isLoadingAdicionar = false;
        this.toastr.success('As questões foram adicionadas a prova!', 'Sucesso');
      },
      error: (err) => {
        this.isLoadingAdicionar = false; 
        
        if (err.error && err.error.erro) {
          this.toastr.error(err.error.erro, 'Atenção');
        } else if (err.error && err.error.message) {
          this.toastr.error(err.error.message, 'Atenção');
        } else if (typeof err.error === 'string') {
          this.toastr.error(err.error, 'Atenção');
        } else {
          this.toastr.error('Ocorreu um erro ao tentar buscar as questões no banco.', 'Erro de Servidor');
        }
      }
    });
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

  isTopicoSelecionado(topico: string): boolean {
    return this.topicosSelecionados.some(t => t.topico === topico);
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

  exportarFormatoAvaliar(){
    this.provaFormatoAvaliar = [];
    let numero = 1;
    this.prova$?.forEach(questao => {
      questao.questoes.forEach(q => {
        const formatoAvaliar = {
          numeroQuestao: numero++,
          enunciado: q.enunciado,
          alternativas: q.alternativas,
          respostaCorreta: q.respostaCorreta
        };
        this.provaFormatoAvaliar.push(formatoAvaliar);
      });
    });

    this.integracaoAvaliarService.exportarFormatoAvaliar(this.provaFormatoAvaliar).subscribe({
      next: (data: string) => {
        this.formatoAvaliarResultado = data;
        this.toastr.success("Prova exportada para avaliação com sucesso!", 'Sucesso!');

      if (data && data.trim().length > 0) {
        this.baixarArquivoTxt(data);
      } else {
        this.toastr.warning("O servidor retornou uma prova vazia.", "Aviso");
      }
      },
      error: (err: any) => {
        console.error("Erro ao exportar prova para avaliação:", err);
        this.toastr.error("Falha ao exportar prova para avaliação.", 'Erro');
      }
    });
  }

  isEditando(index: number, campo: string): boolean {
    return this.editando?.indexQuestao === index && this.editando?.campo === campo;
  }

  ativarEdicao(index: number, campo: string): void {
      this.editando = { indexQuestao: index, campo: campo as CampoEdicao };
  }
  
  salvarEdicao(): void {
    this.editando = null;
    this.toastr.info("Alteração salva localmente.");
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

  abrirComentarios(questao: any) {
    this.questaoSelecionada = questao;
    this.modalAberta = true;
  }

  fecharModal() {
    this.modalAberta = false;
    this.questaoSelecionada = null;
  }
  

  async onFinalizarProva() {
    if (!this.provaId || !this.prova$) {
      alert("Nenhuma prova ativa.");
      return;
    }
    
    this.isLoadingFinalizar = true;

    try {
      const provaAtual = await firstValueFrom(this.prova$);

      await firstValueFrom(this.provaService.salvarProvaNoBanco(provaAtual));
      console.log("Prova salva no banco de dados com sucesso!");
      this.prova$ = null;
      this.provaId = null;
      this.isLoadingFinalizar = false;
      this.notificationService.setMessage("Prova salva no banco de dados com sucesso!");
      this.router.navigate(['/provas-salvas']);

    } catch (error) {
      console.error("Erro ao salvar a prova:", error);
      this.toastr.error("Falha ao salvar a prova no banco de dados.", 'Erro');
      this.isLoadingFinalizar = false;
    }
  }


  private baixarArquivoTxt(conteudo: string) {
    const blob = new Blob([conteudo], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    const data = new Date().toLocaleDateString('pt-BR').replace(/\//g, '-');
    link.download = `prova-avaliacao-${data}.txt`;
    
    link.click();
    window.URL.revokeObjectURL(url);
  }

  async executarAcoesFinais() {
    this.exportarFormatoAvaliar();
    await this.onFinalizarProva();
  }

  
}