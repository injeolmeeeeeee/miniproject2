import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../service/websocket.service';
import { Player, Question } from '../../models';
import { AdminService } from '../../service/admin.service';
import { QuestionStore } from '../../question.store';

@Component({
  selector: 'app-results',
  templateUrl: './results.component.html',
  styleUrls: ['./results.component.css']
})
export class ResultsComponent implements OnInit, OnDestroy {
  gameCode: string = '';
  players: Player[] = [];
  private subscription: Subscription | undefined;
  playerName: string = '';
  currentRound!: number;
  isAdmin: boolean = false;
  currentQuestion!: Question;
  isWebSocketConnected: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private webSocketService: WebSocketService,
    private adminSvc: AdminService,
    private questionStore: QuestionStore
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.adminSvc.isAdmin;
    this.route.queryParams.subscribe(params => {
      this.currentRound = params['currentRound'];
      const questionString = params['question'];
      this.currentQuestion = JSON.parse(questionString);
    });

    this.route.paramMap.subscribe(params => {
      this.gameCode = params.get('gameId') || '';
      this.playerName = params.get('playerName') || '';
      if (this.gameCode) {
        this.webSocketService.connect(this.gameCode, this.playerName);

        this.webSocketService.onConnected().subscribe(connected => {
          console.log('WebSocket connection status:', connected);
          this.isWebSocketConnected = connected;
        });

        this.webSocketService.getReceivedMessages().subscribe(message => {
          console.log('Received messages:', message);
          this.players = [];
          const playerList = message.content;
          playerList.forEach((player: Player) => {
            const responses = player.responses;
            this.players.push({
              name: player.name,
              score: player.score,
              responses: responses,
              gameId: player.gameId
            });
          });
          console.log('Players:', this.players);
        });
      } else {
        console.error("Game id is empty.");
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  goToEnd(): void {
    this.webSocketService.sendEnd(this.gameCode);
  }

  redirectToQuestions(): void {
    this.deleteDisplayedQuestion()
    this.webSocketService.sendNextQuestionEvent(this.gameCode);
  }

  deleteDisplayedQuestion(): void {
    if (this.currentQuestion) {
      this.questionStore.deleteQuestion(this.currentQuestion.id)
        .then(() => {
          console.log('Deleted displayed question from Dexie.');
        })
        .catch(error => {
          console.error('Error deleting displayed question from Dexie:', error);
        });
    }
  }

  quit() {
    this.router.navigate(['/end']);
  }

}