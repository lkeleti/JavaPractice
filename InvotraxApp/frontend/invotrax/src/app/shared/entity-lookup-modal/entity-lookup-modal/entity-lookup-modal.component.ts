import { Component, EventEmitter, Input, Output, OnInit, OnDestroy, HostListener } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { debounceTime, Subject, Subscription } from 'rxjs';

export interface LookupItem {
  id: number;
  label: string;
  subLabel?: string;
}

@Component({
  selector: 'app-entity-lookup-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './entity-lookup-modal.component.html',
  styleUrls: ['./entity-lookup-modal.component.scss']
})
export class EntityLookupModalComponent implements OnInit, OnDestroy {
  @Input() title = 'Elem keresése';
  @Input() items: LookupItem[] = [];

  @Output() selected = new EventEmitter<LookupItem>();
  @Output() cancelled = new EventEmitter<void>();

  searchKey: string = '';
  filteredItems: LookupItem[] = [];
  selectedIndex: number = 0;
  keyListener!: (event: KeyboardEvent) => void;

  ngOnInit(): void {
    this.filteredItems = [...this.items];
    this.keyListener = this.handleGlobalKeyDown.bind(this);
    document.addEventListener('keydown', this.keyListener);
  }

  ngOnDestroy(): void {
    document.removeEventListener('keydown', this.keyListener);
  }

  onKeyDown(event: KeyboardEvent): void {
    const maxIndex = this.filteredItems.length - 1;

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      if (this.selectedIndex < maxIndex) {
        this.selectedIndex++;
      }
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      if (this.selectedIndex > 0) {
        this.selectedIndex--;
      }
    } else if (event.key === 'Enter') {
      event.preventDefault();
      this.confirmSelection();
    } else if (event.key === 'Escape') {
      event.preventDefault();
      this.cancel();
    } else if (event.key.length === 1 && !event.ctrlKey && !event.metaKey) {
      this.searchKey += event.key;
      this.filterItems();
    } else if (event.key === 'Backspace') {
      this.searchKey = this.searchKey.slice(0, -1);
      this.filterItems();
    }
  }

  handleGlobalKeyDown(event: KeyboardEvent): void {
    // fókusztól függetlenül figyeljük a beírt karaktereket
    this.onKeyDown(event);
  }

  filterItems(): void {
    const key = this.searchKey.toLowerCase();
    this.filteredItems = this.items.filter(item =>
      item.label.toLowerCase().includes(key) ||
      item.subLabel?.toLowerCase().includes(key)
    );
    this.selectedIndex = 0;
  }

  selectItem(index: number): void {
    this.selectedIndex = index;
  }

  confirmSelection(): void {
    const selectedItem = this.filteredItems[this.selectedIndex];
    if (selectedItem) {
      this.selected.emit(selectedItem);
    }
  }

  cancel(): void {
    this.cancelled.emit();
  }
}