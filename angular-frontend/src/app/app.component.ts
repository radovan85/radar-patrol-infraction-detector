import { Component } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet,CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent {
  title = 'radar-frontend';

  /*
  constructor(public router: Router) {}

  // Proverava da li je trenutna ruta login
  isLoginRoute(): boolean {
    return this.router.url === '/login';
  }
    */
}
