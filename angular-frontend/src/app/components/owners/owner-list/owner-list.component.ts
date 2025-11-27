import { Component, inject, OnInit } from '@angular/core';
import { Owner } from '../../../classes/owner';
import { OwnerService } from '../../../services/owner.service';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { sortByKey, SortDir } from '../../../utils/sort-utils';



@Component({
  selector: 'app-owner-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-list.component.html',
  styleUrl: './owner-list.component.css'
})
export class OwnerListComponent implements OnInit {

  private ownerService = inject(OwnerService);

  private ownerList: Owner[] = [];
  private paginatedOwners: Owner[] = [];
  private pageSize = 12;
  private currentPage = 1;
  private totalPages = 1;
  private router = inject(Router);

  openActionsId: number | null = null;

  // 🔹 Sortiranje
  sortKey: keyof Owner | null = null;
  sortDir: SortDir = 'asc';

  ngOnInit(): void {
    this.listAllOwners();
  }

  listAllOwners() {
    this.ownerService.collectAllOwners()
      .then((response) => {
        this.ownerList = response.data;
        this.totalPages = Math.max(1, Math.ceil(this.ownerList.length / this.pageSize));
        this.applySort(); // ako želiš default sort
        this.setPage(1);
      })
      .catch((error) => {
        console.log(`Error loading owners:  ${error}`);
      });
  }

  toggleActions(id?: number) {
    this.openActionsId = this.openActionsId === id ? null : id ?? null;
  }

  editOwner(owner: Owner) {
    console.log('Edit', owner.id);
  }

  deleteOwner(ownerId: any) {
    if (confirm(`Remove this owner?\nIt will affect all related data!`)) {
      this.ownerService.deleteOwner(ownerId)
        .then(() => {
          this.ownerList = this.ownerList.filter(tempOwner => tempOwner.id !== ownerId);
          this.totalPages = Math.max(1, Math.ceil(this.ownerList.length / this.pageSize));

          if ((this.currentPage - 1) * this.pageSize >= this.ownerList.length && this.currentPage > 1) {
            this.currentPage--;
          }

          this.setPage(this.currentPage);
        })
        .catch((error) => {
          console.log(`Error deleting owner: ${error}`);
        });
    }
  }

  // 🔹 Sortiranje
  sortBy(key: keyof Owner) {
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
    this.ownerList = sortByKey(this.ownerList, this.sortKey, this.sortDir);
  }

  // 🔹 Paginacija
  setPage(page: number) {
    if (page < 1 || page > this.totalPages) {
      return;
    }
    this.currentPage = page;
    this.paginatedOwners = this.ownerList.slice((page - 1) * this.pageSize, page * this.pageSize);
  }

  nextPage() {
    this.setPage(this.currentPage + 1);
  }

  prevPage() {
    this.setPage(this.currentPage - 1);
  }

  getPaginatedOwners() {
    return this.paginatedOwners;
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
