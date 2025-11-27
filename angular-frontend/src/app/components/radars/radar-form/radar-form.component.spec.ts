import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RadarFormComponent } from './radar-form.component';

describe('RadarFormComponent', () => {
  let component: RadarFormComponent;
  let fixture: ComponentFixture<RadarFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RadarFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RadarFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
