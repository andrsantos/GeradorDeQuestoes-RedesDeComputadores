import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { GerenciamentoService } from '../../services/gerenciamento/gerenciamento-service';
import { Cenario } from '../../models/cenario.model';
import { Prompt } from '../../models/prompt.model';
import { Documento } from '../../models/documento.model';
import { DocumentoExibicao } from '../../models/documento-exibicao.model';
import { Router } from '@angular/router';
import { AlimentacaoService } from '../../services/alimentacao/alimentacao-service';
import { Observable } from 'rxjs';
import { HttpEvent } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-gerenciamento',
  imports: [FormsModule, ReactiveFormsModule, CommonModule],
  templateUrl: './gerenciamento.html',
  styleUrl: './gerenciamento.scss',
  standalone: true
})
export class Gerenciamento implements OnInit {
  

  // Variáveis de Controle
  managementForm!: FormGroup;
  searchPerformed = false;
  listaDocumentacao: any[] = [];
  paginaAtual: number = 1;
  itensPorPagina: number = 7;
  filtroTopico: string = '';
  filtroNivel: string = '';
  
  //Variáveis de Cenário
  cenarioAtualizado: Cenario | null = null;
  listaCenarios: Cenario[] = [];
  showEditModal: boolean = false;
  editCenarioForm!: FormGroup;
  showDeleteModal: boolean = false;
  cenarioParaDeletar: Cenario | null = null;
  showInsertModal: boolean = false;
  insertCenarioForm!: FormGroup;

  // Variáveis de Prompt
  listaPrompts: Prompt[] = [];
  promptSelecionado: Prompt | null = null;
  showPromptModal: boolean = false;
  idPromptEditando: string | null = null;
  promptTemporario: any = {}; 
  showInsertPromptModal: boolean = false;
  insertPromptForm!: FormGroup;

  // Variáveis de Documentos
  listaDocumentos: Documento[] = [];
  showChunkModal: boolean = false;
  chunkSelecionado: Documento | null = null;
  listaMateriais: DocumentoExibicao[] = [];
  public topico: string = ''; 
  public modalContextoAberta = false;
  public promptContexto: string = '';
  public arquivoContexto: File | null = null;
  public nivelSelecionado: string = 'UNIVERSITARIO_INTERMEDIARIO';
  public fonteContexto: string = '';
  public isArquivoContexto = false;
  public isProcessingRag = false;
  public arquivo: File | null = null;
  public isSearching: boolean = false;

  constructor(private fb: FormBuilder, 
    private gerenciamentoService: GerenciamentoService,
    private router: Router,
    private alimentacaoService: AlimentacaoService,
    private toastr: ToastrService
    
) { }

  ngOnInit(): void {
    this.managementForm = this.fb.group({
      tableType: ['', Validators.required]
    });
    
    // Formulários de cenário e prompt
    this.editCenarioForm = this.fb.group({
      id: [''],
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      descricao: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.insertCenarioForm = this.fb.group({
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      descricao: ['', [Validators.required, Validators.minLength(10)]]
    });

    this.insertPromptForm = this.fb.group({
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required]
      // instrucoesEspecificas: ['', [Validators.required, Validators.minLength(20)]]
    });

  }



onSearch(): void {
    if (this.managementForm.valid) {
      this.isSearching = true; 
      this.searchPerformed = false; 
      
      this.listar(); 

      setTimeout(() => {
        this.isSearching = false; 
        this.searchPerformed = true; 
      }, 2000);
    }
  }

  // Método de Listagem comum para todos os cenários
  listar(): void {
    console.log('Tipo selecionado para listagem:', this.managementForm.value.tableType);
    const request = {
      filtro: this.managementForm.value.tableType
    };

    if (request.filtro === 'scenarios') {
      request.filtro = 'CENARIO';
      this.gerenciamentoService.listarCenarios(request).subscribe({
        next: (cenarios) => {
          this.paginaAtual = 1;
          this.listaCenarios = cenarios;
          console.log('Cenarios listados:', cenarios);
        },
        error: (error) => console.error('Erro ao listar cenarios:', error)
      });
    }

    if (request.filtro === 'prompts') {
      request.filtro = 'PROMPTS';
      this.gerenciamentoService.listarPrompts(request).subscribe({
        next: (prompts) => {
          this.paginaAtual = 1;
          this.listaPrompts = prompts;
          console.log('Prompts listados:', prompts);
        },
        error: (error) => console.error('Erro ao listar prompts:', error)
      });
    }

    if (request.filtro === 'documentation') {
      request.filtro = 'DOCUMENTOS';
      this.gerenciamentoService.listarDocumentosFiltrados().subscribe({
        next: (materiais) => {
          this.paginaAtual = 1;
          this.listaMateriais = materiais;
          console.log('Materiais listados:', materiais);
        },
        error: (error) => console.error('Erro ao listar materiais:', error)
      });
    }


  }

  
  // --- MÉTODOS DE PROMPT ---
  iniciarEdicaoPrompt(prompt: Prompt): void {
    this.idPromptEditando = prompt.id;
    this.promptTemporario = { ...prompt };
  }

  
  cancelarEdicao(): void {
    this.idPromptEditando = null;
    this.promptTemporario = {};
  }

  salvarEdicaoInline(): void {
    if (this.idPromptEditando) {
      this.gerenciamentoService.atualizarPrompt(this.idPromptEditando, this.promptTemporario).subscribe({
        next: (promptAtualizado) => {
          const index = this.listaPrompts.findIndex(p => p.id === this.idPromptEditando);
          if (index !== -1) {
            this.listaPrompts[index] = promptAtualizado;
          }
          this.cancelarEdicao(); 
          alert('Prompt atualizado com sucesso!');
        },
        error: (error) => {
          console.error('Erro ao atualizar prompt:', error);
          alert('Erro ao salvar alterações do prompt.');
        }
      });
    }
  }

  visualizarPrompt(prompt: Prompt): void {
    this.promptSelecionado = prompt;
    this.showPromptModal = true;
  }

  deletarPrompt(id: string): void {
    if (confirm('Deseja realmente excluir este prompt?')) {
      this.gerenciamentoService.deletarPrompt(id).subscribe({
        next: () => {
          this.listaPrompts = this.listaPrompts.filter(p => p.id !== id);
          console.log("Prompt deletado:", id);
        },
        error: (error) => console.error('Erro ao deletar prompt:', error)
      });
    }
  }

  abrirModalInsercaoPrompt(): void {
    this.insertPromptForm.reset({ nivel: '' });
    this.showInsertPromptModal = true;
  }

  fecharModalInsercaoPrompt(): void {
    this.showInsertPromptModal = false;
  }

  irParaCadastroPrompt():void {
  this.router.navigate(['/cadastro-prompt']);
  }

  submeterInsercaoPrompt(): void {
    if (this.insertPromptForm.valid) {
      const novoPrompt: Prompt = this.insertPromptForm.value;
      
      this.gerenciamentoService.inserirPrompt(novoPrompt).subscribe({
        next: (promptCriado) => {
          console.log('Prompt criado com sucesso:', promptCriado);
          this.listaPrompts = [promptCriado, ...this.listaPrompts]; 
          this.fecharModalInsercaoPrompt();
          alert('Novo Prompt cadastrado!');
        },
        error: (error) => {
          console.error('Erro ao inserir prompt:', error);
          alert('Erro ao salvar o prompt no banco.');
        }
      });
    }
  }


  // --- MÉTODOS DE CENÁRIO ---
  submeterInsercao(): void {
    if (this.insertCenarioForm.valid) {
      const novoCenario: Cenario = this.insertCenarioForm.value;
      this.gerenciamentoService.inserirCenario(novoCenario).subscribe({
        next: (cenarioCriado) => {
          this.listaCenarios = [cenarioCriado, ...this.listaCenarios];
          this.fecharModalInsercao();
          alert('Cenário inserido com sucesso!');
        },
        error: (error) => alert('Erro ao salvar o novo cenário.')
      });
    }
  }

  abrirModalInsercao(): void {
    this.insertCenarioForm.reset({ nivel: '' });
    this.showInsertModal = true;
  }

  fecharModalInsercao(): void {
    this.showInsertModal = false;
  }

  confirmarExclusao(): void {
    if (this.cenarioParaDeletar?.id) {
      const id = this.cenarioParaDeletar.id;
      this.gerenciamentoService.deletarCenario(id).subscribe({
        next: () => {
          this.listaCenarios = this.listaCenarios.filter(c => c.id !== id);
          if (this.listaCenariosPaginada.length === 0 && this.paginaAtual > 1) this.paginaAtual--;
          this.fecharModalDelecao();
        },
        error: (error) => {
          console.error('Erro ao deletar:', error);
          this.fecharModalDelecao();
        }
      });
    }
  }

  abrirModalDelecao(cenario: Cenario): void {
    this.cenarioParaDeletar = cenario;
    this.showDeleteModal = true;
  }

  fecharModalDelecao(): void {
    this.showDeleteModal = false;
    this.cenarioParaDeletar = null;
  }

  submeterEdicao(): void {
    if (this.editCenarioForm.valid) {
      const cenarioDados: Cenario = this.editCenarioForm.value;
      this.gerenciamentoService.atualizarCenario(cenarioDados.id!, cenarioDados).subscribe({
        next: (cenarioRetornado) => {
          this.listaCenarios = this.listaCenarios.map(c =>
            c.id === cenarioRetornado.id ? cenarioRetornado : c
          );
          this.fecharModalEdicao();
          alert('Cenário atualizado com sucesso!');
        },
        error: (error) => console.error('Erro ao atualizar cenario:', error)
      });
    }
  }

  abrirModalEdicao(cenario: Cenario): void {
    this.showEditModal = true;
    this.cenarioAtualizado = { ...cenario };
    this.editCenarioForm.patchValue({
      id: cenario.id,
      topico: cenario.topico,
      nivel: cenario.nivel,
      descricao: cenario.descricao
    });
  }

  fecharModalEdicao(): void {
    this.showEditModal = false;
    this.editCenarioForm.reset();
    this.cenarioAtualizado = null;
  }

  // --- AUXILIARES ---
  mudarPagina(proxima: boolean): void {
    if (proxima && this.paginaAtual < this.totalPaginas) {
      this.paginaAtual++;
    } else if (!proxima && this.paginaAtual > 1) {
      this.paginaAtual--;
    }
  }

  get listaCenariosFiltrada(): Cenario[] {
    return this.listaCenarios.filter(cenario => {
      const correspondeTopico = cenario.topico.toLowerCase().includes(this.filtroTopico.toLowerCase());
      const correspondeNivel = this.filtroNivel === '' || cenario.nivel === this.filtroNivel;
      return correspondeTopico && correspondeNivel;
    });
  }

  get listaCenariosPaginada(): Cenario[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaCenariosFiltrada.slice(inicio, fim);
  }

  get listaPromptsFiltrada(): Prompt[] {
    return this.listaPrompts.filter(prompt => {
      const correspondeTopico = prompt.topico.toLowerCase().includes(this.filtroTopico.toLowerCase());
      const correspondeNivel = this.filtroNivel === '' || prompt.nivel === this.filtroNivel;
      return correspondeTopico && correspondeNivel;
    });
  }

  get listaPromptsPaginada(): Prompt[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaPromptsFiltrada.slice(inicio, fim);
  }

  get totalPaginas(): number {
    const tipoAtivo = this.managementForm.get('tableType')?.value;
    let totalRegistros = 0;
    
    if (tipoAtivo === 'prompts') totalRegistros = this.listaPromptsFiltrada.length;
    else if (tipoAtivo === 'documentation') totalRegistros = this.listaMateriaisFiltrada.length;
    else totalRegistros = this.listaCenariosFiltrada.length;
      
    return Math.ceil(totalRegistros / this.itensPorPagina) || 1;
  }

  get listaDocumentosFiltrada(): Documento[] {
    return this.listaDocumentos.filter(doc => {
      const meta = this.getParsedMeta(doc);

      const topico = meta.topico || '';
      const fonte = meta.fonte || '';
      const nivel = meta.nivel_material || '';

      const busca = this.filtroTopico.toLowerCase();
      
      const correspondeTopico = topico.toLowerCase().includes(busca) || 
                              fonte.toLowerCase().includes(busca);
                              
      const correspondeNivel = this.filtroNivel === '' || nivel === this.filtroNivel;

      return correspondeTopico && correspondeNivel;
    });
  }
 
  getMeta(doc: Documento) {
    if (typeof doc.metadata === 'string') {
      try { return JSON.parse(doc.metadata); } catch { return {}; }
    }
    return doc.metadata;
  }

  getParsedMeta(doc: Documento): MetadataObj {
    if (!doc.metadata) return {};

    if (typeof doc.metadata === 'string') {
      try {
        return JSON.parse(doc.metadata) as MetadataObj;
      } catch (e) {
        return {};
      }
    }
    
    return doc.metadata as MetadataObj;
  }

  get listaDocumentosPaginada(): Documento[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaDocumentosFiltrada.slice(inicio, fim);
  }

  visualizarChunk(doc: Documento): void {
    this.chunkSelecionado = doc;
    this.showChunkModal = true;
  }
  
  abrirModalDelecaoChunk(doc: Documento): void {
      const meta = this.getParsedMeta(doc);
      const fonte = meta.fonte || '';

    if (confirm(`Deseja excluir este fragmento da fonte: ${fonte}?`)) {
       console.log('Deletando chunk ID:', doc.id);
    }
  }

  onFiltroChange(): void {
    this.paginaAtual = 1;
  }

  limparFiltros(): void {
    this.filtroTopico = '';
    this.filtroNivel = '';
    this.paginaAtual = 1;
  }

  get listaMateriaisFiltrada(): DocumentoExibicao[] {
    return this.listaMateriais.filter(mat => {
      const topico = mat.topico || '';
      const fonte = mat.fonte || '';

      const busca = this.filtroTopico.toLowerCase();
      
      const correspondeTopico = topico.toLowerCase().includes(busca) || 
                                fonte.toLowerCase().includes(busca);                   

      return correspondeTopico;
    });
  }

  get listaMateriaisPaginada(): DocumentoExibicao[] {
    const inicio = (this.paginaAtual - 1) * this.itensPorPagina;
    const fim = inicio + this.itensPorPagina;
    return this.listaMateriaisFiltrada.slice(inicio, fim);
  }

  baixarPDF(idBinario: string, nomeArquivo: string): void {
    console.log(`Iniciando download do arquivo: ${nomeArquivo}...`);
    
    this.gerenciamentoService.baixarMaterialBinario(idBinario).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        
        const linkHTML = document.createElement('a');
        linkHTML.href = url;
        
        linkHTML.download = nomeArquivo; 
        
        document.body.appendChild(linkHTML);
        linkHTML.click();
        document.body.removeChild(linkHTML);
        
        window.URL.revokeObjectURL(url);
      },
      error: (error) => {
        console.error('Erro ao fazer o download do PDF:', error);
        alert('Não foi possível baixar o arquivo. Ele pode estar indisponível no servidor.');
      }
    });
  }

  abrirModalContexto(file?: File): void {
    this.modalContextoAberta = true;
    if (file) {
      this.arquivoContexto = file;
      this.isArquivoContexto = true;
    }
  }

   fecharModalContexto(): void {
    this.modalContextoAberta = false;
    this.arquivoContexto = null;
    this.promptContexto = '';
  }

   isContextoValido(): boolean {
    return !!this.arquivoContexto && 
           this.promptContexto.trim().length > 0 && 
           this.topico.trim().length > 0 && 
           this.fonteContexto.trim().length > 0;
  }

  chamarUploadContexto(arquivo: File, topico: string, nivel: string): Observable<HttpEvent<any>> {
      console.log("Iniciando upload de contexto para o tópico:", topico);
      this.isArquivoContexto = true;
      this.isProcessingRag = true;
      return this.alimentacaoService.uploadPdf(arquivo, topico, this.fonteContexto);
    }

  iniciarUpload() {
      if (!this.arquivoContexto) {
        this.toastr.error('Nenhum arquivo selecionado.', 'Erro');
        return;
      }

      this.chamarUploadContexto(this.arquivoContexto, this.topico, this.nivelSelecionado)
        .subscribe({
          next: (event) => {
            console.log('Progresso do upload:', event);
          },
          error: (error) => {
            console.error('Erro ao fazer upload do arquivo:', error);
            this.isProcessingRag = false;
            alert('Erro ao enviar o documento.');
          },
          complete: () => {
            console.log('Upload finalizado com sucesso!');
            this.isProcessingRag = false;
            this.fecharModalContexto();
            this.listar(); 
            alert('Documento cadastrado e indexado com sucesso!');
          }
        });
    }

  onFileSelected(event: any, tipo: 'contexto'): void {
    console.log("Chegou na fileselected");
    const file = event.target?.files?.[0];
    if (file?.type === 'application/pdf') {
      if (tipo === 'contexto') {
        this.arquivoContexto = file;
        this.isArquivoContexto = true;
      } 
    } else {
      this.toastr.error('Por favor, selecione apenas arquivos PDF.', 'Formato Inválido');
    }
  }

  




}