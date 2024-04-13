import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, Subject, Subscription } from 'rxjs';
import { QuestionStore } from '../question.store';

@Injectable({
  providedIn: 'root'
})
export class WebSocketService {
  private ws: WebSocket | undefined;
  private gameCode!: string;
  private playerName!: string;

  private receivedMessagesSubject: Subject<any> = new Subject<any>();
  private connectedSubject = new Subject<boolean>();
  private onConnectedSubject: Subject<boolean> = new Subject<boolean>();
  private gameStartSubject: Subject<void> = new Subject<void>();
  private subscription: Subscription | undefined;

  private router = inject(Router);
  private questionStore = inject(QuestionStore);

  constructor() {}

  connect(gameCode: string, playerName: string): void {
    this.gameCode = gameCode;
    this.playerName = playerName;
    if ("WebSocket" in window) {
      console.log("Connecting...");
      let url = "ws://localhost:8080/websocket-server";
      console.log("Connect to " + url);
      this.ws = new WebSocket(`${url}?gameCode=${gameCode}`);
      this.ws.onopen = () => this.onWebSocketOpen();
      this.ws.onmessage = (event) => this.onReceive(event);
      this.ws.onerror = (error) => {
        console.error("WebSocket error:", error);
      };
    } else {
      console.error("WebSocket NOT supported by your Browser!");
    }
  }

  private onWebSocketOpen(): void {
    console.log("WebSocket connected");
    this.onConnectedSubject.next(true);
  }

  private onReceive(event: MessageEvent<any>): void {
    try {
      let receivedMsg = JSON.parse(event.data);
      console.log('Received message type:', receivedMsg.type);
      console.log('Received message:', receivedMsg);

        if (receivedMsg.type === 'questions') {
          this.handleQuestions(receivedMsg.content);
          console.log(receivedMsg.content)
        } else if (receivedMsg.type === 'player_list') {
          this.receivedMessagesSubject.next(receivedMsg);
        } else if (receivedMsg.type === 'game_start') {
          this.handleGameStart(this.gameCode, this.playerName);
          console.log(receivedMsg.content)
        } else if (receivedMsg.type === 'next_question') {
          this.handleNextQuestionEvent(this.gameCode, this.playerName);
        } else if (receivedMsg.type === 'end') {
          this.receivedMessagesSubject.next(receivedMsg);
          this.handleEnd(this.gameCode)
        } else {
          console.error('Received unrecognized object message:', receivedMsg);
        }
    } catch (error) {
      console.error("Error parsing received message:", error);
    }
  }

  private handleQuestions(question: any): void {
    this.questionStore.handleQuestions(question);
  }

  private handleGameStart(gameCode: string, playerName: string): void {
    console.log("Handling game start...");
    
    this.subscription = this.questionStore.questionsSaved().subscribe(() => {
      console.log("Navigating to game page...");
      this.router.navigate(['/game', gameCode, playerName]);
      console.log("Navigation complete.");
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  public handleNextQuestionEvent(gameCode: string, playerName: string): void {
    this.router.navigate(['/game', gameCode, playerName]);
  }


  public handleEnd(gameId: string): void {
    this.router.navigate(['/end']);
  }

  public getGameStartEvent(): Observable<void> {
    return this.gameStartSubject.asObservable();
  }

  public onConnected(): Observable<boolean> {
    return this.connectedSubject.asObservable();
  }

  public send(gameId: string, content: string): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      const message = {
        gameId: gameId,
        content: content
      };
      console.log("Sent message: ", message);
      this.ws.send(JSON.stringify(message));
    } else {
      console.error("WebSocket connection is not open or is still connecting.");
    }
  }

  public sendNewPlayer(gameId: string): void {
    const newPlayer = 'new_player';
    this.send(gameId, newPlayer);
  }

  public sendStartEvent(gameId: string): void {
    const startEvent = "start_game";
    this.send(gameId, startEvent);
  }

  public sendNextQuestionEvent(gameId: string, playerName: string): void {
    const newRound = 'new_round';
    this.send(gameId, newRound);
  }
  

  public sendResponseEvent(gameId: string): void {
    const responseEvent = "response_submit";
    this.send(gameId, responseEvent);
  }

  public sendEnd(gameId: string): void {
    const responseEvent = "end";
    this.send(gameId, responseEvent);
  }

  public close(): void {
    if (this.ws) {
      this.ws.close();
      console.log("WebSocket connection closed");
    } else {
      console.error("WebSocket connection is not open");
    }
  }

  public getReceivedMessages(): Observable<any> {
    return this.receivedMessagesSubject.asObservable();
  }
}