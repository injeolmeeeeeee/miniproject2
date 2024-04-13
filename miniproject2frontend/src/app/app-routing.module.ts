import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { QuestionsComponent } from './components/questions/questions.component';
import { StartComponent } from './components/start/start.component';
import { CreateGameComponent } from './components/create-game/create-game.component';
import { EnterGameComponent } from './components/enter-game/enter-game.component';
import { GameLobbyComponent } from './components/game-lobby/game-lobby.component';
import { EndingComponent } from './components/ending/ending.component';
import { ResultsComponent } from './components/results/results.component';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule } from '@angular/forms';


const routes: Routes = [
  { path: '', redirectTo: '/start', pathMatch: 'full' },
  { path: 'start', component: StartComponent },
  { path: 'create', component: CreateGameComponent },
  { path: 'enter', component: EnterGameComponent }, 
  { path: 'enter/:gameId/:selectedEdition', component: EnterGameComponent },
  { path: 'game-lobby/:gameId/:playerName', component: GameLobbyComponent }, //lazy load
  { path: 'game/:gameId/:playerName', component: QuestionsComponent }, //lazy load
  { path: 'result/:gameId/:playerName', component: ResultsComponent}, //lazy load
  { path:'end', component: EndingComponent} //lazy load
];

@NgModule({
  imports: [BrowserModule, HttpClientModule, ReactiveFormsModule,
    RouterModule.forRoot(routes, { useHash: true })],
  exports: [RouterModule]
})

export class AppRoutingModule { }