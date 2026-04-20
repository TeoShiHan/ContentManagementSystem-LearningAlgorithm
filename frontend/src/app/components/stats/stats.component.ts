import { Component, OnInit, ElementRef, ViewChild, AfterViewInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ProblemService } from '../../services/problem.service';
import { Stats } from '../../models/problem.model';
import Chart from 'chart.js/auto';

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent implements OnInit {
  stats: Stats | null = null;

  @ViewChild('difficultyChart') difficultyChartRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('typeChart') typeChartRef!: ElementRef<HTMLCanvasElement>;

  private difficultyChart: Chart | null = null;
  private typeChart: Chart | null = null;

  constructor(private problemService: ProblemService) {}

  ngOnInit(): void {
    this.problemService.getStats().subscribe({
      next: (stats) => {
        this.stats = stats;
        setTimeout(() => this.renderCharts(), 100);
      },
      error: (err) => console.error('Failed to load stats:', err)
    });
  }

  renderCharts(): void {
    if (!this.stats) return;

    // Difficulty Pie Chart
    if (this.difficultyChartRef) {
      this.difficultyChart?.destroy();
      this.difficultyChart = new Chart(this.difficultyChartRef.nativeElement, {
        type: 'doughnut',
        data: {
          labels: ['Easy', 'Medium', 'Hard'],
          datasets: [{
            data: [this.stats.easyCount, this.stats.mediumCount, this.stats.hardCount],
            backgroundColor: ['#66bb6a', '#ffa726', '#ef5350'],
            borderColor: '#1a1a2e',
            borderWidth: 3
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          plugins: {
            legend: {
              position: 'bottom',
              labels: { color: '#e0e0e0', font: { size: 12 } }
            }
          }
        }
      });
    }

    // Question Type Bar Chart
    if (this.typeChartRef && this.stats.byQuestionType) {
      const entries = Object.entries(this.stats.byQuestionType);
      this.typeChart?.destroy();
      this.typeChart = new Chart(this.typeChartRef.nativeElement, {
        type: 'bar',
        data: {
          labels: entries.map(([key]) => key.replace(/_/g, ' ')),
          datasets: [{
            label: 'Problems',
            data: entries.map(([, val]) => val),
            backgroundColor: '#4fc3f7',
            borderRadius: 4
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          indexAxis: 'y',
          plugins: {
            legend: { display: false }
          },
          scales: {
            x: {
              ticks: { color: '#a0a0b0', stepSize: 1 },
              grid: { color: 'rgba(255,255,255,0.05)' }
            },
            y: {
              ticks: { color: '#a0a0b0', font: { size: 11 } },
              grid: { display: false }
            }
          }
        }
      });
    }
  }

  get solvedPercentage(): number {
    return this.stats?.totalProblems ? 100 : 0;
  }
}
