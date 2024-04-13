import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game, Player } from '../models';

@Injectable({
  providedIn: 'root'
})
export class GameCodeService {

  apiUrl = 'http://localhost:8080/api/';

  constructor(private http: HttpClient) { }

  createGame(edition: string): Observable<Game> {
    return this.http.post<Game>(`${this.apiUrl}create?edition=${edition}`, null); // Pass 'edition' in the request URL
  }
}
