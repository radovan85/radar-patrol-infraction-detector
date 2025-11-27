import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { VehicleService } from '../../../services/vehicle.service';
import { Router } from '@angular/router';
import { Owner } from '../../../classes/owner';
import { OwnerService } from '../../../services/owner.service';
import { ValidationService } from '../../../services/validation.service';
import axios from 'axios';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-vehicle-form',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vehicle-form.component.html',
  styleUrl: './vehicle-form.component.css'
})
export class VehicleFormComponent implements OnInit, AfterViewInit {

  private ownerList: Owner[] = [];
  private ownerService = inject(OwnerService);
  private vehicleService = inject(VehicleService);
  private router = inject(Router);
  private validationService = inject(ValidationService);

  ngOnInit(): void {
    Promise.all([
      this.loadAllOwners()
    ])

      .catch((error) => {
        console.log(`Error loading data:  ${error}`);
      })
  }

  ngAfterViewInit(): void {
    this.executeVehicleForm();
  }

  loadAllOwners() {
    this.ownerService.collectAllOwners()
      .then((response) => {
        this.ownerList = response.data;
      })
  }


  executeVehicleForm() {
    var form = document.getElementById(`vehicleForm`) as HTMLFormElement;
    var ownerSearch = document.getElementById(`ownerSearch`) as HTMLInputElement;
    var ownerIdHidden = document.getElementById(`ownerId`) as HTMLInputElement;
  
    // Hook: kad se promeni ownerSearch, pronađi ownera po prikazanom tekstu
    ownerSearch.addEventListener(`change`, () => {
      var selectedText = ownerSearch.value.trim().toLowerCase();
      var matchedOwner = this.ownerList.find(o =>
        `${o.name} (${o.email})`.toLowerCase() === selectedText
      );
  
      ownerIdHidden.value = matchedOwner?.id?.toString() ?? ``;
    });
  
    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();
  
      // fallback: ako ownerIdHidden nije popunjen, pokušaj da ga pronađeš pre validacije
      if (!ownerIdHidden.value) {
        var fallbackText = ownerSearch.value.trim().toLowerCase();
        var fallbackOwner = this.ownerList.find(o =>
          `${o.name} (${o.email})`.toLowerCase() === fallbackText
        );
        ownerIdHidden.value = fallbackOwner?.id?.toString() ?? ``;
      }
  
      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });
  
      if (this.validationService.validateVehicle()) {
        try {
          await axios.post(`${this.vehicleService.getTargetUrl()}`, {
            registrationNumber: serializedData[`registrationNumber`],
            brand: serializedData[`brand`],
            fiscalPower: serializedData[`fiscalPower`],
            model: serializedData[`model`],
            manufactureYear: serializedData[`manufactureYear`],
            ownerId: ownerIdHidden.value // koristi direktno hidden polje
          });
  
          this.redirect(`/vehicles`);
        } catch (error: any) {
          console.log(error);
          if (error.response && error.response.status === 409) {
            alert(error.response.data);
          } else {
            alert(`Failed`);
          }
        }
      }
    });
  }
  




  redirect(path: string) {
    this.router.navigate([`/${path}`]);
  }

  getOwnerList() {
    return this.ownerList;
  }

  validateNumber(event: any) {
    return this.validationService.validateNumber(event);
  }

}
