import { AfterViewInit, Component, inject, OnInit } from '@angular/core';
import { Radar } from '../../../classes/radar';
import { RadarService } from '../../../services/radar.service';
import { ValidationService } from '../../../services/validation.service';
import { ActivatedRoute, Router } from '@angular/router';
import axios from 'axios';

@Component({
  selector: 'app-radar-update-form',
  imports: [],
  standalone: true,
  templateUrl: './radar-update-form.component.html',
  styleUrl: './radar-update-form.component.css'
})
export class RadarUpdateFormComponent implements OnInit, AfterViewInit {


  private currentRadar: Radar = new Radar;
  private radarService = inject(RadarService);
  private validationService = inject(ValidationService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);

  ngOnInit(): void {
    this.getRadarDetails(this.route.snapshot.params[`radarId`]);
  }

  ngAfterViewInit(): void {
    this.executeRadarUpdateForm();
  }

  getRadarDetails(radarId: any): void {
    this.radarService.getRadarDetails(radarId)
      .then((response) => {
        this.currentRadar = response.data;
      })
      .catch((error) => {
        console.log(`Error loading radar details: ${error}`);
      });
  }


  executeRadarUpdateForm() {
    var form = document.getElementById(`radarForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRadar()) {
        await axios.put(`${this.radarService.getTargetUrl()}/${this.currentRadar.id}`, {
          name: serializedData[`name`],
          maxSpeed: serializedData[`maxSpeed`],
          status: serializedData[`status`],
          longitude: serializedData[`longitude`],
          latitude: serializedData[`latitude`]
        })
          .then(() => {
            this.redirect(`/radars`);
          })

          .catch((error) => {
            console.log(error);
            alert(`Failed`);
          });
      }
    });
  }

  validateNumber(event: any) {
    return this.validationService.validateNumber(event);
  }

  redirect(path: string) {
    this.router.navigate([`/${path}`]);
  }

  getCurrentRadar() {
    return this.currentRadar;
  }

}
