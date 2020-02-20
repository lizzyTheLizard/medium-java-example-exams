import { Component, OnInit, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { Overlay, OverlayRef, OverlayConfig } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import { ProgressSpinnerService } from './progress-spinner.service';

@Component({
  selector: 'app-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class ProgressSpinnerComponent implements OnInit {
  private overlayRef: OverlayRef;
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
    this.progressSpinnerService.getSpinnerStatus().subscribe(status => this.statusUpdate(status));
  }

  private statusUpdate(status: boolean): void {
    if (status) {
      this.attach();
    } else {
      this.detatch();
    }
  }

  private attach(): void {
    if (this.overlayRef) {
      return;
    }
    this.overlayRef = this.overlay.create(this.progressSpinnerOverlayConfig);
    const templatePortal = new TemplatePortal(this.progressSpinnerRef, this.vcRef);
    this.overlayRef.attach(templatePortal);
  }

  private detatch() {
    if (!this.overlayRef) {
      return;
    }
    this.overlayRef.dispose();
    this.overlayRef = null;
  }
}
