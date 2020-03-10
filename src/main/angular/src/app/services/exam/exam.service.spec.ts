import { TestBed } from '@angular/core/testing';

import { ExamService } from './jdbcExam.service';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { HttpClientModule } from '@angular/common/http';
import { KeycloakAngularModule } from 'keycloak-angular';

describe('ExamService', () => {
  let service: ExamService;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [MatSnackBarModule, KeycloakAngularModule, HttpClientModule]}).compileComponents();
    service = TestBed.inject(ExamService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
