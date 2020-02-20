import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { map } from 'rxjs/operators';
import { Breakpoints, BreakpointObserver } from '@angular/cdk/layout';
import { Observable } from 'rxjs';

import { ExamService, Exam } from '../../services/exam/exam.service';

@Component({
  selector: 'app-start-page',
  templateUrl: './start-page.component.html',
  styleUrls: ['./start-page.component.scss']
})
export class StartPageComponent {
  key = '';
  colspan$ = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => matches ? 2 : 1));
  inputClass$ = this.breakpointObserver.observe(Breakpoints.Handset).pipe(
    map(({ matches }) => matches ? 'fullWidth' : 'fixedWidth'));

  exams$: Observable<Exam[]>;
  displayedColumns = ['id', 'name'];

  navigateToKey() {
    this.router.navigate(['/exam/', this.key]);
  }

  fileUpload(files: File[]) {
    this.examService.createExams(files);
  }

  check(id: string) {
    this.router.navigate(['/admin/', id]);
  }

  constructor(private readonly router: Router,
              private readonly examService: ExamService,
              private breakpointObserver: BreakpointObserver) {
    this.exams$ = this.examService.getExams();
  }
}
