import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceTypeFromComponent } from './invoice-type-form.component';

describe('InvoiceTypeFromComponent', () => {
  let component: InvoiceTypeFromComponent;
  let fixture: ComponentFixture<InvoiceTypeFromComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceTypeFromComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(InvoiceTypeFromComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
