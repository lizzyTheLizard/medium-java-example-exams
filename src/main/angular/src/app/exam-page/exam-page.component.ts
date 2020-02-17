import { Component } from '@angular/core';
import { Observable } from 'rxjs';
import { Exam, ExamService } from '../exam.service';
import { Router, ActivatedRoute } from '@angular/router';
import { map, flatMap } from 'rxjs/operators';

@Component({
  selector: 'app-exam-page',
  templateUrl: './exam-page.component.html',
  styleUrls: ['./exam-page.component.scss']
})
export class ExamPageComponent {
  exam$: Observable<Exam>

  constructor(private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly examService: ExamService) {
}

  ngOnInit() {
    this.exam$ = this.route.params.pipe(
      map(params => params['id']),
      flatMap(id => this.examService.getExam(id))
    );
}
}
