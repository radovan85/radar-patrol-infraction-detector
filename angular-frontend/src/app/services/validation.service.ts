import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class ValidationService {
  
  validateRadar() {
    var name = (<HTMLInputElement>document.getElementById(`name`)).value.trim();
    var maxSpeed = (<HTMLInputElement>document.getElementById(`maxSpeed`)).value;
    var longitude = (<HTMLInputElement>document.getElementById(`longitude`)).value;
    var latitude = (<HTMLInputElement>document.getElementById(`latitude`)).value;
    var status = (<HTMLSelectElement>document.getElementById(`status`)).value;

    var nameError = document.getElementById(`nameError`);
    var maxSpeedError = document.getElementById(`maxSpeedError`);
    var longitudeError = document.getElementById(`longitudeError`);
    var latitudeError = document.getElementById(`latitudeError`);
    var statusError = document.getElementById(`statusError`);

    var maxSpeedNumber = Number(maxSpeed);
    var longitudeNumber = Number(longitude);
    var latitudeNumber = Number(latitude);
    var returnValue = true;

    if (nameError) {
      if (name === '' || name.length < 2 || name.length > 100) {
        returnValue = false;
        nameError.classList.add('is-visible');
        nameError.innerText = 'Name must be between 2 and 100 characters.';
      } else {
        nameError.classList.remove('is-visible');
        nameError.innerText = '';
      }
    }

    // MAX SPEED
    if (maxSpeedError) {
      if (maxSpeed === '' || maxSpeedNumber < 30 || maxSpeedNumber > 300) {
        returnValue = false;
        maxSpeedError.classList.add('is-visible');
        maxSpeedError.innerText = 'Max speed must be between 30 and 300.';
      } else {
        maxSpeedError.classList.remove('is-visible');
        maxSpeedError.innerText = '';
      }
    }

    // LONGITUDE
    if (longitudeError) {
      if (longitude === '' || longitudeNumber < -180 || longitudeNumber > 180) {
        returnValue = false;
        longitudeError.classList.add('is-visible');
        longitudeError.innerText = 'Longitude must be between -180 and 180.';
      } else {
        longitudeError.classList.remove('is-visible');
        longitudeError.innerText = '';
      }
    }

    // LATITUDE
    if (latitudeError) {
      if (latitude === '' || latitudeNumber < -90 || latitudeNumber > 90) {
        returnValue = false;
        latitudeError.classList.add('is-visible');
        latitudeError.innerText = 'Latitude must be between -90 and 90.';
      } else {
        latitudeError.classList.remove('is-visible');
        latitudeError.innerText = '';
      }
    }

    // STATUS
    if (statusError) {
      if (status === '') {
        returnValue = false;
        statusError.classList.add('is-visible');
        statusError.innerText = 'You must select one of the options.';
      } else {
        statusError.classList.remove('is-visible');
        statusError.innerText = '';
      }
    }
    

    return returnValue;


  }

  validateOwner(){
    var name = (<HTMLInputElement>document.getElementById(`name`)).value.trim();
    var email = (<HTMLInputElement>document.getElementById(`email`)).value.trim();
    var birthDate = (<HTMLInputElement>document.getElementById(`birthDateStr`)).value.trim();

    var nameError = document.getElementById(`nameError`);
    var emailError = document.getElementById(`emailError`);
    var birthDateError = document.getElementById(`birthDateError`);
    var regEmail = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/g;
    var returnValue = true;

    if (nameError) {
      if (name === '' || name.length < 5 || name.length > 60) {
        returnValue = false;
        nameError.classList.add('is-visible');
        nameError.innerText = 'Name must be between 5 and 60 characters.';
      } else {
        nameError.classList.remove('is-visible');
        nameError.innerText = '';
      }
    }

    if (emailError) {
      if (email === `` || email.length > 50 || !regEmail.test(email)) {
        returnValue = false;
        emailError.classList.add('is-visible');
        emailError.innerText = 'Provide valid email!';
      } else {
        emailError.classList.remove('is-visible');
        emailError.innerText = '';
      }
    }

    if (birthDateError) {
      if (birthDate === ``) {
        returnValue = false;
        birthDateError.classList.add('is-visible');
        birthDateError.innerText = 'Birth date is not valid!';
      } else {
        const birth = new Date(birthDate);
        const today = new Date();
        let age = today.getFullYear() - birth.getFullYear();
        const m = today.getMonth() - birth.getMonth();
        if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) {
          age--;
        }
    
        if (age < 18 || age > 100) {
          returnValue = false;
          birthDateError.classList.add('is-visible');
          birthDateError.innerText = 'Owner must be between 18 and 100 years old.';
        } else {
          birthDateError.classList.remove('is-visible');
          birthDateError.innerText = '';
        }
      }
    }
    

    return returnValue;
  }

  validateVehicle() {
    var registrationNumber = (<HTMLInputElement>document.getElementById(`registrationNumber`)).value.trim();
    var brand = (<HTMLInputElement>document.getElementById(`brand`)).value.trim();
    var fiscalPower = (<HTMLInputElement>document.getElementById(`fiscalPower`)).value;
    var model = (<HTMLInputElement>document.getElementById(`model`)).value.trim();
    var manufactureYear = (<HTMLInputElement>document.getElementById(`manufactureYear`)).value;
    var ownerId = (<HTMLSelectElement>document.getElementById(`ownerId`)).value;
  
    var registrationNumberError = document.getElementById(`registrationNumberError`);
    var brandError = document.getElementById(`brandError`);
    var fiscalPowerError = document.getElementById(`fiscalPowerError`);
    var modelError = document.getElementById(`modelError`);
    var manufactureYearError = document.getElementById(`manufactureYearError`);
    var ownerIdError = document.getElementById(`ownerIdError`);
  
    var fiscalPowerNumber = Number(fiscalPower);
    var manufactureYearNumber = Number(manufactureYear);
    var currentYear = new Date().getFullYear();
    var returnValue = true;
  
    // Registration Number
    if (registrationNumberError) {
      if (registrationNumber === '' || registrationNumber.length < 5 || registrationNumber.length > 25) {
        returnValue = false;
        registrationNumberError.classList.add('is-visible');
        registrationNumberError.innerText = 'Registration number must be between 5 and 25 characters.';
      } else {
        registrationNumberError.classList.remove('is-visible');
        registrationNumberError.innerText = '';
      }
    }
  
    // Brand
    if (brandError) {
      if (brand === '' || brand.length < 2 || brand.length > 30) {
        returnValue = false;
        brandError.classList.add('is-visible');
        brandError.innerText = 'Brand must be between 2 and 30 characters.';
      } else {
        brandError.classList.remove('is-visible');
        brandError.innerText = '';
      }
    }
  
    // Fiscal Power
    if (fiscalPowerError) {
      if (fiscalPower === '' || fiscalPowerNumber < 1 || fiscalPowerNumber > 30) {
        returnValue = false;
        fiscalPowerError.classList.add('is-visible');
        fiscalPowerError.innerText = 'Fiscal power must be between 1 and 30.';
      } else {
        fiscalPowerError.classList.remove('is-visible');
        fiscalPowerError.innerText = '';
      }
    }
  
    // Model
    if (modelError) {
      if (model === '' || model.length < 2 || model.length > 30) {
        returnValue = false;
        modelError.classList.add('is-visible');
        modelError.innerText = 'Model must be between 2 and 30 characters.';
      } else {
        modelError.classList.remove('is-visible');
        modelError.innerText = '';
      }
    }
  
    // Manufacture Year
    if (manufactureYearError) {
      if (manufactureYear === '' || manufactureYearNumber < 1960 || manufactureYearNumber > currentYear) {
        returnValue = false;
        manufactureYearError.classList.add('is-visible');
        manufactureYearError.innerText = `Manufacture year must be between 1960 and ${currentYear}.`;
      } else {
        manufactureYearError.classList.remove('is-visible');
        manufactureYearError.innerText = '';
      }
    }
  
    // OwnerId
    if (ownerIdError) {
      if (ownerId === '') {
        returnValue = false;
        ownerIdError.classList.add('is-visible');
        ownerIdError.innerText = 'You must select an owner.';
      } else {
        ownerIdError.classList.remove('is-visible');
        ownerIdError.innerText = '';
      }
    }
  
    return returnValue;
  }
  

  


  validateNumber(event: KeyboardEvent): void {
    var allowedKeys = ['Backspace', 'ArrowLeft', 'ArrowRight', 'Tab'];
  
    if (!/^[\d.-]$/.test(event.key) && !allowedKeys.includes(event.key)) {
      event.preventDefault();
    }
  
    if (event.key === '-' && (event.target as HTMLInputElement).selectionStart !== 0) {
      event.preventDefault();
    }
  }
  
}
