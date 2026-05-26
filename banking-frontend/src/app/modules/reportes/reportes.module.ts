import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { ReportesRoutingModule } from './reportes-routing.module';
import { ReportesComponent } from './reportes/reportes.component';

@NgModule({
  declarations: [ReportesComponent],
  imports: [SharedModule, ReportesRoutingModule],
})
export class ReportesModule {}
