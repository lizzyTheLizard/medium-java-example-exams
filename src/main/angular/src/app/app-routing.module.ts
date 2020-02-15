import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StartPageComponent } from './start-page/start-page.component';
import { ExamPageComponent } from './exam-page/exam-page.component';
import { AdminPageComponent } from './admin-page/admin-page.component';
import { MyExamGuard } from './my-exam.guard';

const routes: Routes = [
  { path: 'admin/:id', component: AdminPageComponent, canActivate: [MyExamGuard] },
  { path: 'exam/:id', component: ExamPageComponent },
  { path: '**', component: StartPageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
