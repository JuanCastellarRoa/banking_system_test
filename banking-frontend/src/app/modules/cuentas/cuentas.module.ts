import { NgModule } from '@angular/core';
import { SharedModule } from '../../shared/shared.module';
import { CuentasRoutingModule } from './cuentas-routing.module';
import { CuentasListComponent } from './cuentas-list/cuentas-list.component';
import { CuentasFormComponent } from './cuentas-form/cuentas-form.component';

@NgModule({
  declarations: [CuentasListComponent, CuentasFormComponent],
  imports: [SharedModule, CuentasRoutingModule],
})
export class CuentasModule {}
