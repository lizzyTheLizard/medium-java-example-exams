import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay } from 'rxjs/operators';

export interface Exam {
  name: string;
  id: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  constructor() { }

  createExams(files: File[]): Observable<any> {
    // TODO
    return of().pipe(delay(1000));
  }

  getExams(): Observable<Exam[]> {
    // TODO
    return of([
        {name: 'Test Exam', id: '23-456'},
        {name: 'Test Exam 2', id: '23-456'} ,
        {name: 'Test Exam 2', id: '23-456'}
    ]).pipe(delay(1000));
  }

  isMine(id: string): Observable<boolean> {
    // TODO
    return of(false).pipe(delay(1000));
  }
}
