import { Component, OnInit } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common'; 
import { FormsModule } from '@angular/forms'; 
import { ActivatedRoute, RouterLink, Router } from '@angular/router'; 
import { firstValueFrom, Observable, switchMap, tap } from 'rxjs';
import { ProvaSalva } from '../../models/prova-entity.model'; 
import { Questao } from '../../models/questao.model';
import { ToastrService } from 'ngx-toastr';
import { ProvaService } from '../../services/prova/prova-service';
import { NotificationService } from '../../services/notification/notification-service';
import { ProvaManualStateService } from '../../services/prova-manual-state/prova-manual-state';
import { IntegracaoAvaliarService } from '../../services/integracao-avaliar/integracao-avaliar';

@Component({
  selector: 'app-detalhe-prova',
  standalone: true,
  imports: [CommonModule, RouterLink, DatePipe, FormsModule], 
  templateUrl: './detalhe-prova.html', 
  styleUrls: ['./detalhe-prova.scss']    
})
export class DetalheProva implements OnInit {

  public prova$!: Observable<ProvaSalva>;
  
  public isDeleteModalVisible = false;
  public isEditModalVisible = false; 
  public isDeleting = false;
  public isDownloading = false;
  public isGenerating = false; 
  public isSavingQuestao = false; 

  private provaId: string | null = null; 
  
  public questaoEmEdicao: Questao | null = null; 
  public topicoParaGerar: string = ''; 
  
  public topicosDisponiveis: string[] = [];

  public isExporting = false;

  objectKeys = Object.keys;

  constructor(
    private provaService: ProvaService,
    private route: ActivatedRoute,
    private router: Router,
    private notificationService: NotificationService,
    private toastr: ToastrService,
    private stateService: ProvaManualStateService ,
    private integracaoAvaliarService: IntegracaoAvaliarService
  ) {}

  ngOnInit(): void {
    this.carregarProva();
    this.carregarTopicos(); 
  }

  carregarTopicos() {
    this.provaService.getTopicosDisponiveis().subscribe(topicos => {
      this.topicosDisponiveis = topicos;
    });
  }

  carregarProva() {
    this.prova$ = this.route.paramMap.pipe(
      switchMap(params => {
        const id = params.get('id');
        if (!id) throw new Error('ID não encontrado');
        this.provaId = id;
        return this.provaService.getDetalheProva(id);
      }),
      tap(prova => {
        this.verificarRetornoDoBanco(prova);
      })
    );
  }

  verificarRetornoDoBanco(prova: ProvaSalva): void {
    const estado = this.stateService.lerEstado();
    const questaoDoBanco = this.stateService.getAndClearQuestaoSelecionada();

    if (estado && estado.provaId === this.provaId && estado.idQuestaoEdicao && questaoDoBanco) {
      const questaoOriginal = prova.questoes.find(q => q.id === estado.idQuestaoEdicao);
      
      if (questaoOriginal) {
        this.abrirModalEdicao(questaoOriginal);
        
        if (this.questaoEmEdicao) {
          this.questaoEmEdicao.enunciado = questaoDoBanco.enunciado;
          this.questaoEmEdicao.alternativas = questaoDoBanco.alternativas;
          this.questaoEmEdicao.respostaCorreta = questaoDoBanco.respostaCorreta;
          this.questaoEmEdicao.topico = questaoDoBanco.topico;
          
          this.toastr.info('Questão do banco carregada. Salve para confirmar.');
        }
      }
      this.stateService.limparEstado();
    }
  }

  getTipoQuestaoEdicao(): 'M5' | 'M4' | 'VF' | 'DISC' {
    if (!this.questaoEmEdicao) return 'DISC';

    const alts = this.questaoEmEdicao.alternativas || {};
    const has = (key: string) => alts[key] && alts[key].trim() !== '';

    if (has('a') && has('b') && has('c') && has('d') && has('e')) return 'M5';
    if (has('a') && has('b') && has('c') && has('d')) return 'M4';

    const resp = (this.questaoEmEdicao.respostaCorreta || '').toUpperCase().trim();
    if (resp === 'V' || resp === 'F') return 'VF';

    return 'DISC';
  }

  abrirModalEdicao(questao: Questao): void {
    this.questaoEmEdicao = JSON.parse(JSON.stringify(questao));
    this.topicoParaGerar = questao.topico || ''; 
    this.isEditModalVisible = true;
  }

  fecharModalEdicao(): void {
    this.isEditModalVisible = false;
    this.questaoEmEdicao = null;
  }

  gerarNovaVersaoIA(): void {
    if (!this.topicoParaGerar) {
      this.toastr.warning("Selecione um tópico para gerar.");
      return;
    }
    this.isGenerating = true;
    
    this.provaService.gerarQuestaoAvulsa(this.topicoParaGerar, 1).subscribe({
      next: (res) => {
        if (res.questoes && res.questoes.length > 0) {
          const nova = res.questoes[0];
          if (this.questaoEmEdicao) {
            this.questaoEmEdicao.enunciado = nova.enunciado;
            this.questaoEmEdicao.alternativas = nova.alternativas;
            this.questaoEmEdicao.respostaCorreta = nova.respostaCorreta;
            this.questaoEmEdicao.topico = this.topicoParaGerar;
          }
          this.toastr.success("Nova versão gerada! Salve para confirmar.");
        }
        this.isGenerating = false;
      },
      error: () => {
        this.toastr.error("Erro ao gerar com IA.");
        this.isGenerating = false;
      }
    });
  }

  irAoBanco(): void {
    if (!this.provaId || !this.questaoEmEdicao?.id) return;

    this.stateService.salvarEstado({
      provaId: this.provaId,
      idQuestaoEdicao: this.questaoEmEdicao.id, 
      returnUrl: `/provas-salvas/${this.provaId}` 
    });

    this.router.navigate(['/banco-questoes/selecionar-questao']);
  }

  salvarEdicao(): void {
    if (!this.questaoEmEdicao || !this.questaoEmEdicao.id) return;

    this.isSavingQuestao = true;
    this.provaService.atualizarQuestaoExistente(this.questaoEmEdicao.id, this.questaoEmEdicao)
      .subscribe({
        next: () => {
          this.toastr.success("Questão atualizada!");
          this.isSavingQuestao = false;
          this.fecharModalEdicao();
          this.carregarProva(); 
        },
        error: () => {
          this.toastr.error("Erro ao salvar.");
          this.isSavingQuestao = false;
        }
      });
  }

  temAlternativasValidas(alternativas: any): boolean {
    if (!alternativas) return false;
    return Object.values(alternativas).some((v: any) => v && v.trim() !== '');
  }
  
  openDeleteModal(): void { this.isDeleteModalVisible = true; }
  closeDeleteModal(): void { this.isDeleteModalVisible = false; }
  
  onConfirmDelete(): void {
     if (!this.provaId) return;
     this.isDeleting = true;
     this.provaService.deleteProva(this.provaId).subscribe({
        next: () => {
           this.notificationService.setMessage('Prova excluída!');
           this.router.navigate(['/provas-salvas']);
        },
        error: () => { this.isDeleting = false; this.isDeleteModalVisible = false; }
     });
  }

  onDownloadPdf(): void {
     if (!this.provaId) return;
     this.isDownloading = true;
     this.provaService.downloadProvaSalvaPdf(this.provaId).subscribe({
        next: (blob) => {
           const url = window.URL.createObjectURL(blob);
           const a = document.createElement('a');
           a.href = url;
           a.download = `prova_${this.provaId}.pdf`;
           a.click();
           this.isDownloading = false;
        },
        error: () => this.isDownloading = false
     });
  }

  async exportarFormatoAvaliar() {
    this.isExporting = true;
    
    try {
      const prova = await firstValueFrom(this.prova$);
      
      if (!prova || !prova.questoes || prova.questoes.length === 0) {
        this.toastr.warning("A prova está vazia.", "Aviso");
        this.isExporting = false;
        return;
      }

      let numero = 1;
      const payloadFormatado = prova.questoes.map(q => ({
        numeroQuestao: numero++,
        enunciado: q.enunciado,
        alternativas: q.alternativas,
        respostaCorreta: q.respostaCorreta
      }));

      this.integracaoAvaliarService.exportarFormatoAvaliar(payloadFormatado).subscribe({
        next: (data: string) => {
          this.toastr.success("Prova exportada para avaliação com sucesso!", 'Sucesso!');
          if (data && data.trim().length > 0) {
            this.baixarArquivoTxt(data);
          } else {
            this.toastr.warning("O servidor retornou uma prova vazia.", "Aviso");
          }
          this.isExporting = false;
        },
        error: (err: any) => {
          console.error("Erro ao exportar prova para avaliação:", err);
          this.toastr.error("Falha ao exportar prova para avaliação.", 'Erro');
          this.isExporting = false;
        }
      });
      
    } catch (error) {
       console.error("Erro ao processar prova para exportação:", error);
       this.toastr.error("Falha ao ler os dados da prova.", 'Erro');
       this.isExporting = false;
    }
  }

  private baixarArquivoTxt(conteudo: string) {
    const blob = new Blob([conteudo], { type: 'text/plain;charset=utf-8' });
    const url = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    
    const dataAtual = new Date().toLocaleDateString('pt-BR').replace(/\//g, '-');
    link.download = `prova-avaliacao-${dataAtual}.txt`;
    
    link.click();
    window.URL.revokeObjectURL(url);
  }


}