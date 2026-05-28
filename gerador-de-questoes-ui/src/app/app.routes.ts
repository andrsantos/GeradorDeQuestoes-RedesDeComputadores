import { Routes } from '@angular/router';
import { Dashboard } from './components/dashboard/dashboard';
import { ProvasSalvas } from './pages/provas-salvas/provas-salvas';
import { DetalheProva } from './pages/detalhe-prova/detalhe-prova';
import { Alimentacao } from './pages/alimentacao/alimentacao';
import { GeradorManual } from './pages/gerador-prova/gerador-manual/gerador-manual';
import { GeradorAutomatico } from './pages/gerador-prova/gerador-automatico/gerador-automatico';
import { GeradorProva } from './pages/gerador-prova/gerador-prova';
import { GerenciarBanco } from './pages/banco-dashboard/gerenciar-banco/gerenciar-banco';
import { BancoDashboard } from './pages/banco-dashboard/banco-dashboard';
import { BancoQuestoes } from './pages/banco-dashboard/banco-questoes/banco-questoes';
import { SelecionarQuestao } from './pages/banco-dashboard/selecionar-questao/selecionar-questao';
import { Gerenciamento } from './pages/gerenciamento/gerenciamento';
import { ProvaBuilder } from './pages/prova-builder/prova-builder';
import { CadastroPrompt } from './pages/gerenciamento/cadastro-prompt/cadastro-prompt';

export const routes: Routes = [
   {
    path: '', 
    component: Dashboard,
    title: 'Início - Gerador de Provas'
  },
  {
    path:'gerar-prova', 
    component: GeradorProva,
    title: 'Gerador de Prova'
  },
  {
    
    path: 'gerar-prova/automatico', 
    component: GeradorAutomatico,
    title: 'Gerar Nova Prova'
  },
  {
    path: 'gerar-prova/manual',
    component: GeradorManual,
    title: 'Gerador de Prova '
  },
  {
    path: 'gerar-prova/prova-builder',
    component: ProvaBuilder,
    title: 'Gerador de Prova'
  },
  {
    path: 'provas-salvas',
    component: ProvasSalvas,
    title: 'Provas Salvas'
  },
  {
    path: 'provas-salvas/:id', 
    component: DetalheProva,
    title: 'Detalhe da Prova'
  },
  {
    path:'banco-questoes',
    component: BancoDashboard,
    title:'Dashboard Banco de Questões'
  },
  {
    path:'banco-questoes/gerenciar',
    component: GerenciarBanco,
    title:'Gerenciar Banco de Questões'
  },
  {
    path:'banco-questoes/novo',
    component: BancoQuestoes,
    title:'Banco de Questões'
  },
  {
    path:'banco-questoes/selecionar-questao',
    component: SelecionarQuestao,
    title: 'Selecionar Questão do Banco'
  },
  {
    path:'alimentacao',
    component: Alimentacao,
    title:'Alimentacao - RAG'
  },
    {
    path:'gerenciamento',
    component: Gerenciamento,
    title:'Gerenciamento - RAG'
  },
  {
    path:'cadastro-prompt',
    component: CadastroPrompt,
    title: 'Cadastro de Prompt'
  }

];
