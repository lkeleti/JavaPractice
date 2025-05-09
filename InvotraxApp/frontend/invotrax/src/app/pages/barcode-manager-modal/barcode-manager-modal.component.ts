import { BarcodeDto } from './../../models/barcode.dto';
import { Component, Input } from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { BarcodeService } from '../../services/barcode-service';
import { FormsModule, NgModel, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-barcode-manager-modal',
  templateUrl: './barcode-manager-modal.component.html',
  imports: [CommonModule, ReactiveFormsModule, FormsModule]
})
export class BarcodeManagerModalComponent {
  @Input() initialBarcodes: BarcodeDto[] = [];

  barcodes: BarcodeDto[] = [];
  newBarcode: string = '';
  errorMessage: string = '';

  constructor(
    public activeModal: NgbActiveModal,
    private barcodeService: BarcodeService
  ) { }

  ngOnInit(): void {
    this.barcodes = [...this.initialBarcodes];
  }

  addBarcode(): void {
    const code = this.newBarcode.trim();

    if (code.length !== 13) {
      this.errorMessage = 'A vonalkód pontosan 13 számjegyből kell álljon.';
      return;
    }

    if (!this.isValidEan13(code)) {
      this.errorMessage = 'Érvénytelen EAN-13 vonalkód (hibás ellenőrző szám).';
      return;
    }

    if (!code) return;
    if (this.barcodes.some(b => b.code === code)) {
      this.errorMessage = 'Ez a vonalkód már szerepel a listában.';
      return;
    }

    this.barcodeService.exists(code).subscribe({
      next: (existingBarcode: BarcodeDto) => {
        if (existingBarcode != null) {
          this.errorMessage = 'Ez a vonalkód már egy másik termékhez tartozik.';
        } else {
          this.barcodes.push({ id: 0, code, isGenerated: false });
          this.newBarcode = '';
          this.errorMessage = '';
        }
      },
      error: (err) => {
        this.errorMessage = 'Hiba történt az ellenőrzés során.';
      }
    });
  }

  removeBarcode(index: number): void {
    this.barcodes.splice(index, 1);
  }

  save(): void {
    this.activeModal.close(this.barcodes);
  }

  dismiss(): void {
    this.activeModal.dismiss();
  }

  isValidEan13(code: string): boolean {
    if (!/^\d{13}$/.test(code)) return false;

    const digits = code.split('').map(d => +d);
    const checksum = digits.pop()!;
    const sum = digits.reduce((acc, val, i) => acc + val * (i % 2 === 0 ? 1 : 3), 0);
    const calculated = (10 - (sum % 10)) % 10;

    return checksum === calculated;
  }

  generateBarcode(): void {
    this.barcodeService.generate().subscribe({
      next: (generated: string) => {
        if (this.barcodes.some(b => b.code === generated)) {
          this.errorMessage = 'A generált vonalkód már szerepel a listában.';
        } else {
          this.barcodes.push({
            id: 0,
            code: generated,
            isGenerated: true
          });
          this.errorMessage = '';
        }
      },
      error: () => {
        this.errorMessage = 'Hiba történt a vonalkód generálása során.';
      }
    });
  }

  get hasGeneratedBarcode(): boolean {
    return this.barcodes.some(b => b.isGenerated);
  }
}
