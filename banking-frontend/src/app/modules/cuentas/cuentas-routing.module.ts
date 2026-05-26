import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { CuentasListComponent } from './cuentas-list/cuentas-list.component';

const routes: Routes = [{ path: '', component: CuentasListComponent }];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule],
})
export class CuentasRoutingModule {}
