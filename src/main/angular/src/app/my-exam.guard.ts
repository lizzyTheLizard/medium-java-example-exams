import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, UrlTree, Router } from '@angular/router';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';
import { ExamService } from './exam.service';

@Injectable({
  providedIn: 'root'
})
export class MyExamGuard implements CanActivate {

  constructor(private router: Router, private examService: ExamService)  { }

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {
    const id = next.params.id;
    if (!id) {
        this.router.navigate(['/home']);
        return false;
    }
    return this.examService.isMine(id).pipe(tap(s => {if (!s) {this.router.navigate(['/home']); }}));
  }

}
