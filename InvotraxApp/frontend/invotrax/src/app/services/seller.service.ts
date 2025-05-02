import { Injectable } from '@angular/core';
import { environment } from '../environments/environment.development';
import { HttpClient } from '@angular/common/http';
import { PaymentMethodDto } from '../models/paymentMethod.dto';
import { Observable } from 'rxjs';
import { UpdateSellerCommand } from '../models/update-seller-commad';
import { CreateSellerCommand } from '../models/create-seller-command';
import { SellerCompanyProfileDto } from '../models/seller-company-profile.dto';
import id from '@angular/common/locales/id';

@Injectable({
    providedIn: 'root'
})
export class SellerService {
    private apiUrl = environment.apiUrl;
    constructor(private http: HttpClient) { }

    /**
    * Lekérdezi az összes fizetési módot a backend API-ról.
    * @returns Observable, ami PaymentMethodDto tömböt fog kibocsátani.
    */

    getSeller(): Observable<SellerCompanyProfileDto> {
        return this.http.get<SellerCompanyProfileDto>(`${this.apiUrl}/seller-profile`);
    }

    createSeller(id: number, sellerCommand: CreateSellerCommand) {
        return this.http.post<SellerCompanyProfileDto>(`${this.apiUrl}/seller-profile`, sellerCommand);
    }

    updateSeller(id: number, sellerCommand: UpdateSellerCommand) {
        console.log(id);
        console.log(sellerCommand);
        return this.http.put<SellerCompanyProfileDto>(`${this.apiUrl}/seller-profile`, sellerCommand);
    }

}