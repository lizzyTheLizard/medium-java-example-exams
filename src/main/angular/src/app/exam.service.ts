import { Injectable } from '@angular/core';
import { Observable, of, throwError } from 'rxjs';
import { delay, tap, catchError, map, share } from 'rxjs/operators';
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

export interface Participant {
  name: string;
  id: string;
}


@Injectable({
  providedIn: 'root'
})
export class ExamService {

  getParticipantsForExam(id: string): Observable<Participant[]> {
    // TODO
    this.progressSpinnerService.enable('getParticipantsForExam');
    return of<Participant[]>([
      {name: 'Hans', id: 'dfd'},
      {name: 'Peter', id: 'fdf'}
    ])
    .pipe(delay(1000), share(), tap(() => this.progressSpinnerService.disable('getParticipantsForExam')));
  }

  constructor(private readonly progressSpinnerService: ProgressSpinnerService) { }

  createExams(files: File[]): Observable<any> {
    // TODO
    this.progressSpinnerService.enable('createExams');
    return of()
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable('createExams')));
  }

  getExam(id: string): Observable<Exam> {
    // TODO
    this.progressSpinnerService.enable('getExam');
    return of(
        {name: 'Test Exam', id: '23-456', questions: [{text: 'Question1', answers: ['anser1', 'answer2']}]}
    )
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable('getExam')));
  }

  trySolve(id: string, answers: number[]): Observable<boolean> {
    // TODO
    this.progressSpinnerService.enable('trySolve');
    return of(answers[0] === 0)
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable('trySolve')));
  }

  getExams(): Observable<Exam[]> {
    // TODO
    this.progressSpinnerService.enable('getExams');
    return of([
        {name: 'Test Exam', id: 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa', questions: []},
        {name: 'Test Exam 2', id: '23-456', questions: []} ,
        {name: 'Test Exam 2', id: '23-456', questions: []}
    ])
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable('getExams')));
  }

  isMine(id: string): Observable<boolean> {
    // TODO
    this.progressSpinnerService.enable('isMine');
    return of(id === 'aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa')
    .pipe(delay(1000), tap(() => this.progressSpinnerService.disable('isMine')));
  }
}
