import { Component, inject, OnInit } from '@angular/core';
import { Radar } from '../../../classes/radar';
import { RadarService } from '../../../services/radar.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

@Component({
  selector: 'app-radar-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './radar-list.component.html',
  styleUrl: './radar-list.component.css'
})
export class RadarListComponent implements OnInit {

  private radarList: Radar[] = [];
  private radarService = inject(RadarService);
  private router = inject(Router);
  private paginatedRadars: Radar[] = [];
  private pageSize = 10;
  private currentPage = 1;
  private totalPages = 1;
  isPatrolActive = false;

  ngOnInit(): void {
    Promise.all([
      this.listAllRadars(),
      this.loadPatrolStatus()
    ])

      .catch((error) => {
        console.log(`Error loading data:  ${error}`);
      })
  }

  async listAllRadars(): Promise<void> {
    try {
      const response = await this.radarService.getAllRadars();
      this.radarList = response.data;
      this.totalPages = Math.max(1, Math.ceil(this.radarList.length / this.pageSize));
      this.setPage(1);
    } catch (error) {
      console.error('Failed to load radars', error);
    }
  }

  setPage(page: number) {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.paginatedRadars = this.radarList.slice((page - 1) * this.pageSize, page * this.pageSize);
  }

  deleteRadar(radarId: any) {
    if (confirm(`Remove this radar?\nIt will affect all related data!`)) {
      this.radarService.deleteRadar(radarId)
        .then(() => {
          this.radarList = this.radarList.filter(tempRadar => tempRadar.id !== radarId);

          this.totalPages = Math.max(1, Math.ceil(this.radarList.length / this.pageSize));

          if ((this.currentPage - 1) * this.pageSize >= this.radarList.length && this.currentPage > 1) {
            this.currentPage--;
          }

          this.setPage(this.currentPage);
        })
        .catch((error) => {
          console.log(`Error deleting radar: ${error}`);
          alert(`Error deleting radar!`);
        });
    }
  }

  nextPage() {
    this.setPage(this.currentPage + 1);
  }

  prevPage() {
    this.setPage(this.currentPage - 1);
  }


  getPaginatedRadars() {
    return this.paginatedRadars;
  }

  getCurrentPage() {
    return this.currentPage;
  }

  getTotalPages() {
    return this.totalPages;
  }

  getRadarList() {
    return this.radarList;
  }

  redirect(path: string) {
    this.router.navigate([`/${path}`]);
  }

  activatePatrols() {
    this.radarService.activatePatrols()
      .then(() => {
        alert(`Radar patrol has been activated!`);
        this.isPatrolActive = true;
      })
      .catch((error) => {
        alert(`${error.response.data}`);
      })
  }

  deactivatePatrols() {
    this.radarService.deactivatePatrols()
      .then(() => {
        alert(`Radar patrol has been terminated!`);
        this.isPatrolActive = false;
      })
      .catch((error) => {
        alert(`${error.response.data}`);
      })
  }


  loadPatrolStatus() {
    this.radarService.isPatrolActive()
      .then((response) => {
        this.isPatrolActive = response.data;
      })
  }

}
