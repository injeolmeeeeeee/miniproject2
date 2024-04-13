import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Game } from '../models';

@Injectable({
  providedIn: 'root'
})
export class GameCodeService {

  apiUrl = 'http://localhost:8080/api/';

  constructor(private http: HttpClient) { }

  createGame(edition: string): Observable<Game> {
    return this.http.post<Game>(`${this.apiUrl}create?edition=${edition}`, null);
  }

  getQuote(): Observable<string> {
    return this.http.get(`${this.apiUrl}quote`, { responseType: 'text' });
  }
}