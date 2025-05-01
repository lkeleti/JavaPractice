import { Observable } from 'rxjs';
import {
  Component,
  ElementRef,
  OnInit,
  ViewChild,
  HostListener,
  Input,
  QueryList,
  ViewChildren
} from '@angular/core';
import { NgbActiveModal } from '@ng-bootstrap/ng-bootstrap';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-entity-lookup-modal',
  templateUrl: './entity-lookup-modal.component.html',
  styleUrls: ['./entity-lookup-modal.component.scss'],
  standalone: true,
  imports: [CommonModule, FormsModule]
})
export class EntityLookupModalComponent<T extends { id: number }> implements OnInit {
  @Input() title: string = '';
  @Input() fetchFunction!: (page: number, pageSize: number, search: string) => Observable<T[]>;
  @Input() displayFn!: (item: T) => { label: string; subLabel?: string };

  items: T[] = [];
  searchTerm = '';
  page = 1;
  pageSize = 20;
  isLoading = false;
  allLoaded = false;
  selectedIndex = 0;

  @ViewChild('scrollContainer') scrollContainer!: ElementRef<HTMLDivElement>;
  @ViewChildren('itemElement') itemElements!: QueryList<ElementRef<HTMLDivElement>>;

  constructor(public activeModal: NgbActiveModal) { }

  ngOnInit(): void {
    this.loadItems();
  }

  ngAfterViewInit(): void {
    const container = this.scrollContainer.nativeElement;
    container.addEventListener('scroll', () => {
      const { scrollTop, scrollHeight, clientHeight } = container;
      if (scrollTop + clientHeight >= scrollHeight - 10 && !this.allLoaded) {
        this.loadItems();
      }
    });
  }

  loadItems(): void {
    if (this.isLoading || this.allLoaded) return;
    this.isLoading = true;

    this.fetchFunction(this.page, this.pageSize, this.searchTerm).subscribe({
      next: (newItems) => {
        this.items = [...this.items, ...newItems];
        if (newItems.length < this.pageSize) this.allLoaded = true;
        this.page++;
        this.isLoading = false;
      },
      error: () => {
        this.isLoading = false;
      }
    });
  }

  onSearch(): void {
    this.page = 1;
    this.items = [];
    this.allLoaded = false;
    this.selectedIndex = 0;
    this.loadItems();
  }

  @HostListener('window:keydown', ['$event'])
  handleKeyDown(event: KeyboardEvent): void {
    const maxIndex = this.items.length - 1;

    if (event.key === 'ArrowDown') {
      event.preventDefault();
      if (this.selectedIndex < maxIndex) {
        this.selectedIndex++;
        this.scrollToSelected();
      }
    } else if (event.key === 'ArrowUp') {
      event.preventDefault();
      if (this.selectedIndex > 0) {
        this.selectedIndex--;
        this.scrollToSelected();
      }
    } else if (event.key === 'Enter') {
      event.preventDefault();
      this.confirmSelection();
    } else if (event.key === 'Escape') {
      event.preventDefault();
      this.cancel();
    } else if (event.key.length === 1 && !event.ctrlKey && !event.metaKey) {
      this.searchTerm += event.key;
      this.onSearch();
    } else if (event.key === 'Backspace') {
      this.searchTerm = this.searchTerm.slice(0, -1);
      this.onSearch();
    }
  }

  select(index: number): void {
    this.selectedIndex = index;
    this.confirmSelection();
  }

  confirmSelection(): void {
    const selected = this.items[this.selectedIndex];
    if (selected) this.activeModal.close(selected.id);
  }

  cancel(): void {
    this.activeModal.dismiss(-1);
  }

  scrollToSelected(): void {
    const element = this.itemElements.get(this.selectedIndex);
    if (element) {
      element.nativeElement.scrollIntoView({ block: 'nearest', behavior: 'smooth' });
    }
  }
}
