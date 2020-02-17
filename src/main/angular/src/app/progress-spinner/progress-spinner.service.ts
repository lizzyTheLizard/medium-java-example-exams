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
      console.log('Cancle switch off timer');
      clearTimeout(this.switchOfTimerRef);
      this.switchOfTimerRef = null;
    } else if (!this.subject.value) {
      console.log('Enable spinner');
      this.subject.next(true);
    } else {
      console.log('Set Spinner level', this.levels);
    }
  }

  disable(): void {
    if (this.levels === 0) {
      console.error('Reached level below zero, set it to zero');
      return;
    }

    this.levels--;
    if (this.levels === 0 && this.subject.value) {
      console.log('Disable spinner in 100 ms');
      this.switchOfTimerRef = setTimeout(() => {
        this.subject.next(false);
        this.switchOfTimerRef = null;
        console.log('Disable spinner');
      }, 100);
    } else {
      console.log('Set Spinner level', this.levels);
    }
  }

  getSpinnerStatus(): Observable<boolean> {
    return this.subject;
  }
}
