import { CommonModule } from "@angular/common";
import { Component, Input, OnInit } from "@angular/core";
import { FormsModule } from "@angular/forms"; // Csak FormsModule kell az ngModel-hez
import { NgbActiveModal } from "@ng-bootstrap/ng-bootstrap";
import { SerialNumberDto } from "../../models/serial-number.dto";

@Component({
  selector: 'app-serial-number-manager-modal',
  templateUrl: './serial-number-manager-modal.component.html',
  standalone: true,
  // Csak CommonModule és FormsModule kell
  imports: [CommonModule, FormsModule]
})
export class SerialNumberManagerModalComponent implements OnInit {
  // Input most már DTO tömböt vár
  @Input() initialSerialNumbers: SerialNumberDto[] = [];

  // Belső tárolás is DTO tömbbel
  serialNumbers: SerialNumberDto[] = [];
  // Keresőmezőhöz property
  searchTerm: string = '';

  // Hozzáadás/Törlés property-k eltávolítva (newSerialNumber, errorMessage)

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    this.serialNumbers = JSON.parse(JSON.stringify(this.initialSerialNumbers));
  }

  // Getter a szűrt lista megjelenítéséhez
  get filteredSerialNumbers(): SerialNumberDto[] {
    if (!this.searchTerm) {
      return this.serialNumbers; // Ha nincs keresőszó, a teljes listát adjuk vissza
    }
    const term = this.searchTerm.toLowerCase();
    return this.serialNumbers.filter(sn =>
      sn.serial.toLowerCase().includes(term)
    );
  }

  // Mentéskor visszaadjuk a teljes (potenciálisan módosított) belső listát
  save(): void {
    // Opcionális: Ellenőrizhetjük itt, hogy a módosított gyáriszámok egyediek-e még a listán belül.
    const editedSerials = this.serialNumbers.map(sn => sn.serial);
    const uniqueEditedSerials = new Set(editedSerials);
    if (uniqueEditedSerials.size !== editedSerials.length) {
      alert('Hiba: A módosított gyári számok között duplikátum található!');
      return; // Ne zárjuk be a modalt hiba esetén
    }
    // Opcionális: Ellenőrizhetjük, hogy üres gyáriszámot adtak-e meg.
    if (this.serialNumbers.some(sn => !sn.serial || sn.serial.trim() === '')) {
      alert('Hiba: Egyik gyári szám sem lehet üres!');
      return;
    }

    this.activeModal.close(this.serialNumbers);
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }

  // Eseménykezelő, hogy az Enter leütése ne zárja be a formot/modalt véletlenül az inputban
  onInputKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      event.preventDefault(); // Megakadályozzuk az alapértelmezett Enter viselkedést (pl. form submit)
    }
  }
}