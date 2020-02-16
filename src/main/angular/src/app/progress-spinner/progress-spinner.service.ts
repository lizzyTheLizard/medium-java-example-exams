import { Injectable } from '@angular/core';
import { Observable, of, BehaviorSubject } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProgressSpinnerService {
  private subject: BehaviorSubject<boolean> = new BehaviorSubject(false);

  constructor() { }

  setSpinnerStatus(status: boolean): void {
    this.subject.next(status);
  }

  getSpinnerStatus(): Observable<boolean> {
    return this.subject;
  }
}
