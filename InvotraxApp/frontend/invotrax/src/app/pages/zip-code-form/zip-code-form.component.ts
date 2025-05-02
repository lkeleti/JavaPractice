import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ZipCodeService } from '../../services/zip-code.service';
import { ZipCodeDto } from '../../models/zip-code.dto';
import { UpdateZipCodeCommand } from '../../models/update-zip-code-command';
import { CreateZipCodeCommand } from '../../models/create-zip-code-command';

@Component({
  selector: 'app-zip-code-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './zip-code-form.component.html'
})

export class ZipCodeFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  zipCodeId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private zipCodeService: ZipCodeService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      zip: ['', Validators.required],
      city: ['', Validators.required],
      deleted: [false] // csak szerkesztésnél használjuk
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.zipCodeId = +idParam;
      this.loadZipCode(this.zipCodeId);
    } else {
      this.form.removeControl('deleted'); // create módban felesleges
    }
  }

  loadZipCode(id: number): void {
    this.zipCodeService.findZipCodeById(id).subscribe({
      next: (zip) => this.form.patchValue(zip),
      error: () => alert('Nem sikerült betölteni az irányítószám adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const updateZipCodeCommand: UpdateZipCodeCommand = this.form.value;
    const createZipCodeCommand: CreateZipCodeCommand = this.form.value;

    if (this.isEditMode && this.zipCodeId) {
      this.zipCodeService.updateZipCode(this.zipCodeId, updateZipCodeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/zip-codes']),
        error: () => alert('Nem sikerült frissíteni az irányítószámot.')
      });
    } else {
      this.zipCodeService.createZipCode(createZipCodeCommand).subscribe({
        next: () => this.router.navigate(['/master-data/zip-codes']),
        error: () => alert('Nem sikerült létrehozni az irányítószámot.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/zip-codes']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}
