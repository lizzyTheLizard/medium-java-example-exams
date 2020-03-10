import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { StartPageComponent } from './pages/start-page/start-page.component';
import { ExamPageComponent } from './pages/jdbcExam-page/jdbcExam-page.component';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { MyExamGuard } from './services/jdbcExam/my-jdbcExam.guard';

const routes: Routes = [
  { path: 'admin/:id', component: AdminPageComponent, canActivate: [MyExamGuard] },
  { path: 'jdbcExam/:id', component: ExamPageComponent },
  { path: '**', component: StartPageComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
