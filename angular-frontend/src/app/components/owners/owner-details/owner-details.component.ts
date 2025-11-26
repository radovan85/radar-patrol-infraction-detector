import { Component, inject, OnInit } from '@angular/core';
import { OwnerService } from '../../../services/owner.service';
import { VehicleService } from '../../../services/vehicle.service';
import { Owner } from '../../../classes/owner';
import { Vehicle } from '../../../classes/vehicle';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, Location } from '@angular/common';

@Component({
  selector: 'app-owner-details',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './owner-details.component.html',
  styleUrl: './owner-details.component.css'
})
export class OwnerDetailsComponent implements OnInit {

  private ownerService = inject(OwnerService);
  private vehicleService = inject(VehicleService);
  private owner = new Owner;
  private vehicleList: Vehicle[] = [];
  private route = inject(ActivatedRoute);
  private location = inject(Location);

  ngOnInit(): void {
    Promise.all([
      this.loadOwnerDetails(this.route.snapshot.params[`ownerId`]),
      this.loadVehicles(this.route.snapshot.params[`ownerId`])
    ])

      .catch((error) => {
        console.log(`Error loading data:   ${error}`);
      })
  }

  loadOwnerDetails(ownerId: any) {
    this.ownerService.getOwnerDetails(ownerId)
      .then((response) => {
        this.owner = response.data;
      })
  }

  loadVehicles(ownerId: any) {
    this.vehicleService.collectAllVehiclesByOwnerId(ownerId)
      .then((response) => {
        this.vehicleList = response.data;
      })
  }

  getOwner() {
    return this.owner;
  }

  getVehicleList() {
    return this.vehicleList;
  }

  goBack() {
    this.location.back();
  }



}
