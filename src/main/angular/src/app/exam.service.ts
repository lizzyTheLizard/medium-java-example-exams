import { Injectable } from '@angular/core';
import { Observable, of } from 'rxjs';
import { delay, tap } from 'rxjs/operators';
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
    this.progressSpinnerService.setSpinnerStatus(true);
    return of()
    .pipe(delay(1000), tap(() => this.progressSpinnerService.setSpinnerStatus(false)));
  }


  getExam(id: string): Observable<Exam> {
    // TODO
    this.progressSpinnerService.setSpinnerStatus(true);
    return of(
        {name: 'Test Exam', id: '23-456', "questions": [{text: "Question1", answers: ["anser1", "answer2"]}]}
    )
    .pipe(delay(1000), tap(() => this.progressSpinnerService.setSpinnerStatus(false)));
  }

  getExams(): Observable<Exam[]> {
    // TODO
    this.progressSpinnerService.setSpinnerStatus(true);
    return of([
        {name: 'Test Exam', id: '23-456', "questions": []},
        {name: 'Test Exam 2', id: '23-456', "questions": []} ,
        {name: 'Test Exam 2', id: '23-456', "questions": []}
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
