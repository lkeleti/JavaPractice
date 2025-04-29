import { ComponentFixture, TestBed } from '@angular/core/testing';

import { VatRateListComponent } from './vat-rate-list.component';

describe('VatRateListComponent', () => {
  let component: VatRateListComponent;
  let fixture: ComponentFixture<VatRateListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VatRateListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(VatRateListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
