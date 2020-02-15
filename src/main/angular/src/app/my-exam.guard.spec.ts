import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { MyExamGuard } from './my-exam.guard';

describe('MyExamGuard', () => {
  let guard: MyExamGuard;

  beforeEach(() => {
    TestBed.configureTestingModule({imports: [RouterTestingModule]});
    guard = TestBed.inject(MyExamGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });
});
