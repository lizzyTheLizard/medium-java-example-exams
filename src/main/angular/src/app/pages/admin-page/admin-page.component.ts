import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { map, flatMap } from 'rxjs/operators';
import { ExamService, Exam, Participant } from '../../services/exam/exam.service';
import { environment } from 'src/environments/environment';


@Component({
  selector: 'app-admin-page',
  templateUrl: './admin-page.component.html',
  styleUrls: ['./admin-page.component.scss']
})
export class AdminPageComponent implements OnInit {
  participations: Participant[];
  exam: Exam = null;
  link = '';

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
        this.link = environment.uiUrl + this.router.createUrlTree(['/exam/', exam.id]).toString();
        this.refresh();
      });
  }

  close(): void {
    this.examService.close(this.exam.id).subscribe(() => {
      this.snackBar.open('You`re exam has been closed', 'ok', {duration: 5000});
      this.router.navigate(['/home/']);
    });
  }

  refresh(): void {
    this.examService.getParticipantsForExam(this.exam.id).subscribe(
      result => this.participations = result);
  }
}
