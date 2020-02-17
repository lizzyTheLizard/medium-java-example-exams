import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { Exam, ExamService, Question } from '../exam.service';
import { Router, ActivatedRoute } from '@angular/router';
import { map, flatMap, tap } from 'rxjs/operators';
import { ProgressSpinnerService } from '../progress-spinner/progress-spinner.service';

@Component({
  selector: 'app-exam-page',
  templateUrl: './exam-page.component.html',
  styleUrls: ['./exam-page.component.scss']
})
export class ExamPageComponent implements OnInit {
  exam: Exam;
  answers: Map<number, number>;

  constructor(private readonly router: Router,
              private readonly route: ActivatedRoute,
              private readonly examService: ExamService,
              private readonly progressSpinnerService: ProgressSpinnerService) { }

  ngOnInit() {
    this.route.params.pipe(
        map(params => params.id),
        flatMap(id => this.examService.getExam(id)))
      .subscribe(exam => {
        this.exam = exam;
        this.answers = new Map();
      });
  }

  submit(): void {
    const answerArray = [];
    this.answers.forEach((v, k) => answerArray[v] = k);
    this.examService.trySolve(this.exam.id, answerArray).subscribe(result => this.checkResult(result));
  }

  private checkResult(result: boolean): void {
    if (result) {
      // TODO Show message
      this.router.navigate(['/home/']);
    } else {
      // TODO Show message
    }
  }

  answerMissing(): boolean {
    return !this.answers || this.answers.size < this.exam.questions.length;
  }

  setAnswer(questionIndex: number, answerIndex: number): void {
    this.answers.set(questionIndex, answerIndex);
  }
}
