import { Routes } from '@angular/router';
import { modelerRoute } from './modeler/modeler.route';

const BPMN_ROUTES = [modelerRoute];

export const bpmnState: Routes = [
  {
    path: '',
    children: BPMN_ROUTES,
  },
];
