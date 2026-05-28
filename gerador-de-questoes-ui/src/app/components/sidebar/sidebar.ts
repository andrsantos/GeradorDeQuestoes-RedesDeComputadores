import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.html',
  styleUrls: ['./sidebar.scss']
})
export class Sidebar {
  navLinks = [
    { path: '/', label: 'Início', exact: true },
    { path: '/gerenciamento', label: 'Gerenciamento', exact: false },
    { path: '/gerar-prova', label: 'Geração', exact: false },
    { path: '/banco-questoes', label: 'Banco de Questões', exact: false },
    { path: '/provas-salvas', label: 'Provas Salvas', exact: false }
    // { path: '/alimentacao', label: 'Alimentação', exact: false },
  ];
}