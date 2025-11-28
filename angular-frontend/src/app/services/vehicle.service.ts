import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class VehicleService {

  private targetUrl = `http://localhost:8080/api/vehicles`;

  getVehiclesCount(): Promise<any> {
    return axios.get(`${this.targetUrl}/count`);
  }

  collectAllVehicles(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  collectAllVehiclesByOwnerId(ownerId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/byOwnerId/${ownerId}`);
  }

  deleteVehicle(vehicleId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${vehicleId}`);
  }

  getVehicleDetails(vehicleId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${vehicleId}`);
  }

  getVehicleDetailsByRN(regNumber: any): Promise<any> {
    return axios.get(`${this.targetUrl}/byRn/${regNumber}`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }
}
