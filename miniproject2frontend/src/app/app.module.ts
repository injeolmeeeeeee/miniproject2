import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { FormsModule } from '@angular/forms';

import { SocialLoginModule, SocialAuthServiceConfig, GoogleLoginProvider } from '@abacritt/angularx-social-login';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { CreateGameComponent } from './components/create-game/create-game.component';
import { EnterGameComponent } from './components/enter-game/enter-game.component';
import { StartComponent } from './components/start/start.component';
import { GameLobbyComponent } from './components/game-lobby/game-lobby.component';
import { EndingComponent } from './components/ending/ending.component';
import { QuestionsComponent } from './components/questions/questions.component';
import { ResultsComponent } from './components/results/results.component';
import { PlayerService } from './service/player.service';
import { QuestionStore } from './question.store';
import { GoogleSigninComponent } from './components/google-signin/google-signin.component';

@NgModule({
  declarations: [
    AppComponent,
    CreateGameComponent,
    EnterGameComponent,
    StartComponent,
    GameLobbyComponent,
    EndingComponent,
    QuestionsComponent,
    ResultsComponent,
    GoogleSigninComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    SocialLoginModule
  ],
  providers: [
    PlayerService,
    QuestionStore,
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider('1055630252877-4ed0a9m2nfjjk7jj9ats1auf1u103ihi.apps.googleusercontent.com', {
              scopes: 'openid profile email',
            }),
          },
        ],
        onError: (err) => {
          console.error(err);
        },
      } as SocialAuthServiceConfig,
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }