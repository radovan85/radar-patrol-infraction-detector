import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VehicleUpdateFormComponent } from './vehicle-update-form.component';

describe('VehicleUpdateFormComponent', () => {
  let component: VehicleUpdateFormComponent;
  let fixture: ComponentFixture<VehicleUpdateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VehicleUpdateFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VehicleUpdateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
