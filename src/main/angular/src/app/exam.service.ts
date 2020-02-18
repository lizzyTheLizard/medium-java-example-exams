import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay, tap, catchError, map } from 'rxjs/operators';
import { ProgressSpinnerService } from './progress-spinner/progress-spinner.service';

export interface Question {
  text: string;
  answers: string[];
}

export interface Exam {
  name: string;
  id: string;
  questions: Question[];
}


@Injectable({
  providedIn: 'root'
})
export class ExamService {

  constructor(private readonly progressSpinnerService: ProgressSpinnerService) { }

  createExams(files: File[]): Observable<any> {
    // TODO
    this.progressSpinnerService.enable();
    return of()
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable()));
  }

  getExam(id: string): Observable<Exam> {
    // TODO
    this.progressSpinnerService.enable();
    return of(
        {name: 'Test Exam', id: '23-456', questions: [{text: 'Question1', answers: ['anser1', 'answer2']}]}
    )
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable()));
  }

  trySolve(id: string, answers: number[]): Observable<boolean> {
    // TODO
    this.progressSpinnerService.enable();
    return of(answers[0] === 0)
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable()));
  }

  getExams(): Observable<Exam[]> {
    // TODO
    this.progressSpinnerService.enable();
    return of([
        {name: 'Test Exam', id: '23-456', questions: []},
        {name: 'Test Exam 2', id: '23-456', questions: []} ,
        {name: 'Test Exam 2', id: '23-456', questions: []}
    ])
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable()));
  }

  isMine(id: string): Observable<boolean> {
    // TODO
    this.progressSpinnerService.enable();
    return of(false)
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable()));
  }
}
