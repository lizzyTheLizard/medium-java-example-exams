import { BrowserModule } from '@angular/platform-browser';
import { NgModule, DoBootstrap, ApplicationRef } from '@angular/core';

import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { LayoutModule } from '@angular/cdk/layout';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { MatRadioModule } from '@angular/material/radio';
import { ReactiveFormsModule } from '@angular/forms';
import { FormsModule } from '@angular/forms';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatPaginatorModule } from '@angular/material/paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSnackBarModule } from '@angular/material/snack-bar';
import { KeycloakAngularModule, KeycloakService } from 'keycloak-angular';
import { AppRoutingModule } from './app-routing.module';
import { AdminPageComponent } from './pages/admin-page/admin-page.component';
import { StartPageComponent } from './pages/start-page/start-page.component';
import { ExamPageComponent } from './pages/exam-page/exam-page.component';
import { ProgressSpinnerComponent } from './components/progress-spinner/progress-spinner.component';
import { AppComponent } from './components/app/app.component';
import { environment } from 'src/environments/environment';
import { HttpClientModule } from '@angular/common/http';

const keycloakService = new KeycloakService();

@NgModule({
  declarations: [
    AppComponent,
    StartPageComponent,
    ExamPageComponent,
    AdminPageComponent,
    ProgressSpinnerComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    BrowserAnimationsModule,
    MatGridListModule,
    MatCardModule,
    MatMenuModule,
    MatIconModule,
    MatButtonModule,
    LayoutModule,
    MatInputModule,
    MatSelectModule,
    MatRadioModule,
    ReactiveFormsModule,
    FormsModule,
    MatToolbarModule,
    MatSidenavModule,
    MatListModule,
    MatTableModule,
    MatPaginatorModule,
    MatSortModule,
    MatProgressSpinnerModule,
    MatSnackBarModule,
    KeycloakAngularModule,
    HttpClientModule
  ],
  providers: [ {
    provide: KeycloakService,
    useValue: keycloakService
  }],
})
export class AppModule implements DoBootstrap {
  ngDoBootstrap(appRef: ApplicationRef): void {
    keycloakService
      .init({
        config: {
          url: environment.keycloakUrl,
          realm: 'exam',
          clientId: 'ui'
        },
        initOptions: {
          onLoad: 'login-required',
          checkLoginIframe: false
        },
        enableBearerInterceptor: true,
        bearerExcludedUrls: ['/assets', '/clients/public']
      })
      .then(() => {
        appRef.bootstrap(AppComponent);
        console.log('[ngDoBootstrap] bootstrap app');
      })
      .catch(error => console.error('[ngDoBootstrap] init Keycloak failed', error));
  }
}
