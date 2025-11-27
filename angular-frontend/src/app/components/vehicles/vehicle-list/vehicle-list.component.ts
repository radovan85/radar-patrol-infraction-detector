import { Component, inject, OnInit } from '@angular/core';
import { VehicleService } from '../../../services/vehicle.service';
import { Vehicle } from '../../../classes/vehicle';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

// 👇 Import sort utils
import { sortByKey, SortDir } from '../../../utils/sort-utils';

@Component({
  selector: 'app-vehicle-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vehicle-list.component.html',
  styleUrl: './vehicle-list.component.css'
})
export class VehicleListComponent implements OnInit {

  private vehicleService = inject(VehicleService);
  private vehicleList: Vehicle[] = [];
  private paginatedVehicles: Vehicle[] = [];
  private pageSize = 12;
  private currentPage = 1;
  private totalPages = 1;
  private router = inject(Router);

  openActionsId: number | null = null;

  // 🔹 Sortiranje
  sortKey: keyof Vehicle | null = null;
  sortDir: SortDir = 'asc';

  ngOnInit(): void {
    this.listAllVehicles().catch((error) => {
      console.log(`Error loading data:  ${error}`);
    });
  }

  toggleActions(id?: number) {
    this.openActionsId = this.openActionsId === id ? null : id ?? null;
  }

  async listAllVehicles() {
    const response = await this.vehicleService.collectAllVehicles();
    this.vehicleList = response.data;
    this.totalPages = Math.max(1, Math.ceil(this.vehicleList.length / this.pageSize));
    this.applySort(); // default sort ako želiš
    this.setPage(1);
  }

  deleteVehicle(vehicleId: any) {
    if (confirm(`Remove this vehicle?\nIt will affect all related data!`)) {
      this.vehicleService.deleteVehicle(vehicleId)
        .then(() => {
          this.vehicleList = this.vehicleList.filter(tempVehicle => tempVehicle.id !== vehicleId);
          this.totalPages = Math.max(1, Math.ceil(this.vehicleList.length / this.pageSize));

          if ((this.currentPage - 1) * this.pageSize >= this.vehicleList.length && this.currentPage > 1) {
            this.currentPage--;
          }

          this.setPage(this.currentPage);
        })
        .catch((error) => {
          console.log(`Error deleting vehicle: ${error}`);
        });
    }
  }

  // 🔹 Sortiranje
  sortBy(key: keyof Vehicle) {
    if (this.sortKey === key) {
      this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    } else {
      this.sortKey = key;
      this.sortDir = 'asc';
    }
    this.applySort();
    this.setPage(1);
  }

  private applySort() {
    if (!this.sortKey) return;
    this.vehicleList = sortByKey(this.vehicleList, this.sortKey, this.sortDir);
  }

  // 🔹 Paginacija
  setPage(page: number) {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.paginatedVehicles = this.vehicleList.slice((page - 1) * this.pageSize, page * this.pageSize);
  }

  nextPage() {
    this.setPage(this.currentPage + 1);
  }

  prevPage() {
    this.setPage(this.currentPage - 1);
  }

  getPaginatedVehicles() {
    return this.paginatedVehicles;
  }

  getCurrentPage() {
    return this.currentPage;
  }

  getTotalPages() {
    return this.totalPages;
  }

  redirect(path: string) {
    this.router.navigate([`/${path}`]);
  }
}
