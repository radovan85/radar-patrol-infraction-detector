import { Component, inject } from '@angular/core';
import { AuthenticationRequest } from '../../classes/authentication-request';
import { AuthService } from '../../services/auth.service';
import { User } from '../../classes/user';
import axios from 'axios';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  private authRequest: AuthenticationRequest = new AuthenticationRequest();
  private authService = inject(AuthService);

  ngAfterViewInit(): void {
    this.executeLoginForm();
  }

  public getAuthRequest(): AuthenticationRequest {
    return this.authRequest;
  }

  executeLoginForm() {
    const form = document.getElementById(`loginForm`) as HTMLFormElement;
    const alertMessage = document.getElementById(`error-message`);
    let authUser: User = new User();

    form.addEventListener(`submit`, async (event) => {
      event.preventDefault();

      const formData = new FormData(form);
      const serializedData: { [key: string]: string } = {};
      formData.forEach((value, key) => {
        serializedData[key] = value.toString().trim();
      });

      await axios.post(`${this.authService.getTargetUrl()}api/auth/login`, {
        username: serializedData[`email`],
        password: serializedData[`password`]
      })
      .then((response) => {
        localStorage.setItem(`currentUser`, JSON.stringify(response));
        authUser = response.data;

        const tokenStr = authUser.authToken;
        if (tokenStr) {
          const authToken = `Bearer ${tokenStr}`;
          localStorage.setItem(`authToken`, authToken);
        }

        if (alertMessage) {
          alertMessage.style.visibility = `hidden`;
        }

        console.log(`Login completed!`);
        window.location.reload();
      })
      .catch(() => {
        if (alertMessage) {
          alertMessage.style.visibility = `visible`;
        }
      });
    });
  }
}
