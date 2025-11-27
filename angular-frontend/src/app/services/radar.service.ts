import { Injectable } from '@angular/core';
import axios from 'axios';

@Injectable({
  providedIn: 'root'
})
export class RadarService {

  private targetUrl = `http://localhost:8080/api/radars`;


  getRadarsCount() {
    return axios.get(`${this.targetUrl}/count`);
  }

  getAllRadars() {
    return axios.get(`${this.targetUrl}`);
  }

  getRadarDetails(radarId: any) {
    return axios.get(`${this.targetUrl}/${radarId}`);
  }

  deleteRadar(radarId: any): Promise<any> {
    return axios.delete(`${this.targetUrl}/${radarId}`);
  }

  activatePatrols(): Promise<any> {
    return axios.get(`${this.targetUrl}/activatePatrol`);
  }

  deactivatePatrols(): Promise<any> {
    return axios.get(`${this.targetUrl}/deactivatePatrol`);
  }

  isPatrolActive(): Promise<any> {
    return axios.get(`${this.targetUrl}/patrol/status`);
  }

  getTargetUrl() {
    return this.targetUrl;
  }




}
