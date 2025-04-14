import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.scss']
})
export class ConfirmationDialogComponent {

  @Input() title: string = 'Confirm Action'; // Alapértelmezett cím
  @Input() message: string = 'Are you sure you want to proceed?'; // Alapértelmezett üzenet
  @Input() confirmText: string = 'Confirm'; // Megerősítés gomb szövege
  @Input() cancelText: string = 'Cancel'; // Mégse gomb szövege

  // Injectáljuk az NgbActiveModal-t, ezzel tudjuk bezárni az ablakot
  constructor(public activeModal: NgbActiveModal) { }

  // Ha a felhasználó a megerősítésre kattint
  confirm(): void {
    // Bezárjuk a modált 'true' értékkel jelezve a megerősítést
    this.activeModal.close(true);
  }

  // Ha a felhasználó a Mégse gombra vagy az 'x'-re kattint
  dismiss(): void {
    // Bezárjuk a modált egy 'cancel' indokkal (vagy bármi mással)
    this.activeModal.dismiss('cancel click');
  }

}
