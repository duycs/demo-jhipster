import { Route } from '@angular/router';

import { ModelerComponent } from './modeler.component';

export const modelerRoute: Route = {
  path: 'modeler',
  component: ModelerComponent,
  data: {
    authorities: [],
    pageTitle: 'activate.title',
  },
};
