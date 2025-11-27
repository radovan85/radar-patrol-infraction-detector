import { Routes } from '@angular/router';
import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { LayoutComponent } from './components/layout/layout.component';
import { unidentifiedGuard } from './guards/unidentified.guard';
import { authGuard } from './guards/auth.guard';
import { RadarFormComponent } from './components/radars/radar-form/radar-form.component';
import { RadarListComponent } from './components/radars/radar-list/radar-list.component';
import { RadarUpdateFormComponent } from './components/radars/radar-update-form/radar-update-form.component';
import { OwnerListComponent } from './components/owners/owner-list/owner-list.component';
import { OwnerFormComponent } from './components/owners/owner-form/owner-form.component';
import { OwnerUpdateFormComponent } from './components/owners/owner-update-form/owner-update-form.component';
import { OwnerDetailsComponent } from './components/owners/owner-details/owner-details.component';
import { VehicleListComponent } from './components/vehicles/vehicle-list/vehicle-list.component';
import { VehicleDetailsComponent } from './components/vehicles/vehicle-details/vehicle-details.component';
import { VehicleFormComponent } from './components/vehicles/vehicle-form/vehicle-form.component';
import { VehicleUpdateFormComponent } from './components/vehicles/vehicle-update-form/vehicle-update-form.component';
import { InfractionListComponent } from './components/infractions/infraction-list/infraction-list.component';
import { InfractionDetailsComponent } from './components/infractions/infraction-details/infraction-details.component';

export const routes: Routes = [



    {
        path: `login`,
        component: LoginComponent,
        canActivate: [unidentifiedGuard]
    },

    {
        path: ``,
        component: LayoutComponent,
        canActivate: [authGuard],
        children: [
            { path: `home`, component: HomeComponent },
            { path: `radars/addRadar`, component: RadarFormComponent },
            { path: `radars`, component: RadarListComponent },
            { path: `radars/updateRadar/:radarId`, component: RadarUpdateFormComponent },
            { path: `owners`, component: OwnerListComponent },
            { path: `owners/addOwner`, component: OwnerFormComponent },
            { path: `owners/updateOwner/:ownerId`, component: OwnerUpdateFormComponent },
            { path: `owners/ownerDetails/:ownerId`, component: OwnerDetailsComponent },
            { path: `vehicles`, component: VehicleListComponent },
            { path: `vehicles/vehicleDetails/:vehicleId`, component: VehicleDetailsComponent },
            { path: `vehicles/addVehicle`, component: VehicleFormComponent },
            { path: `vehicles/updateVehicle/:vehicleId`, component: VehicleUpdateFormComponent },
            { path: `infractions`, component: InfractionListComponent },
            { path: `infractions/infractionDetails/:infId`, component: InfractionDetailsComponent }
        ]
    },

    {
        path: `**`,
        redirectTo: `/home`,
        pathMatch: `full`
    }
];
