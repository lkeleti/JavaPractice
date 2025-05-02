import { Component, HostListener, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule, FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ManufacturerService } from '../../services/manufacturer.service';
import { ManufacturerDto } from '../../models/manufacturer.dto';
import { UpdateManufacturerCommand } from '../../models/update-manufacturer-command';
import { CreateManufacturerCommand } from '../../models/create-manufacturer-command';

@Component({
  selector: 'app-manufacturer-form',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './manufacturer-form.component.html'
})

export class ManufacturerFormComponent implements OnInit {
  form!: FormGroup;
  isEditMode = false;
  manufacturerId?: number;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private router: Router,
    private manufacturerService: ManufacturerService
  ) { }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.required],
      website: ['']
    });

    const idParam = this.route.snapshot.paramMap.get('id');
    if (idParam) {
      this.isEditMode = true;
      this.manufacturerId = +idParam;
      this.loadManufacturer(this.manufacturerId);
    }
  }

  loadManufacturer(id: number): void {
    this.manufacturerService.findManufacturerById(id).subscribe({
      next: (manufacturer) => this.form.patchValue(manufacturer),
      error: () => alert('Nem sikerült betölteni a gyártó adatait.')
    });
  }

  submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const updateManufacturerCommand: UpdateManufacturerCommand = this.form.value;
    const createManufacturerCommand: CreateManufacturerCommand = this.form.value;

    if (this.isEditMode && this.manufacturerId) {
      this.manufacturerService.updateManufacturer(this.manufacturerId, updateManufacturerCommand).subscribe({
        next: () => this.router.navigate(['/master-data/manufacturers']),
        error: () => alert('Nem sikerült frissíteni a gyártót.')
      });
    } else {
      this.manufacturerService.createManufacturer(createManufacturerCommand).subscribe({
        next: () => this.router.navigate(['/master-data/manufacturers']),
        error: () => alert('Nem sikerült létrehozni a gyártót.')
      });
    }
  }

  cancel(): void {
    this.router.navigate(['/master-data/manufacturers']);
  }

  @HostListener('window:keydown.escape', ['$event'])
  handleEscape(event: KeyboardEvent): void {
    this.cancel();
  }
}

