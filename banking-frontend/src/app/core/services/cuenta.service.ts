import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Cuenta } from '../../shared/models/cuenta.model';

@Injectable({ providedIn: 'root' })
export class CuentaService {
  private readonly apiUrl = `${environment.apiUrl}/cuentas`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Cuenta[]> {
    return this.http.get<Cuenta[]>(this.apiUrl);
  }

  getById(numeroCuenta: number): Observable<Cuenta> {
    return this.http.get<Cuenta>(`${this.apiUrl}/${numeroCuenta}`);
  }

  create(cuenta: Cuenta): Observable<Cuenta> {
    return this.http.post<Cuenta>(this.apiUrl, cuenta);
  }

  update(numeroCuenta: number, cuenta: Cuenta): Observable<Cuenta> {
    return this.http.put<Cuenta>(`${this.apiUrl}/${numeroCuenta}`, cuenta);
  }

  patch(numeroCuenta: number, data: Partial<Cuenta>): Observable<Cuenta> {
    return this.http.patch<Cuenta>(`${this.apiUrl}/${numeroCuenta}`, data);
  }

  delete(numeroCuenta: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${numeroCuenta}`);
  }
}
