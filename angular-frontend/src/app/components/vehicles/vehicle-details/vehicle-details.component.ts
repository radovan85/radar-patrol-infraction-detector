import { Component, inject, OnInit } from '@angular/core';
import { VehicleService } from '../../../services/vehicle.service';
import { OwnerService } from '../../../services/owner.service';
import { Vehicle } from '../../../classes/vehicle';
import { Owner } from '../../../classes/owner';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, Location } from '@angular/common';


@Component({
  selector: 'app-vehicle-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './vehicle-details.component.html',
  styleUrl: './vehicle-details.component.css'
})
export class VehicleDetailsComponent implements OnInit {

  private vehicleService = inject(VehicleService);
  private ownerService = inject(OwnerService);
  private vehicle = new Vehicle;
  private owner = new Owner;
  private route = inject(ActivatedRoute);
  private location = inject(Location);

  ngOnInit(): void {
    const vehicleId = this.route.snapshot.params['vehicleId'];
  
    this.loadVehicleDetails(vehicleId)
      .then(() => {
        if (this.vehicle.ownerId) {
          return this.loadOwnerDetails(this.vehicle.ownerId);
        }
        // uvek vrati Promise<void>
        return Promise.resolve();
      })
      .catch((error) => {
        console.log(`Error loading data: ${error}`);
      });
  }
  
  

  loadVehicleDetails(vehicleId: any): Promise<void> {
    return this.vehicleService.getVehicleDetails(vehicleId)
      .then((response) => {
        this.vehicle = response.data;
      });
  }
  
  loadOwnerDetails(ownerId: any): Promise<void> {
    return this.ownerService.getOwnerDetails(ownerId)
      .then((response) => {
        this.owner = response.data;
      });
  }
  

  getOwner() {
    return this.owner;
  }

  getVehicle() {
    return this.vehicle;
  }

  goBack() {
    this.location.back();
  }

}
