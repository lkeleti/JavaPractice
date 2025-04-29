import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VatRateFormComponent } from './vat-rate-form.component';

describe('VatRateFormComponent', () => {
  let component: VatRateFormComponent;
  let fixture: ComponentFixture<VatRateFormComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VatRateFormComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VatRateFormComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
