import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ToastrService } from 'ngx-toastr';
import { BancoQuestoesService } from '../../../services/banco-questoes/banco-questoes';
import { BancoQuestao } from '../../../models/banco-questao.model';
import { ProvaService } from '../../../services/prova/prova-service';

@Component({
  selector: 'app-gerenciar-banco',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './gerenciar-banco.html',
  styleUrls: ['./gerenciar-banco.scss']
})
export class GerenciarBanco implements OnInit {

  questoes: BancoQuestao[] = [];
  questoesExibidas: BancoQuestao[] = [];

  // Listas para os Dropdowns
  topicosDisponiveis: string[] = [];
  conceitosDisponiveis: string[] = [];

  // Variáveis de Filtro
  topicoSelecionado: string = '';
  conceitoSelecionado: string = '';
  nivelSelecionado: string = '';
  ordemSelecionada: 'asc' | 'desc' = 'desc';
  searchTerm: string = '';
  dataFiltro: string = '';

  // Variáveis de Controle
  isLoading = false;
  isEditModalOpen = false;
  isComentarioModalOpen = false;
  isCadastroModalOpen = false;
  isModoEdicao = false;
  questaoEmEdicao: BancoQuestao | null = null;
  questaoComentario: BancoQuestao | null = null;
  novaQuestao: BancoQuestao = this.criarNovaQuestao();
  editStates: { [key: string]: boolean } = {};
  objectKeys = Object.keys;

  constructor(
    private bancoService: BancoQuestoesService,
    private provaService: ProvaService,
    private toastr: ToastrService
  ) { }

  ngOnInit(): void {
    this.carregarQuestoes();
    this.buscarTopicos();
  }

  buscarTopicos() {
    this.provaService.getTopicosDisponiveis().subscribe({
      next: (topicos) => {
        this.topicosDisponiveis = topicos;
      },
      error: (err) => {
        console.error("Erro ao buscar tópicos:", err);
      }
    });
  }

  buscarConceitosPorTopico(topico: string) {
    if (!topico) {
      this.conceitosDisponiveis = [];
      this.conceitoSelecionado = '';
      return;
    }

    this.provaService.getConceitosPorTopico(topico).subscribe({
      next: (conceitos) => {
        this.conceitosDisponiveis = conceitos;
        if (!this.conceitosDisponiveis.includes(this.conceitoSelecionado)) {
          this.conceitoSelecionado = '';
        }
      },
      error: (err) => console.error(`Erro ao buscar conceitos para o tópico ${topico}:`, err)
    });
  }

  carregarQuestoes(): void {
    this.isLoading = true;
    this.bancoService.listarTodas().subscribe({
      next: (data) => {
        this.questoes = data;
        console.log("Questões", this.questoes);
        this.aplicarFiltros();
        this.isLoading = false;
      },
      error: () => {
        this.toastr.error('Erro ao carregar questões.');
        this.isLoading = false;
      }
    });
  }

  aplicarFiltros() {
    let resultado = [...this.questoes];

    if (this.topicoSelecionado) {
      resultado = resultado.filter(q => q.topico === this.topicoSelecionado);
    }

    if (this.conceitoSelecionado) {
      resultado = resultado.filter(q => q.conceito === this.conceitoSelecionado);
    }

    if (this.nivelSelecionado) {
      resultado = resultado.filter(q => q.nivel === this.nivelSelecionado);
    }

    if (this.searchTerm) {
      const termo = this.searchTerm.toLowerCase();
      resultado = resultado.filter(q =>
        q.enunciado.toLowerCase().includes(termo) ||
        q.topico.toLowerCase().includes(termo) ||
        (q.conceito && q.conceito.toLowerCase().includes(termo))
      );
    }

    if (this.dataFiltro) {
      resultado = resultado.filter(q => {
        if (!q.dataCriacao) return false;
        return q.dataCriacao.split('T')[0] === this.dataFiltro;
      });
    }

    resultado.sort((a, b) => {
      const dataA = a.dataCriacao ? new Date(a.dataCriacao).getTime() : 0;
      const dataB = b.dataCriacao ? new Date(b.dataCriacao).getTime() : 0;
      return this.ordemSelecionada === 'asc'
        ? dataA - dataB
        : dataB - dataA;
    });

    this.questoesExibidas = resultado;
  }

  onDataChange() {
    this.aplicarFiltros();
  }

  onConceitoChange() {
    this.aplicarFiltros();
  }

  onNivelChange() {
    this.aplicarFiltros();
  }

  limparFiltros() {
    this.searchTerm = '';
    this.topicoSelecionado = '';
    this.conceitoSelecionado = '';
    this.nivelSelecionado = '';
    this.dataFiltro = '';
    this.conceitosDisponiveis = [];
    this.aplicarFiltros();
  }

  onSearch() {
    this.aplicarFiltros();
  }

  onTopicoChange(event: any) {
    this.topicoSelecionado = event.target.value;
    this.buscarConceitosPorTopico(this.topicoSelecionado);
    this.aplicarFiltros();
  }

  onOrderChange(event: any) {
    this.ordemSelecionada = event.target.value as 'asc' | 'desc';
    this.aplicarFiltros();
  }

  onExcluir(id: string | undefined): void {
    if (!id) return;
    if (confirm('Tem certeza que deseja excluir esta questão?')) {
      this.bancoService.excluirQuestao(id).subscribe({
        next: () => {
          this.toastr.success('Questão excluída.');
          this.carregarQuestoes();
        },
        error: () => this.toastr.error('Erro ao excluir.')
      });
    }
  }

  // --- LÓGICA DE EDIÇÃO INLINE ---
  isEditando(indexQuestao: number, campo: string): boolean {
    return !!this.editStates[`${indexQuestao}_${campo}`];
  }

  ativarEdicao(indexQuestao: number, campo: string): void {
    this.editStates[`${indexQuestao}_${campo}`] = true;
  }

  salvarEdicaoItem(questao: BancoQuestao, indexQuestao: number, campo: string): void {
    this.editStates[`${indexQuestao}_${campo}`] = false;

    if (campo === 'resposta' && questao.respostaCorreta) {
      questao.respostaCorreta = questao.respostaCorreta.toLowerCase();
    }

    if (questao.id) {
      this.bancoService.atualizarQuestao(questao.id, questao).subscribe({
        next: () => {
          this.toastr.success('Alteração salva com sucesso!');
        },
        error: () => {
          this.toastr.error('Erro ao salvar alteração. Verifique a conexão.');
        }
      });
    }
  }

  // --- MÉTODOS DE MODAIS E CADASTRO ---
  salvarQuestao() {
    this.bancoService.cadastrarQuestao(this.novaQuestao).subscribe({
      next: () => {
        this.toastr.success("Questão cadastrada com sucesso!");
        this.fecharCadastro();
        this.carregarQuestoes();
      },
      error: () => this.toastr.error("Erro ao cadastrar questão")
    });
  }

  abrirComentarios(questao: BancoQuestao) {
    this.questaoComentario = questao;
    this.isComentarioModalOpen = true;
  }

  fecharComentarios() {
    this.isComentarioModalOpen = false;
    this.questaoComentario = null;
  }

  abrirCadastro() {
    this.novaQuestao = this.criarNovaQuestao();
    this.isCadastroModalOpen = true;
  }

  fecharCadastro() {
    this.isCadastroModalOpen = false;
    this.isModoEdicao = false;
  }

  criarNovaQuestao(): BancoQuestao {
    return {
      tipo: "MULTIPLA_ESCOLHA_5",
      topico: "",
      enunciado: "",
      alternativas: { a: "", b: "", c: "", d: "", e: "" },
      respostaCorreta: "",
      competencia: "",
      conceito: "",
      comentarioTecnico: "",
      nivel: "UNIVERSITARIO_INTERMEDIARIO",
      dataCriacao: new Date().toISOString().split('.')[0],
    } as BancoQuestao;
  }



}