import { Injectable } from '@angular/core';
import { Observable, throwError, OperatorFunction, pipe } from 'rxjs';
import { tap, map, catchError } from 'rxjs/operators';
import { ProgressSpinnerService } from '../../components/progress-spinner/progress-spinner.service';
import { HttpClient } from '@angular/common/http';
import { MatSnackBar } from '@angular/material/snack-bar';
import { environment } from 'src/environments/environment';
import { KeycloakService } from 'keycloak-angular';

export interface Exam {
  id: string;
  questions: Question[];
  title: string;
  text: string;
  owner: string;
}

export interface Question {
  text: string;
  options: string[];
}

export interface Participant {
  userId: string;
  firstName: string;
  lastName: string;
  successful: string;
  comment: string;
  remainingAttempts: number;
}

export interface Solution {
  answers: number[];
  comment: string;
}

@Injectable({
  providedIn: 'root'
})
export class ExamService {

  constructor(private readonly progressSpinnerService: ProgressSpinnerService,
              private readonly snackBar: MatSnackBar,
              private readonly httpClient: HttpClient,
              private readonly keycloakService: KeycloakService) { }

  getParticipantsForExam(id: string): Observable<Participant[]> {
    this.progressSpinnerService.enable('get Participants');
    return this.httpClient.get<Participant[]>(environment.apiUrl + 'exams/' + id + '/participants')
      .pipe(this.errorHandler('get Participants'));
  }

  isMine(id: string): Observable<boolean> {
    return this.getExam(id)
      .pipe(map(e => e.owner === this.keycloakService.getKeycloakInstance().subject));
  }

  getExam(id: string): Observable<Exam> {
    this.progressSpinnerService.enable('get Exam');
    return this.httpClient.get<Exam>(environment.apiUrl + 'exams/' + id)
      .pipe(this.errorHandler('get Exam'));
  }

  close(id: string): Observable<any> {
    return this.httpClient.delete<any>(environment.apiUrl + 'exams/' + id)
      .pipe(this.errorHandler('delete Exam'));
  }

  getExams(): Observable<Exam[]> {
    this.progressSpinnerService.enable('get Exams');
    return this.httpClient.get<Exam>(environment.apiUrl + 'exams/')
      .pipe(this.errorHandler('get Exams'));
  }

  getTriesLeft(id: string): Observable<number> {
    this.progressSpinnerService.enable('get Tried Left');
    return this.httpClient.get<number>(environment.apiUrl + 'exams/' + id + '/triesLeft')
      .pipe(this.errorHandler('get Tried Left'));
  }

  createExams(files: File[]): Observable<Exam> {
    const formData = new FormData();
    formData.append('file', files[0]);
    this.progressSpinnerService.enable('create Exam');
    return this.httpClient.post<Exam>(environment.apiUrl + 'exams/', formData)
      .pipe(this.errorHandler('create Exam'));
  }

  trySolve(id: string, solution: Solution): Observable<boolean> {
    return this.httpClient.post<Exam>(environment.apiUrl + 'exams/' + id + '/solution', solution)
      .pipe(this.errorHandler('check solution'));
  }

  private errorHandler<T, R>(logInfo: string): OperatorFunction<T, R> {
    return pipe(
      tap(() =>  {
        console.log('Successfully ' + logInfo);
        this.progressSpinnerService.disable(logInfo);
      }),
      catchError<R, Observable<R>>(e => {
        console.warn('Could not ' + logInfo, e);
        this.progressSpinnerService.disable(logInfo);
        this.snackBar.open('Could not ' + logInfo, 'OK');
        return throwError(e);
      }),
    );
  }
}
