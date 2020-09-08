import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

import { JhipsterSampleApplicationSharedModule } from 'app/shared/shared.module';
import { ModelerComponent } from './modeler/modeler.component';
import { bpmnState } from './bpmn.route';

@NgModule({
  imports: [JhipsterSampleApplicationSharedModule, RouterModule.forChild(bpmnState)],
  declarations: [ModelerComponent],
})
export class BpmnModule {}
