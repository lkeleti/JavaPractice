import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment.development';
import { BarcodeDto } from '../models/barcode.dto';

@Injectable({ providedIn: 'root' })
export class BarcodeService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    exists(code: string): Observable<BarcodeDto> {
        return this.http.get<BarcodeDto>(`${this.apiUrl}/barcodes/${encodeURIComponent(code)}`);
    }

    generate(): Observable<string> {
        return this.http.put<string>(`${this.apiUrl}/barcodes`, {});
    }
}
