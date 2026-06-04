import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Sidebar } from './components/sidebar/sidebar';
import { LoadingBarComponent } from './components/loading-bar/loading-bar';

@Component({
  selector: 'app-root',
  standalone: true, 
  imports: [RouterOutlet, Sidebar, LoadingBarComponent],
  templateUrl: './app.html',
  styleUrl: './app.scss' 
})
export class App {
  protected readonly title = signal('gerador-de-questoes-ui');
}