import { bootstrapApplication } from '@angular/platform-browser';
import { appConfig } from './app/app.config';
import { AppComponent } from './app/app.component';

// Dodaj Foundation import
import 'foundation-sites';

// Deklariši jQuery da ga TypeScript prepozna
declare var $: any;

bootstrapApplication(AppComponent, appConfig)
  .catch((err) => console.error(err));

// Inicijalizacija Foundation-a kada se DOM učita
document.addEventListener('DOMContentLoaded', () => {
  $(document).foundation();
});

