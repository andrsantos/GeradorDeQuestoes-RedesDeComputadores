import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { finalize } from 'rxjs/operators';
import { Prompt } from '../../../models/prompt.model';
import { PromptService } from '../../../services/prompts/prompt-service';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';


@Component({
  selector: 'app-detalhe-prompt',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './detalhe-prompt.html',
  styleUrls: ['./detalhe-prompt.scss']
})
export class DetalhePrompt implements OnInit {
  
  nomeTopico: string = '';
  prompts: Prompt[] = [];
  isLoading: boolean = true;

  isFormOpen: boolean = false;
  isSaving: boolean = false;
  promptForm!: FormGroup;
  idPromptEmEdicao: string | null = null;
  

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private promptService: PromptService,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
 
    this.promptForm = this.fb.group({
      nivel: ['', Validators.required],
      instrucao: ['', [Validators.required, Validators.minLength(20)]],
      ativo: [true] 
    });

    this.route.queryParamMap.subscribe(params => {
      this.nomeTopico = params.get('topico') || '';
      if (this.nomeTopico) {
        this.carregarPrompts();
      } else {
        console.warn("Nenhum tópico fornecido na URL. Retornando ao painel.");
        this.voltarParaPainel();
      }
    });
    
  }

  carregarPrompts(): void {
      this.isLoading = true;
      this.promptService.listarPromptsPorTopico(this.nomeTopico)
        .pipe(finalize(() => this.isLoading = false))
        .subscribe({
          next: (data) => this.prompts = data,
          error: (err) => console.error('Erro ao buscar os prompts:', err)
        });
  }
    
  voltarParaPainel(): void {
    this.router.navigate(['/gerenciamento']);
  }

  editarPrompt(prompt: Prompt): void {
    console.log('Em breve: Editar', prompt.id);
  }

  abrirFormularioNovo(): void {
      this.idPromptEmEdicao = null; 
      this.promptForm.reset({ ativo: true, nivel: '' }); 
      this.isFormOpen = true; 
  }

  abrirFormularioEdicao(prompt: Prompt): void {
    if (!prompt.id) return;
    
    this.idPromptEmEdicao = prompt.id; 
    
    this.promptForm.patchValue({
      nivel: prompt.nivel,
      instrucao: prompt.instrucao,
      ativo: prompt.ativo
    });
    
    this.isFormOpen = true; 
  }

  cancelarFormulario(): void {
    this.isFormOpen = false;
    this.idPromptEmEdicao = null;
  }

  cancelarNovo(): void {
    this.isFormOpen = false; 
  }

  salvarPrompt(): void {
    if (this.promptForm.invalid) {
      alert('Preencha os campos obrigatórios corretamente.');
      return;
    }

    this.isSaving = true;
    
    const dadosFormulario = {
      ...this.promptForm.value,
      topicoNome: this.nomeTopico
    };

    if (this.idPromptEmEdicao) {
      this.promptService.editarPrompt(this.idPromptEmEdicao, dadosFormulario)
        .pipe(finalize(() => this.isSaving = false))
        .subscribe({
          next: (promptAtualizado) => {
            const index = this.prompts.findIndex(p => p.id === promptAtualizado.id);
            if (index !== -1) {
              this.prompts[index] = promptAtualizado;
            }
            this.aplicarRegraHighlanderVisual(promptAtualizado); 
            this.cancelarFormulario();
          },
          error: (err) => console.error('Erro ao editar:', err)
        });

    } else {
      this.promptService.cadastrarPrompt(dadosFormulario)
        .pipe(finalize(() => this.isSaving = false))
        .subscribe({
          next: (promptCriado) => {
            this.prompts.unshift(promptCriado);
            this.aplicarRegraHighlanderVisual(promptCriado); 
            this.cancelarFormulario();
          },
          error: (err) => console.error('Erro ao salvar:', err)
        });
    }
  }

  private aplicarRegraHighlanderVisual(promptAtivo: Prompt): void {
    if (promptAtivo.ativo) {
      this.prompts.forEach(p => {
        if (p.nivel === promptAtivo.nivel && p.id !== promptAtivo.id) {
          p.ativo = false;
        }
      });
    }
  }

  deletarPrompt(prompt: Prompt): void {

    console.log("Objeto prompt recebido no clique:", prompt);

    const confirmacao = confirm(`Atenção: Tem certeza que deseja excluir esta instrução do nível ${prompt.nivel}? Essa ação não pode ser desfeita.`);
    
    if (confirmacao && prompt.id) {
      console.log("TESTANOD");
      this.promptService.deletarPrompt(prompt.id).subscribe({
        next: () => {
          this.prompts = this.prompts.filter(p => p.id !== prompt.id);
          
        },
        error: (err) => {
          console.error('Erro ao excluir prompt:', err);
          alert('Não foi possível excluir o prompt. Verifique os logs.');
        }
      });
    }



  }


  alternarStatus(prompt: Prompt): void {
    if (!prompt.id) return;

    this.promptService.alternarStatusPrompt(prompt.id).subscribe({
      next: (promptAtualizado) => {
        prompt.ativo = promptAtualizado.ativo;
        if (promptAtualizado.ativo) {
          this.prompts.forEach(p => {
            if (p.nivel === promptAtualizado.nivel && p.id !== promptAtualizado.id) {
              p.ativo = false;
            }
          });
        }
      },
      error: (err) => {
        console.error('Erro ao alterar status do prompt:', err);
        alert('Não foi possível alterar o status. Verifique os logs.');
      }
    });
  }


}