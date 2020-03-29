import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import './vendor';
import { DemoJhipsterSharedModule } from 'app/shared/shared.module';
import { DemoJhipsterCoreModule } from 'app/core/core.module';
import { DemoJhipsterAppRoutingModule } from './app-routing.module';
import { DemoJhipsterHomeModule } from './home/home.module';
import { DemoJhipsterEntityModule } from './entities/entity.module';
// jhipster-needle-angular-add-module-import JHipster will add new module here
import { MainComponent } from './layouts/main/main.component';
import { NavbarComponent } from './layouts/navbar/navbar.component';
import { FooterComponent } from './layouts/footer/footer.component';
import { PageRibbonComponent } from './layouts/profiles/page-ribbon.component';
import { ErrorComponent } from './layouts/error/error.component';

@NgModule({
  imports: [
    BrowserModule,
    DemoJhipsterSharedModule,
    DemoJhipsterCoreModule,
    DemoJhipsterHomeModule,
    // jhipster-needle-angular-add-module JHipster will add new module here
    DemoJhipsterEntityModule,
    DemoJhipsterAppRoutingModule
  ],
  declarations: [MainComponent, NavbarComponent, ErrorComponent, PageRibbonComponent, FooterComponent],
  bootstrap: [MainComponent]
})
export class DemoJhipsterAppModule {}
