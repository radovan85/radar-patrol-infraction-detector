import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class InfractionService {

  private targetUrl = `http://localhost:8080/api/infractions`;

  getInfractionsCount(): Promise<any> {
    return axios.get(`${this.targetUrl}/count`);
  }

  collectAllInfractions(): Promise<any> {
    return axios.get(`${this.targetUrl}`);
  }

  getInfractionDetails(infractionId: any): Promise<any> {
    return axios.get(`${this.targetUrl}/${infractionId}`);
  }
}
