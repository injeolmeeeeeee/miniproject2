import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Player } from '../models';
import { BehaviorSubject, Observable, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class PlayerService {
  private apiUrl = 'http://localhost:8080/api/';

  private playerNameSubject = new BehaviorSubject<string>('');

  constructor(private http: HttpClient) { }

  addPlayerToGameLobby(gameId: string, player: Player): Observable<any> {
    console.log(player);
    return this.http.post<any>(`${this.apiUrl}game-lobby/${gameId}/players`, player);
  }

  getPlayersInGameLobby(gameId: string): Observable<Player[]> {
    console.log('Request Payload:', { gameId });
    return this.http.get<Player[]>(`${this.apiUrl}game-lobby/${gameId}`, { responseType: 'json' })
      .pipe(
        catchError(error => {
          console.error('Error fetching players:', error);
          throw error;
        })
      );
  }

  saveResponsesAndScores(gameId: string, player: Player): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}responses-scores?gameId=${gameId}`, player);
  }

  checkPlayerNameExists(playerName: string, gameId: string): Observable<boolean> {
    console.log('Checking player name existence. Player Name:', playerName, 'Game ID:', gameId);
    return this.http.get<boolean>(`${this.apiUrl}checkPlayerNameExists?name=${playerName}&gameId=${gameId}`);
  }
}