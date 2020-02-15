import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class ProgressSpinnerService {

  constructor() { }

  getSpinnerStatus(): Observable<boolean> {
    // TODO
    return of(true).pipe(delay(5000));
  }
}
