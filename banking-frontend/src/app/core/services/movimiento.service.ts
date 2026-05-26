import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { Movimiento } from '../../shared/models/movimiento.model';

@Injectable({ providedIn: 'root' })
export class MovimientoService {
  private readonly apiUrl = `${environment.apiUrl}/movimientos`;

  constructor(private readonly http: HttpClient) {}

  getAll(): Observable<Movimiento[]> {
    return this.http.get<Movimiento[]>(this.apiUrl);
  }

  getById(id: number): Observable<Movimiento> {
    return this.http.get<Movimiento>(`${this.apiUrl}/${id}`);
  }

  create(movimiento: Movimiento): Observable<Movimiento> {
    return this.http.post<Movimiento>(this.apiUrl, movimiento);
  }

  update(id: number, movimiento: Movimiento): Observable<Movimiento> {
    return this.http.put<Movimiento>(`${this.apiUrl}/${id}`, movimiento);
  }

  patch(id: number, data: Partial<Movimiento>): Observable<Movimiento> {
    return this.http.patch<Movimiento>(`${this.apiUrl}/${id}`, data);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
