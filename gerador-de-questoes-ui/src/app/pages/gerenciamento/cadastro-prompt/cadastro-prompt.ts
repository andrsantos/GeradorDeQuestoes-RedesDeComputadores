import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators, FormControl } from '@angular/forms';
import { GerenciamentoService } from '../../../services/gerenciamento/gerenciamento-service';
import { DocumentoExibicao } from '../../../models/documento-exibicao.model';
import { Prompt } from '../../../models/prompt.model';

@Component({
  selector: 'app-cadastro-prompt',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-prompt.html',
  styleUrl: './cadastro-prompt.scss',
})
export class CadastroPrompt implements OnInit {
  
insertPromptForm!: FormGroup;
buscaDocumentoControl = new FormControl(''); 
documentosDisponiveis: DocumentoExibicao[] = [];
documentosFiltrados: DocumentoExibicao[] = [];
documentosSelecionados = new Set<string>(); 

  constructor(private fb: FormBuilder, 
  private gerenciamentoService: GerenciamentoService) {}

  ngOnInit(): void {
    this.insertPromptForm = this.fb.group({
      topico: ['', [Validators.required, Validators.minLength(5)]],
      nivel: ['', Validators.required],
      instrucoesEspecificas: ['', [Validators.required, Validators.minLength(20)]]
    });

    this.buscarDocumentos();

    this.buscaDocumentoControl.valueChanges.subscribe(termo => {
      this.filtrarDocumentos(termo || '');
    });
  }

  buscarDocumentos(){
    this.gerenciamentoService.listarDocumentosFiltrados().subscribe({
      next: (materiais: DocumentoExibicao[]) => {
        this.documentosDisponiveis = materiais;
        this.documentosFiltrados = materiais;
        console.log("Materiais ", materiais);
      },
      error: (error) => console.error('Erro ao listar materiais:', error)
    });
  }

  filtrarDocumentos(termo: string): void {
    const busca = termo.toLowerCase().trim();
    if (!busca) {
      this.documentosFiltrados = this.documentosDisponiveis;
      return;
    }

    this.documentosFiltrados = this.documentosDisponiveis.filter(doc => 
      doc.materialReferencia.toLowerCase().includes(busca) ||
      doc.fonte.toLowerCase().includes(busca) ||
      doc.topico.toLowerCase().includes(busca)
    );
  }

  toggleDocumento(idReferencia: string): void {
    if (this.documentosSelecionados.has(idReferencia)) {
      this.documentosSelecionados.delete(idReferencia);
    } else {
      this.documentosSelecionados.add(idReferencia);
    }
  }

  submeterPrompt(): void {
    if (this.insertPromptForm.valid) {
    
      const novoPrompt: Prompt = {
        id: '', 
        topico: this.insertPromptForm.value.topico,
        nivel: this.insertPromptForm.value.nivel,
        instrucoesEspecificas: this.insertPromptForm.value.instrucoesEspecificas,
        listaDocumentos: Array.from(this.documentosSelecionados) 
      };
      
      console.log('Dados completos prontos para enviar ao backend:', novoPrompt);

      this.gerenciamentoService.inserirPrompt(novoPrompt).subscribe({
        next: (promptCriado) => {
          console.log('Prompt criado com sucesso:', promptCriado);
          alert('Novo Prompt cadastrado!');
          
          this.insertPromptForm.reset({ nivel: '' });
          this.documentosSelecionados.clear();
          
          this.voltar();
        },
        error: (error) => {
          console.error('Erro ao inserir prompt:', error);
          alert('Erro ao salvar o prompt no banco.');
        }
      });
    }
  }



  voltar(): void {
    console.log('Voltando para a tela anterior...');
  }

}
