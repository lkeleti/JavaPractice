import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvoiceTypeListComponent } from './invoice-type-list.component';

describe('InvoiceTypeListComponent', () => {
  let component: InvoiceTypeListComponent;
  let fixture: ComponentFixture<InvoiceTypeListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [InvoiceTypeListComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(InvoiceTypeListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
