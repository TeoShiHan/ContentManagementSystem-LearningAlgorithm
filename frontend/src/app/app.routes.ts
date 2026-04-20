import { Routes } from '@angular/router';
import { ProblemTableComponent } from './components/problem-table/problem-table.component';
import { StatsComponent } from './components/stats/stats.component';

export const routes: Routes = [
  { path: '', component: ProblemTableComponent },
  { path: 'stats', component: StatsComponent },
  { path: '**', redirectTo: '' }
];
