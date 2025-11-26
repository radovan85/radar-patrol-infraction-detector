import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class OwnerService {

  private targetUrl = `http://localhost:8080/api/owners`;

  getOwnersCount(): Promise<any> {
    return axios.get(`${this.targetUrl}/count`);
  }

  collectAllOwners() {
    return axios.get(`${this.targetUrl}`);
  }

  deleteOwner(ownerId: any) {
    return axios.delete(`${this.targetUrl}/${ownerId}`);
  }

  getOwnerDetails(ownerId:any){
    return axios.get(`${this.targetUrl}/${ownerId}`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }

}
