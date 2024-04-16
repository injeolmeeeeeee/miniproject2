import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { WebSocketService } from '../../service/websocket.service';
import { Subscription } from 'rxjs';
import * as Phaser from 'phaser';
import { AdminService } from '../../service/admin.service';

@Component({
  selector: 'app-game-lobby',
  templateUrl: './game-lobby.component.html',
  styleUrls: ['./game-lobby.component.css']
})
export class GameLobbyComponent implements OnInit, OnDestroy {
  
  gameCode !: string;
  players: any[] = [];
  private subscription: Subscription | undefined;
  playerName !: string;
  game !: Phaser.Game;
  isAdmin: boolean = false;

  private router = inject(Router);

  constructor(
    private route: ActivatedRoute,
    private webSocketService: WebSocketService,
    private adminSvc: AdminService
  ) { }

  ngOnInit(): void {
    this.isAdmin = this.adminSvc.isAdmin;
    this.route.paramMap.subscribe(params => {
      this.gameCode = params.get('gameId') || '';
      this.playerName = params.get('playerName') || '';
      if (this.gameCode) {
        this.webSocketService.connect(this.gameCode, this.playerName);
        this.subscription = this.webSocketService.getReceivedMessages().subscribe(message => {
          console.log('Received messages:', message);
          
          this.players = [];
          const playerList = message.content;
        
          playerList.forEach((player: any) => {
            this.players.push({
              name: player.name,
              score: player.score,
              responses: player.responses
            });
          })
          console.log('Players:', this.players);
        });
        
      } else {
        console.error("Game id is empty.");
      }
    });

    this.game = new Phaser.Game({
      scene: {
        preload: this.preload.bind(this),
        create: this.create.bind(this)
      }
    });
  }

  ngOnDestroy(): void {
    if (this.subscription) {
      this.subscription.unsubscribe();
    }
  }

  preload() {
    this.game.scene.scenes[0].load.audio('startaudio', 'assets/startaudio.wav');
  }  

  create() {}

  start(): void {
    const buttonSound = this.game.sound.add('startaudio');
    buttonSound.play();
    console.log("Start button clicked");
    console.log("Routing to the next page...");
    console.log("Game code:", this.gameCode);
    this.webSocketService.sendStartEvent(this.gameCode);
  }

  quit() {
    this.router.navigate(['/end']);
  }
}