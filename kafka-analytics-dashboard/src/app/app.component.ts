
import {
  Component,
  OnInit,
  OnDestroy,
  AfterViewInit,
  ViewChild,
  ElementRef,
  inject
} from '@angular/core';

import { CommonModule } from '@angular/common';

import {
  timer,
  Subscription,
  exhaustMap,
  catchError,
  of
} from 'rxjs';

import Chart from 'chart.js/auto';

import {
  AnalyticsService,
  AnalyticsData
} from './services/analytics.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent
  implements OnInit, AfterViewInit, OnDestroy {

  @ViewChild('barCanvas')
  barCanvas!: ElementRef<HTMLCanvasElement>;

  @ViewChild('pieCanvas')
  pieCanvas!: ElementRef<HTMLCanvasElement>;

  private analyticsService = inject(AnalyticsService);

  private subscription?: Subscription;

  private barChart?: Chart;
  private pieChart?: Chart;

  statistics: AnalyticsData = {};

  totalVisits = 0;
  activePages = 0;
  topPage = '-';

  connected = false;
  lastUpdate: Date | null = null;

  pages = ['P1', 'P2', 'P3', 'P4', 'P5'];

  colors = [
    '#3b82f6',
    '#14b8a6',
    '#8b5cf6',
    '#f59e0b',
    '#ec4899'
  ];

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {

    this.createCharts();

    this.subscription = timer(0, 1000).pipe(

      exhaustMap(() =>
        this.analyticsService.getAnalytics().pipe(

          catchError(error => {
            console.error('Erreur API :', error);
            this.connected = false;
            return of(null);
          })

        )
      )

    ).subscribe(data => {

      if (data !== null) {
        this.connected = true;
        this.updateStatistics(data);
      }

    });
  }

  updateStatistics(data: AnalyticsData): void {

    this.statistics = data;

    this.totalVisits = Object.values(data)
      .reduce((total, count) => total + count, 0);

    this.activePages = Object.keys(data)
      .filter(page => data[page] > 0).length;

    const entries = Object.entries(data)
      .sort((a, b) => b[1] - a[1]);

    this.topPage = entries.length > 0
      ? entries[0][0]
      : '-';

    this.lastUpdate = new Date();

    const values = this.pages.map(
      page => data[page] || 0
    );

    if (this.barChart) {
      this.barChart.data.datasets[0].data = values;
      this.barChart.update('none');
    }

    if (this.pieChart) {
      this.pieChart.data.datasets[0].data = values;
      this.pieChart.update('none');
    }
  }

  createCharts(): void {

    this.barChart = new Chart(
      this.barCanvas.nativeElement,
      {
        type: 'bar',
        data: {
          labels: this.pages,
          datasets: [{
            label: 'Nombre de visites',
            data: [0, 0, 0, 0, 0],
            backgroundColor: this.colors,
            borderRadius: 6
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: false,
          plugins: {
            legend: {
              display: false
            }
          },
          scales: {
            x: {
              ticks: {
                color: '#cbd5e1'
              },
              grid: {
                display: false
              }
            },
            y: {
              beginAtZero: true,
              ticks: {
                color: '#cbd5e1',
                precision: 0
              },
              grid: {
                color: '#334155'
              }
            }
          }
        }
      }
    );

    this.pieChart = new Chart(
      this.pieCanvas.nativeElement,
      {
        type: 'doughnut',
        data: {
          labels: this.pages,
          datasets: [{
            data: [0, 0, 0, 0, 0],
            backgroundColor: this.colors,
            borderWidth: 0
          }]
        },
        options: {
          responsive: true,
          maintainAspectRatio: false,
          animation: false,
          cutout: '65%',
          plugins: {
            legend: {
              position: 'bottom',
              labels: {
                color: '#cbd5e1',
                padding: 20
              }
            }
          }
        }
      }
    );
  }

  getPercentage(page: string): number {

    if (this.totalVisits === 0) {
      return 0;
    }

    return (
      (this.statistics[page] || 0)
      / this.totalVisits
    ) * 100;
  }

  ngOnDestroy(): void {

    this.subscription?.unsubscribe();

    this.barChart?.destroy();
    this.pieChart?.destroy();
  }
}
