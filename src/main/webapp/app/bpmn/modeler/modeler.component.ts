import { Component, OnInit, ViewChild, ElementRef, OnDestroy } from '@angular/core';
import { HttpResponse, HttpClient } from '@angular/common/http';
import { Subscription } from 'rxjs';
import { JhiEventManager } from 'ng-jhipster';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { ModelerService } from './modeler.service';
import { Validators, FormBuilder } from '@angular/forms';
import { propertiesPanelModule } from 'bpmn-js-properties-panel';
import propertiesProviderModule from 'bpmn-js-properties-panel/lib/provider/bpmn';
import Modeler from 'bpmn-js/lib/Modeler';
import { Observable } from 'rxjs';

@Component({
  selector: 'bpmn-modeler',
  templateUrl: './modeler.component.html',
  // styleUrls: ['./modeler.component.css']
})
export class ModelerComponent implements OnInit, OnDestroy {
  modeler: Modeler;
  pro: propertiesPanelModule;

  // @ViewChild('canvas')
  // canvesRef: ElementRef;

  constructor(
    protected modelerService: ModelerService,
    protected eventManager: JhiEventManager,
    protected modalService: NgbModal,
    public fb: FormBuilder,
    private http: HttpClient
  ) {}

  ngOnDestroy(): void {
    throw new Error('Method not implemented.');
  }

  ngOnInit(): void {
    this.modeler = new Modeler({
      container: '#canvas',
      //width: '100%',
      //height: '600px',
      propertiesPanel: {
        parent: '#properties',
      },
      additionalModules: [
        //propertiesPanelModule,
        //propertiesProviderModule
      ],
      moddleExtensions: {
        //camunda: camundaModdleDescriptor
      },
    });

    // load default bpmn file with only start design
    this.load();
  }

  load(): void {
    this.getExample().subscribe(data => {
      this.modeler.importXML(data, value => this.handleError(value));
    });
  }

  handleError(err: any) {
    if (err) {
      console.warn('Ups, error: ', err);
    }
  }

  public getExample(): Observable<string> {
    const url = 'default.bpmn';
    return this.http.get(url, { responseType: 'text' });
  }
}
