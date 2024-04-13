import { Component, OnDestroy, OnInit } from '@angular/core';
import { SocialAuthService, } from '@abacritt/angularx-social-login';
import { GameCodeService } from '../../service/game-code.service';
import { Router } from '@angular/router';
import { Game } from '../../models';
import { AdminService } from '../../service/admin.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-create-game',
  templateUrl: './create-game.component.html',
  styleUrls: ['./create-game.component.css']
})
export class CreateGameComponent implements OnInit, OnDestroy {
  gameCode: string = '';
  selectedEdition: string = '';
  edition: string[] = ['FRIENDSHIP EDITION', 'BACK TO SCHOOL EDITION', 'EX-FRIEND EDITION'];
  gameId !: string;
  isAdmin: boolean = false;
  authSubscription!: Subscription;


  constructor(
    // private authService: SocialAuthService,
    private gameCodeService: GameCodeService,
    private router: Router,
    private adminService: AdminService
  ) { }

  onSelectEdition(edition: string): void {
    console.log('Selected edition:', edition);
    this.selectedEdition = edition;
  }

  createGame(): void {
    if (!this.selectedEdition) {
      console.error('Edition is not chosen.');
      return;
    }

    this.gameCodeService.createGame(this.selectedEdition).subscribe(
      (game: Game) => { 
        this.gameCode = game.gameId;
        this.adminService.isAdmin = true;
      },
      (error) => {
        console.error('Error creating game:', error);
      }
    );
  }

  next() {
    if (this.gameCode && this.selectedEdition) {
      this.router.navigate(['/enter', this.gameCode, this.selectedEdition]);
    } else {
      console.error('Game code or edition not available.');
    }
  }

  share() {
    if (this.gameCode) {
      var gameUrl = encodeURIComponent('https://your-game-url.com/enter-game/' + this.gameCode);
      var message = encodeURIComponent('WRNS: JOIN ME');
      var deepLinkUrl = 'https://telegram.me/share/url?url=' + gameUrl + '&text=' + message;
      window.open(deepLinkUrl);
    } else {
      console.error('No game code available.');
    }
  }

  back(): void {
    this.router.navigate(['/start']);
  }

  ngOnDestroy(): void {
    // this.authSubscription.unsubscribe();
  }

  ngOnInit() {
    // this.authSubscription = this.authService.authState.subscribe((user) => {
    //   console.log('user', user);
    // });
  }

  googleSignin(googleWrapper: any) {
    googleWrapper.click();
  }
}
