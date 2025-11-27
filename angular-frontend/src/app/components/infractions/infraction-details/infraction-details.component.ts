import { Component, inject, OnInit } from '@angular/core';
import { OwnerService } from '../../../services/owner.service';
import { RadarService } from '../../../services/radar.service';
import { VehicleService } from '../../../services/vehicle.service';
import { InfractionService } from '../../../services/infraction.service';
import { ActivatedRoute, Router } from '@angular/router';
import { Owner } from '../../../classes/owner';
import { Radar } from '../../../classes/radar';
import { Infraction } from '../../../classes/infraction';
import { Vehicle } from '../../../classes/vehicle';
import { CommonModule, Location } from '@angular/common';

@Component({
  selector: 'app-infraction-details',
  imports: [CommonModule],
  standalone: true,
  templateUrl: './infraction-details.component.html',
  styleUrl: './infraction-details.component.css'
})
export class InfractionDetailsComponent implements OnInit {

  private ownerService = inject(OwnerService);
  private radarService = inject(RadarService);
  private vehicleService = inject(VehicleService);
  private infractionService = inject(InfractionService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private owner = new Owner;
  private radar = new Radar;
  private infraction = new Infraction;
  private vehicle = new Vehicle;
  private location = inject(Location);


  ngOnInit(): void {
    const infId = this.route.snapshot.params['infId'];
    this.loadInfractionDetails(infId);
  }

  loadInfractionDetails(infractionId: any) {
    this.infractionService.getInfractionDetails(infractionId)
      .then((response) => {
        this.infraction = response.data;

        // kad imamo infraction, učitavamo vehicle i radar
        if (this.infraction.vehicleRegistrationNumber) {
          this.loadVehicleDetails(this.infraction.vehicleRegistrationNumber);
        }
        if (this.infraction.radarId) {
          this.loadRadarDetails(this.infraction.radarId);
        }
      })
      .catch((error) => {
        console.error('Error loading infraction', error);
      });
  }

  loadVehicleDetails(regNumber: any) {
    this.vehicleService.getVehicleDetailsByRN(regNumber)
      .then((response) => {
        this.vehicle = response.data;

        // kad imamo vehicle, učitavamo owner
        if (this.vehicle.ownerId) {
          this.loadOwnerDetails(this.vehicle.ownerId);
        }
      })
      .catch((error) => {
        console.error('Error loading vehicle', error);
      });
  }

  loadOwnerDetails(ownerId: any) {
    this.ownerService.getOwnerDetails(ownerId)
      .then((response) => {
        this.owner = response.data;
      })
      .catch((error) => {
        console.error('Error loading owner', error);
      });
  }

  loadRadarDetails(radarId: any) {
    this.radarService.getRadarDetails(radarId)
      .then((response) => {
        this.radar = response.data;
      })
      .catch((error) => {
        console.error('Error loading radar', error);
      });
  }


  getOwner() {
    return this.owner;
  }

  getVehicle() {
    return this.vehicle;
  }

  getInfraction() {
    return this.infraction;
  }

  getRadar() {
    return this.radar;
  }

  goBack() {
    this.location.back();
  }

}
