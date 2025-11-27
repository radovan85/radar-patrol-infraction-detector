import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RadarUpdateFormComponent } from './radar-update-form.component';

describe('RadarUpdateFormComponent', () => {
  let component: RadarUpdateFormComponent;
  let fixture: ComponentFixture<RadarUpdateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RadarUpdateFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RadarUpdateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
