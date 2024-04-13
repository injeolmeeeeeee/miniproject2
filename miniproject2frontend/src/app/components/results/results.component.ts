import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Subscription } from 'rxjs';
import { WebSocketService } from '../../service/websocket.service';
import { Player } from '../../models';
import { AdminService } from '../../service/admin.service';

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

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private webSocketService: WebSocketService,
    private adminSvc: AdminService
  ) {}

  ngOnInit(): void {
    this.isAdmin = this.adminSvc.isAdmin;
    this.route.queryParams.subscribe(params => {
      this.currentRound = params['currentRound'];
    });
    
    this.route.paramMap.subscribe(params => {
      this.gameCode = params.get('gameId') || '';
      this.playerName = params.get('playerName') || '';
      if (this.gameCode) {
        this.webSocketService.connect(this.gameCode, this.playerName);
        this.subscription = this.webSocketService.getReceivedMessages().subscribe(message => {
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

  redirectToQuestions(): void {
    this.webSocketService.handleNextQuestionEvent(this.gameCode, this.playerName);
    this.router.navigate(["session", this.gameCode]);
  }

  goToEnd(): void {
    this.webSocketService.sendEnd(this.gameCode);
  }
}
