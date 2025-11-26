import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { ValidationService } from '../../../services/validation.service';
import { ActivatedRoute, Router } from '@angular/router';
import { OwnerService } from '../../../services/owner.service';
import { Owner } from '../../../classes/owner';
import axios from 'axios';

@Component({
  selector: 'app-owner-update-form',
  standalone: true,
  imports: [],
  templateUrl: './owner-update-form.component.html',
  styleUrl: './owner-update-form.component.css'
})
export class OwnerUpdateFormComponent implements OnInit, AfterViewInit {


  private ownerService = inject(OwnerService);
  private validationService = inject(ValidationService);
  private router = inject(Router);
  private currentOwner = new Owner;
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.getOwnerDetails(this.route.snapshot.params[`ownerId`]);
  }

  ngAfterViewInit(): void {
    this.configureBirthDatePicker();
    this.executeOwnerForm();
  }

  formatDateForInput(dateStr?: string): string {
    if (!dateStr) return '';
  
    // Pretpostavimo da je dateStr u formatu dd/MM/yyyy
    const parts = dateStr.split('/');
    if (parts.length === 3) {
      const dd = parts[0];
      const mm = parts[1];
      const yyyy = parts[2];
      return `${yyyy}-${mm.padStart(2, '0')}-${dd.padStart(2, '0')}`;
    }
  
    // Ako je već yyyy-MM-dd, samo ga vrati
    return dateStr;
  }
  

  getOwnerDetails(ownerId: any) {
    this.ownerService.getOwnerDetails(ownerId)
      .then((response) => {
        this.currentOwner = response.data;
      })

      .catch((error) => {
        console.log(`Error loading owner details:  ${error}`)
      })
  }

  private configureBirthDatePicker(): void {
    const input = document.getElementById('birthDateStr') as HTMLInputElement;

    if (input) {
      const today = new Date();

      // Minimalni datum = danas minus 100 godina
      const minDate = new Date(today.getFullYear() - 100, today.getMonth(), today.getDate());

      // Maksimalni datum = danas minus 18 godina
      const maxDate = new Date(today.getFullYear() - 18, today.getMonth(), today.getDate());

      // Formatiraj u yyyy-MM-dd (HTML date format)
      const formatDate = (d: Date) => {
        const yyyy = d.getFullYear();
        const mm = String(d.getMonth() + 1).padStart(2, '0');
        const dd = String(d.getDate()).padStart(2, '0');
        return `${yyyy}-${mm}-${dd}`;
      };

      input.min = formatDate(minDate);
      input.max = formatDate(maxDate);
    }
  }

  executeOwnerForm() {
    var form = document.getElementById(`ownerForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateOwner()) {
        await axios.put(`${this.ownerService.getTargetUrl()}/${this.currentOwner.id}`, {
          name: serializedData[`name`],
          email: serializedData[`email`],
          birthDateStr: serializedData[`birthDateStr`]
        })
          .then(() => {
            this.redirect(`/owners`);
          })

          .catch((error) => {
            console.log(error);
            if (error.response.status === 409) {
              alert(error.response.data);
            } else {
              alert(`Failed`);
            }

          });
      }
    });
  }

  redirect(path: string) {
    this.router.navigate([`/${path}`]);
  }

  getCurrentOwner() {
    return this.currentOwner;
  }

}



