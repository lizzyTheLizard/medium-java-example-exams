import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MyExamGuard } from './my-exam.guard';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { KeycloakAngularModule } from 'keycloak-angular';

describe('MyExamGuard', () => {
  let guard: MyExamGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [RouterTestingModule, HttpClientModule, KeycloakAngularModule, MatSnackBarModule]});
    guard = TestBed.inject(MyExamGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
