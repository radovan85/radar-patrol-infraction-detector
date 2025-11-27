import { Component, inject, OnInit } from "@angular/core";
import { OwnerService } from "../../services/owner.service";
import { RadarService } from "../../services/radar.service";
import { VehicleService } from "../../services/vehicle.service";
import { InfractionService } from "../../services/infraction.service";
import { Router, RouterLink } from "@angular/router";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  private radarsCount: number = 0;
  private ownersCount: number = 0;
  private vehiclesCount: number = 0;
  private infractionsCount: number = 0;

  private ownerService = inject(OwnerService);
  private radarService = inject(RadarService);
  private vehicleService = inject(VehicleService);
  private infractionService = inject(InfractionService);
  private router = inject(Router);

  ngOnInit(): void {
    Promise.all([
      this.loadOwnersCount(),
      this.loadRadarsCount(),
      this.loadVehiclesCount(),
      this.loadInfractionsCount()
    ]).catch((error) => {
      console.log(`Error loading data: ${error}`);
    });
  }

  async loadRadarsCount() {
    const response = await this.radarService.getRadarsCount();
    return this.radarsCount = response.data;
  }

  async loadOwnersCount() {
    const response = await this.ownerService.getOwnersCount();
    return this.ownersCount = response.data;
  }

  async loadVehiclesCount() {
    const response = await this.vehicleService.getVehiclesCount();
    return this.vehiclesCount = response.data;
  }

  async loadInfractionsCount() {
    const response = await this.infractionService.getInfractionsCount();
    return this.infractionsCount = response.data;
  }

  public getRadarsCount(): number {
    return this.radarsCount;
  }

  public getOwnersCount(): number {
    return this.ownersCount;
  }

  public getVehiclesCount(): number {
    return this.vehiclesCount;
  }

  public getInfractionsCount(): number {
    return this.infractionsCount;
  }

  redirect(path: string) {
    this.router.navigate([`/${path}`]); 
  }
  

  
}
