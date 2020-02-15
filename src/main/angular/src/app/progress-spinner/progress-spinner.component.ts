import { Component, OnInit, TemplateRef, ViewChild, ViewContainerRef } from '@angular/core';
import { Overlay, OverlayRef } from '@angular/cdk/overlay';
import { TemplatePortal } from '@angular/cdk/portal';
import { ProgressSpinnerService } from './progress-spinner.service';

@Component({
  selector: 'app-progress-spinner',
  templateUrl: './progress-spinner.component.html',
  styleUrls: ['./progress-spinner.component.scss']
})
export class ProgressSpinnerComponent implements OnInit {
  private displayProgressSpinner: boolean;
  private overlayRef: OverlayRef;
  @ViewChild('progressSpinnerRef')
  private progressSpinnerRef: TemplateRef<any>;

  constructor(private readonly progressSpinnerService: ProgressSpinnerService,
              private overlay: Overlay, private vcRef: ViewContainerRef) {}

  ngOnInit(): void {
    const progressSpinnerOverlayConfig = {
      hasBackdrop: true,
      positionStrategy: this.overlay.position().global()
            .centerHorizontally()
            .centerVertically()
    };
    this.overlayRef = this.overlay.create(progressSpinnerOverlayConfig);
    this.progressSpinnerService.getSpinnerStatus().subscribe(status => this.setStatus(status));
  }

  setStatus(status: boolean): void {
    if (status && !this.overlayRef.hasAttached() && this.progressSpinnerRef) {
        const templatePortal = new TemplatePortal(this.progressSpinnerRef, this.vcRef);
        console.log('onCheck2', templatePortal);
        console.log('onCheck3', this.progressSpinnerRef);
        this.overlayRef.attach(templatePortal);
    } else if (!status && this.overlayRef.hasAttached()) {
      this.overlayRef.detach();
    }
  }
}
