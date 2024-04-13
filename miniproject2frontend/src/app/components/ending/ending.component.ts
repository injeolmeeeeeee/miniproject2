import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { WebSocketService } from '../../service/websocket.service';

@Component({
  selector: 'app-ending',
  templateUrl: './ending.component.html',
  styleUrls: ['./ending.component.css']
})
export class EndingComponent implements OnInit {
  gameCode: string = '';
  playerName: string = '';
  subscription: any;
  receivedMessage !: string

  constructor(
    private route: ActivatedRoute,
    private webSocketService: WebSocketService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.gameCode = params.get('gameId') || '';
      if (this.gameCode) {
        this.webSocketService.connect(this.gameCode, this.playerName);
        this.subscription = this.webSocketService.getReceivedMessages().subscribe(message => {
          console.log('Received messages:', message);
          this.receivedMessage = message;
        });
      }
    });
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
