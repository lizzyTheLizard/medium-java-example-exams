import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
import { ProgressSpinnerService } from './progress-spinner/progress-spinner.service';

export interface Exam {
  name: string;
  id: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  constructor(private readonly progressSpinnerService: ProgressSpinnerService) { }

  createExams(files: File[]): Observable<any> {
    // TODO
    this.progressSpinnerService.setSpinnerStatus(true);
    return of()
    .pipe(delay(1000), tap(() => this.progressSpinnerService.setSpinnerStatus(false)));
  }

  getExams(): Observable<Exam[]> {
    // TODO
    this.progressSpinnerService.setSpinnerStatus(true);
    return of([
        {name: 'Test Exam', id: '23-456'},
        {name: 'Test Exam 2', id: '23-456'} ,
        {name: 'Test Exam 2', id: '23-456'}
    ])
    .pipe(delay(1000), tap(() => this.progressSpinnerService.setSpinnerStatus(false)));
  }

  isMine(id: string): Observable<boolean> {
    // TODO
    this.progressSpinnerService.setSpinnerStatus(true);
    return of(false)
    .pipe(delay(1000), tap(() => this.progressSpinnerService.setSpinnerStatus(false)));
  }
}
