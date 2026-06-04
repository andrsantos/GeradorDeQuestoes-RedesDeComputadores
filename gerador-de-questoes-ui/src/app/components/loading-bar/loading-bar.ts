import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoadingService } from '../../services/loading-service/loading-service';

@Component({
  selector: 'app-loading-bar',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div *ngIf="loadingService.loading$ | async" class="top-bar-loader">
      <div class="progress-fill"></div>
    </div>
  `,
  styles: [`
    .top-bar-loader {
      position: fixed;
      top: 0;
      left: 0;
      width: 100%;
      height: 4px;
      z-index: 99999; /* Fica acima de modais e menus */
      background: rgba(59, 130, 246, 0.2);
    }
    .progress-fill {
      height: 100%;
      background: #3b82f6; /* Cor principal da barra */
      width: 0%;
      animation: progress-animation 1.5s infinite ease-in-out;
    }
    @keyframes progress-animation {
      0% { width: 0%; transform: translateX(-100%); }
      50% { width: 50%; transform: translateX(50%); }
      100% { width: 100%; transform: translateX(200%); }
    }
  `]
})
export class LoadingBarComponent {
  constructor(public loadingService: LoadingService) {}
}