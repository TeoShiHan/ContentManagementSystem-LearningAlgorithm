import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="app-container">
      <nav class="navbar">
        <div class="nav-brand">
          <span class="material-icons">code</span>
          <span class="brand-text">LeetCode Learning System</span>
        </div>
        <div class="nav-links">
          <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">
            <span class="material-icons">table_chart</span> Problems
          </a>
          <a routerLink="/stats" routerLinkActive="active">
            <span class="material-icons">bar_chart</span> Dashboard
          </a>
        </div>
      </nav>
      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styles: [`
    .app-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
    }
    .navbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      padding: 0 24px;
      height: 56px;
      background: var(--bg-secondary);
      border-bottom: 1px solid var(--border-color);
      position: sticky;
      top: 0;
      z-index: 100;
    }
    .nav-brand {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 600;
      color: var(--accent-blue);
    }
    .nav-links {
      display: flex;
      gap: 4px;
    }
    .nav-links a {
      display: flex;
      align-items: center;
      gap: 4px;
      padding: 8px 16px;
      border-radius: 6px;
      color: var(--text-secondary);
      font-size: 14px;
      font-weight: 500;
      transition: all 0.2s;
    }
    .nav-links a:hover {
      background: var(--hover-bg);
      color: var(--text-primary);
      text-decoration: none;
    }
    .nav-links a.active {
      background: var(--bg-card);
      color: var(--accent-blue);
    }
    .nav-links .material-icons {
      font-size: 18px;
    }
    .main-content {
      flex: 1;
      padding: 24px;
    }
  `]
})
export class AppComponent {}
