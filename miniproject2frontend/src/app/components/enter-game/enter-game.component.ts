import { Component, EventEmitter, Output } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';
import { PlayerService } from '../../service/player.service';
import { Player } from '../../models';

@Component({
  selector: 'app-enter-game',
  templateUrl: './enter-game.component.html',
  styleUrls: ['./enter-game.component.css']
})
export class EnterGameComponent {
  gameId: string = '';
  playerName: string = '';
  selectedEdition: string = '';
  errorMessage: string = '';

  @Output() gameDetailsSubmitted = new EventEmitter<{ gameId: string, playerName: string }>();

  constructor(
    private router: Router,
    private route: ActivatedRoute,
    private playerService: PlayerService
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.gameId = params.get('gameId') || '';
      this.playerName = params.get('playerName') || '';
      this.selectedEdition = params.get('selectedEdition') || '';
    });
  }

  joinGame(): void {
    if (!this.gameId || !this.playerName) {
      this.errorMessage = 'Game code or player name is missing.';
      return;
    }

    this.playerService.checkPlayerNameExists(this.playerName, this.gameId).subscribe(
      (exists) => {
        if (exists) {
          this.errorMessage = 'Player name already exists. Please choose a different name.';
        } else {
          const newPlayer: Player = {
            name: this.playerName,
            gameId: this.gameId
          };
          this.playerService.addPlayerToGameLobby(this.gameId, newPlayer).subscribe(
            () => {
              console.log(this.playerName, 'joined game successfully. Game Code:', this.gameId);
              this.router.navigate(['/game-lobby', this.gameId, this.playerName])
            },
            (error) => {
              if (error.status === 404) {
                this.errorMessage = 'Game does not exist';
              } else {
                this.errorMessage = 'Error joining game: ' + error.message;
              }
            }
          );
        }
      },
      (error) => {
        this.errorMessage = 'Error checking player name: ' + error.message;
      }
    );
  }

  back(): void {
    this.router.navigate(['/start']);
  }
}
