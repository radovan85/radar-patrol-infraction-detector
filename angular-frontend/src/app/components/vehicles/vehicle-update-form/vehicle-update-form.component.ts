import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { Owner } from '../../../classes/owner';
import { OwnerService } from '../../../services/owner.service';
import { CommonModule } from '@angular/common';
import { VehicleService } from '../../../services/vehicle.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ValidationService } from '../../../services/validation.service';
import axios from 'axios';
import { Vehicle } from '../../../classes/vehicle';

@Component({
  selector: 'app-vehicle-update-form',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './vehicle-update-form.component.html',
  styleUrl: './vehicle-update-form.component.css'
})
export class VehicleUpdateFormComponent implements OnInit, AfterViewInit {

  private ownerList: Owner[] = [];
  private ownerService = inject(OwnerService);
  private vehicleService = inject(VehicleService);
  private router = inject(Router);
  private validationService = inject(ValidationService);
  private currentVehicle = new Vehicle;
  private route = inject(ActivatedRoute);

  // flags da znamo kad su podaci spremni
  private ownersLoaded = false;
  private vehicleLoaded = false;

  ngOnInit(): void {
    Promise.all([
      this.loadAllOwners(),
      this.loadCurrentVehicle(this.route.snapshot.params[`vehicleId`])
    ])
      .then(() => {
        // kad oba završe, pokušaj prefill
        this.tryPrefillOwner();
      })
      .catch((error) => {
        console.log(`Error loading data:  ${error}`);
      })
  }

  ngAfterViewInit(): void {
    this.executeVehicleForm();
    // u slučaju da su podaci već ranije stigli, pokušaj prefill i posle mount-a
    this.tryPrefillOwner();
  }

  loadAllOwners() {
    return this.ownerService.collectAllOwners()
      .then((response) => {
        this.ownerList = response.data;
        this.ownersLoaded = true;
        this.tryPrefillOwner();
      })
  }

  loadCurrentVehicle(vehicleId: any) {
    return this.vehicleService.getVehicleDetails(vehicleId)
      .then((response) => {
        this.currentVehicle = response.data;
        this.vehicleLoaded = true;
        this.tryPrefillOwner();
      })
  }

  // Helper: kad su oba spremni i DOM postoji, popuni ownerSearch i hidden ownerId
  private tryPrefillOwner() {
    if (!this.ownersLoaded || !this.vehicleLoaded) {
      return;
    }

    var ownerSearch = document.getElementById(`ownerSearch`) as HTMLInputElement | null;
    var ownerIdHidden = document.getElementById(`ownerId`) as HTMLInputElement | null;

    if (!ownerSearch || !ownerIdHidden) {
      return; // DOM još nije spreman
    }

    var currentOwnerId = this.currentVehicle?.ownerId?.toString();
    if (!currentOwnerId) {
      return;
    }

    var matchedOwner = this.ownerList.find(o => o.id?.toString() === currentOwnerId);
    if (matchedOwner) {
      ownerSearch.value = `${matchedOwner.name} (${matchedOwner.email})`;
      ownerIdHidden.value = matchedOwner.id?.toString() ?? ``;
    }
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
          await axios.put(`${this.vehicleService.getTargetUrl()}/${this.currentVehicle.id}`, {
            registrationNumber: serializedData[`registrationNumber`],
            brand: serializedData[`brand`],
            fiscalPower: serializedData[`fiscalPower`],
            model: serializedData[`model`],
            manufactureYear: serializedData[`manufactureYear`],
            ownerId: ownerIdHidden.value
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

  getCurrentVehicle() {
    return this.currentVehicle;
  }

}
