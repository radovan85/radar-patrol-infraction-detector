import { AfterViewInit, Component, inject } from '@angular/core';
import { RadarService } from '../../../services/radar.service';
import { ValidationService } from '../../../services/validation.service';
import axios from 'axios';
import { Router } from '@angular/router';

@Component({
  selector: 'app-radar-form',
  standalone: true,
  imports: [],
  templateUrl: './radar-form.component.html',
  styleUrl: './radar-form.component.css'
})
export class RadarFormComponent implements AfterViewInit {

  private radarService = inject(RadarService);
  private validationService = inject(ValidationService);
  private router = inject(Router);


  ngAfterViewInit(): void {
    this.executeRadarForm();
  }


  executeRadarForm() {
    var form = document.getElementById(`radarForm`) as HTMLFormElement;

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      var formData = new FormData(form);
      var serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      if (this.validationService.validateRadar()) {
        await axios.post(`${this.radarService.getTargetUrl()}`, {
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
}
