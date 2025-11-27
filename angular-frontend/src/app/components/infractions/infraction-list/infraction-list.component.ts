import { CommonModule } from '@angular/common';
import { Component, inject, OnInit } from '@angular/core';
import { InfractionService } from '../../../services/infraction.service';
import { Router } from '@angular/router';
import { Infraction } from '../../../classes/infraction';

// 👇 Import sort utils
import { sortByKey, SortDir } from '../../../utils/sort-utils';

@Component({
  selector: 'app-infraction-list',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './infraction-list.component.html',
  styleUrl: './infraction-list.component.css'
})
export class InfractionListComponent implements OnInit {

  private paginatedInfractions: Infraction[] = [];
  private infractionList: Infraction[] = [];
  private router = inject(Router);
  private infractionService = inject(InfractionService);
  private pageSize = 10;
  private currentPage = 1;
  private totalPages = 1;

  // 🔹 Sortiranje
  sortKey: keyof Infraction | null = null;
  sortDir: SortDir = 'asc';

  ngOnInit(): void {
    this.loadAllInfractions().catch((error) => {
      console.log(`Error loading data:  ${error}`);
    });
  }

  async loadAllInfractions() {
    const response = await this.infractionService.collectAllInfractions();
    this.infractionList = response.data;
    this.totalPages = Math.max(1, Math.ceil(this.infractionList.length / this.pageSize));
    this.applySort(); // default sort ako želiš
    this.setPage(1);
  }

  // 🔹 Sortiranje
  sortBy(key: keyof Infraction) {
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
    this.infractionList = sortByKey(this.infractionList, this.sortKey, this.sortDir);
  }

  // 🔹 Paginacija
  setPage(page: number) {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.paginatedInfractions = this.infractionList.slice((page - 1) * this.pageSize, page * this.pageSize);
  }

  nextPage() {
    this.setPage(this.currentPage + 1);
  }

  prevPage() {
    this.setPage(this.currentPage - 1);
  }

  getPaginatedInfractions() {
    return this.paginatedInfractions;
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
