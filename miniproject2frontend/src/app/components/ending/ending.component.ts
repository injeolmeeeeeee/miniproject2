import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WebSocketService } from '../../service/websocket.service';
import { GameCodeService } from '../../service/game-code.service';
import { Player } from '../../models';

@Component({
  selector: 'app-ending',
  templateUrl: './ending.component.html',
  styleUrls: ['./ending.component.css']
})
export class EndingComponent implements OnInit {
  gameCode: string = '';
  playerName: string = '';
  subscription: any;
  receivedMessage: string = '';
  quote: string = '';
  players: Player[] = [];

  constructor(
    private route: ActivatedRoute,
    private webSocketService: WebSocketService,
    private gameSvc : GameCodeService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.gameCode = params.get('gameId') || '';
      if (this.gameCode) {
        this.webSocketService.connect(this.gameCode, this.playerName);
        this.subscription = this.webSocketService.getReceivedMessages().subscribe(message => {
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
        });
      }
    });

    this.getQuote(); 
  }

  getQuote(): void {
    this.gameSvc.getQuote().subscribe(
      (quote: string) => {
        console.log('Received quote:', quote);
        this.quote = quote; 
      },
      (error) => {
        console.error('Error fetching quote:', error);
      }
    );
  }

  signOut(): void {
    // gapi.load('auth2', () => {
    //   const auth2 = gapi.auth2.getAuthInstance();
    //   auth2.signOut().then(() => {
    //     console.log('User signed out.');
    //   });
    // });
  }
}