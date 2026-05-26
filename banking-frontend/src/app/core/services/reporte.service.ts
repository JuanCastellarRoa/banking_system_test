import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '../../../environments/environment';
import { ReporteParams, ReporteResponse } from '../../shared/models/reporte.model';

@Injectable({ providedIn: 'root' })
export class ReporteService {
  private readonly apiUrl = `${environment.apiUrl}/reportes`;

  constructor(private readonly http: HttpClient) {}

  getReporte(params: ReporteParams): Observable<ReporteResponse> {
    const httpParams = new HttpParams()
      .set('fechaInicio', params.fechaInicio)
      .set('fechaFin', params.fechaFin)
      .set('cliente', params.cliente.toString());

    return this.http.get<ReporteResponse>(this.apiUrl, { params: httpParams });
  }
}
