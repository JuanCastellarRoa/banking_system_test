import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { MovimientosRoutingModule } from './movimientos-routing.module';
import { MovimientosListComponent } from './movimientos-list/movimientos-list.component';
import { MovimientosFormComponent } from './movimientos-form/movimientos-form.component';

@NgModule({
  declarations: [MovimientosListComponent, MovimientosFormComponent],
  imports: [SharedModule, MovimientosRoutingModule],
})
export class MovimientosModule {}
