import { AfterViewInit, Component, inject } from '@angular/core';
import { ValidationService } from '../../../services/validation.service';
import axios from 'axios';
import { OwnerService } from '../../../services/owner.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-owner-form',
  standalone: true,
  imports: [],
  templateUrl: './owner-form.component.html',
  styleUrl: './owner-form.component.css'
})
export class OwnerFormComponent implements AfterViewInit {

  private ownerService = inject(OwnerService);
  private validationService = inject(ValidationService);
  private router = inject(Router);

  ngAfterViewInit(): void {
    this.configureBirthDatePicker();
    this.executeOwnerForm();
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
        await axios.post(`${this.ownerService.getTargetUrl()}`, {
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

}
