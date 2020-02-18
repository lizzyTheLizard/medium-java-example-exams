import { Component, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { ExamService, Exam, Participant } from '../exam.service';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { map, flatMap } from 'rxjs/operators';

@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  participants: Participant[];
  exam: Exam;

  constructor(private readonly router: Router,
              private readonly route: ActivatedRoute,
              private readonly examService: ExamService,
              private readonly snackBar: MatSnackBar) {}

  ngOnInit() {
    this.route.params.pipe(
        map(params => params.id),
        flatMap(id => this.examService.getExam(id)))
      .subscribe(exam => {
        this.exam = exam;
        this.refresh();
      });
  }

  close(): void {
    this.snackBar.open('You`re exam has been closed', 'ok', {duration: 5000});
    this.router.navigate(['/home/']);
  }

  refresh(): void {
    this.examService.getParticipantsForExam(this.exam.id).subscribe(
      result => this.participants = result);
  }
}
