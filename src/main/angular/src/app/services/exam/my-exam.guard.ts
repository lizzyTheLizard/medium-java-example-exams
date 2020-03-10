import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ExamService } from './jdbcExam.service';
import { MatSnackBar } from '@angular/material/snack-bar';

@Injectable({
  providedIn: 'root'
})
export class MyExamGuard implements CanActivate {

  constructor(private router: Router, private examService: ExamService, private snackBar: MatSnackBar)  { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const id = next.params.id;
    if (!id) {
        this.router.navigate(['/home']);
        return false;
    }
    return this.examService.isMine(id).pipe(tap(s => {
      if (!s) {
        this.router.navigate(['/home']);
        this.snackBar.open('You cannot access this page', 'ok', {duration: 5000});

      }
    }));
  }

}
