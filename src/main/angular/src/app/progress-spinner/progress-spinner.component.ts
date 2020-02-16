import { Component, DoCheck, OnInit, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { Overlay, OverlayRef, OverlayConfig } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import { ProgressSpinnerService } from './progress-spinner.service';

@Component({
  selector: 'app-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class ProgressSpinnerComponent implements OnInit, DoCheck {
  private overlayRef: OverlayRef;
  private status: boolean;
  private progressSpinnerOverlayConfig: OverlayConfig;

  @ViewChild('progressSpinnerRef')
  private progressSpinnerRef: TemplateRef<any>;

  constructor(private readonly progressSpinnerService: ProgressSpinnerService,
              private overlay: Overlay, private vcRef: ViewContainerRef) {}

  ngOnInit(): void {
    this.progressSpinnerOverlayConfig = {
      hasBackdrop: true,
      positionStrategy: this.overlay.position().global()
            .centerHorizontally()
            .centerVertically()
    };
    this.progressSpinnerService.getSpinnerStatus().subscribe(status => this.status = status);
  }

  ngDoCheck(){
    if (this.status && !this.overlayRef) {
      this.overlayRef = this.overlay.create(this.progressSpinnerOverlayConfig);
      const templatePortal = new TemplatePortal(this.progressSpinnerRef, this.vcRef);
      this.overlayRef.attach(templatePortal);
    } else if (!this.status && this.overlayRef) {
      this.overlayRef.dispose();
      this.overlayRef = null;
    }
  }
}
