import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { map, flatMap } from 'rxjs/operators';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Exam, ExamService } from '../../services/exam/exam.service';

@Component({
  selector: 'app-exam-page',
  templateUrl: './exam-page.component.html',
  styleUrls: ['./exam-page.component.scss']
})
export class ExamPageComponent implements OnInit {
  exam: Exam;
  triesLeft: number;
  answers: Map<number, number>;

  constructor(private readonly router: Router,
              private readonly route: ActivatedRoute,
              private readonly examService: ExamService,
              private readonly snackBar: MatSnackBar) { }

  ngOnInit() {
    this.route.params.pipe(
        map(params => params.id),
        flatMap(id => this.examService.getExam(id)))
      .subscribe(exam => {
        this.exam = exam;
        this.answers = new Map();
        this.examService.getTriesLeft(this.exam.id).subscribe(
          triesLeft => this.triesLeft = triesLeft
        );
      }, e => this.router.navigate(['/home']));
  }

  submit(comment: string): void {
    const answerArray = [];
    this.answers.forEach((v, k) => answerArray[k] = v);
    const solution = {answers: answerArray, comment};
    this.examService.trySolve(this.exam.id, solution).subscribe(result => this.checkResult(result));
  }

  private checkResult(result: boolean): void {
    if (result) {
      this.snackBar.open('You sucessfully took the exam', 'ok', {duration: 5000});
      this.router.navigate(['/home/']);
    } else {
      this.examService.getTriesLeft(this.exam.id).subscribe(triesLeft => {
        this.triesLeft = triesLeft;
        if (this.triesLeft <= 0) {
          this.snackBar.open('No more tries left, you have failed the exam', 'ok', {duration: 5000});
          this.router.navigate(['/home/']);
        } else {
          this.snackBar.open('At least one answer is not correct', 'ok', {duration: 5000});
        }
      });
    }
  }

  buttonDisabled(): boolean {
    return !this.answers || this.answers.size < this.exam.questions.length || this.triesLeft <= 0;
  }

  setAnswer(questionIndex: number, answerIndex: number): void {
    this.answers.set(questionIndex, answerIndex);
  }
}
