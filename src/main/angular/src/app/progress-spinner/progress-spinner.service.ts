import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProgressSpinnerService {
  private subject: BehaviorSubject<boolean> = new BehaviorSubject(false);
  private levels = 0;
  private switchOfTimerRef: any;

  constructor() { }

  enable(): void {
    this.levels++;
    if (this.switchOfTimerRef) {
      clearTimeout(this.switchOfTimerRef);
      this.switchOfTimerRef = null;
    } else if (!this.subject.value) {
      this.subject.next(true);
    }
  }

  disable(): void {
    if (this.levels === 0) {
      console.error('Reached level below zero, set it to zero');
      return;
    }

    this.levels--;
    if (this.levels === 0 && this.subject.value) {
      this.switchOfTimerRef = setTimeout(() => {
        this.subject.next(false);
        this.switchOfTimerRef = null;
      }, 100);
    }
  }

  getSpinnerStatus(): Observable<boolean> {
    return this.subject;
  }
}
