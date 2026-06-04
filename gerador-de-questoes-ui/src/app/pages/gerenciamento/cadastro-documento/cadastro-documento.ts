import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { AlimentacaoService } from '../../../services/alimentacao/alimentacao-service';

export interface ArquivoUpload {
  arquivo: File;
  fonte: string;
}

@Component({
  selector: 'app-cadastro-documento',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './cadastro-documento.html',
  styleUrls: ['./cadastro-documento.scss']
})
export class CadastroDocumento implements OnInit {

  public topico: string = '';
  public isTopicoFixo: boolean = false;
  public arquivosContexto: ArquivoUpload[] = [];
  public isProcessingRag: boolean = false;

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private alimentacaoService: AlimentacaoService,
    private toastr: ToastrService
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['topico']) {
        this.topico = params['topico'];
        this.isTopicoFixo = true;
      }
    });
  }

  onFileSelected(event: any): void {
    const files: FileList = event.target?.files;
    
    if (files && files.length > 0) {
      for (let i = 0; i < files.length; i++) {
        const file = files[i];
        if (file.type === 'application/pdf') {
          const nomeSemExtensao = file.name.replace('.pdf', '');
          this.arquivosContexto.push({ arquivo: file, fonte: nomeSemExtensao });
        } else {
          this.toastr.error(`O arquivo ${file.name} não é um PDF e foi ignorado.`, 'Formato Inválido');
        }
      }
    }
    event.target.value = ''; 
  }

  removerArquivoDaLista(index: number): void {
    this.arquivosContexto.splice(index, 1);
  }

  isContextoValido(): boolean {
    if (!this.topico || this.topico.trim().length === 0) return false;
    if (this.arquivosContexto.length === 0) return false;
    const algumSemFonte = this.arquivosContexto.some(item => !item.fonte || item.fonte.trim().length === 0);
    return !algumSemFonte;
  }

  voltar(): void {
    this.router.navigate(['/gerenciamento']); 
  }

  iniciarUpload() {
    if (this.arquivosContexto.length === 0) return;
    this.isProcessingRag = true;
    this.uploadSequencial(0);
  }

  private uploadSequencial(index: number = 0) {
    if (index >= this.arquivosContexto.length) {
      this.isProcessingRag = false;
      this.toastr.success('Todos os documentos foram cadastrados e indexados com sucesso!', 'Upload Concluído');
      this.voltar(); 
      return;
    }

    const itemAtual = this.arquivosContexto[index];

    this.alimentacaoService.uploadPdf(itemAtual.arquivo, this.topico, itemAtual.fonte)
      .subscribe({
        next: () => { },
        error: (error) => {
          this.toastr.error(`Falha ao indexar ${itemAtual.arquivo.name}. Pulando para o próximo...`, 'Erro');
          this.uploadSequencial(index + 1);
        },
        complete: () => {
          this.uploadSequencial(index + 1);
        }
      });
  }
}