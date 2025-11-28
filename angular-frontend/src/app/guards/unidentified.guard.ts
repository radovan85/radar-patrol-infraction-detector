import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { inject } from '@angular/core';

export var unidentifiedGuard: CanActivateFn = (route, state) => {
  
  var authService = inject(AuthService);
  var router = inject(Router);
  var returnValue = false;

  if (authService.isAuthenticated()) {
    router.navigate([`home`]);
  } else {
    returnValue = true;
  }

  return returnValue;
};