import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EntityLookupModalComponent } from './entity-lookup-modal.component';

describe('EntityLookupModalComponent', () => {
  let component: EntityLookupModalComponent;
  let fixture: ComponentFixture<EntityLookupModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EntityLookupModalComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EntityLookupModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
