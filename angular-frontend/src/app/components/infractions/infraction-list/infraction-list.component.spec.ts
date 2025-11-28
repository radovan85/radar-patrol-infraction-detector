import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InfractionListComponent } from './infraction-list.component';

describe('InfractionListComponent', () => {
  let component: InfractionListComponent;
  let fixture: ComponentFixture<InfractionListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InfractionListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InfractionListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
