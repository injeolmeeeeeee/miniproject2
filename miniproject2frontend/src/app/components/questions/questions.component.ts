import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { PlayerService } from '../../service/player.service';
import { Player, Question } from '../../models';
import { QuestionStore } from '../../question.store';
import { WebSocketService } from '../../service/websocket.service';

@Component({
  selector: 'app-questions',
  templateUrl: './questions.component.html',
  styleUrls: ['./questions.component.css']
})
export class QuestionsComponent implements OnInit {
  gameId: string = '';
  playerName: string = '';
  question!: Question;
  userResponse!: string;
  score !: number;
  currentRound: number = 1;
  lastDisplayedQuestionNumber: number = 0;
  message !: any;
  currentQuestion!: Question;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private playerService: PlayerService,
    private webSocketService: WebSocketService,
    private questionStore: QuestionStore
  ) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(params => {
      this.gameId = params.get('gameId') || '';
      this.playerName = params.get('playerName') || '';
    });
      this.getNextQuestion();
  }

  getNextQuestion(): void {
    this.questionStore.getNextQuestion(this.currentRound, this.lastDisplayedQuestionNumber)
      .then(question => {
        if (question) {
          this.currentQuestion = question;
          this.lastDisplayedQuestionNumber++;
          console.log('Current question:', this.currentQuestion);
        } else {
          console.log('No more questions available for round ' + this.currentRound);
          this.webSocketService.sendEnd(this.gameId);
        }
      });
  }  

  goToNextRound(): void {
    this.currentRound++;
    this.lastDisplayedQuestionNumber = 0;
    this.getNextQuestion();
  }

  submit(): void {
    this.calculateScore();
    console.log("scores: ", this.score)
    const player: Player = {
      name: this.playerName,
      responses: [this.userResponse],
      score: this.score,
      gameId: this.gameId
    };
  
    this.playerService.saveResponsesAndScores(this.gameId, player).subscribe(
      () => {
        console.log('Responses and scores saved successfully.');
        this.webSocketService.sendResponseEvent(this.gameId);
        const questionString = JSON.stringify(this.currentQuestion);
        this.router.navigate(['/result', this.gameId, this.playerName], {
          queryParams: {
            question: questionString,
            currentRound: this.currentRound,
          }
        });
      },
      (error: any) => {
        console.error('Error saving responses and scores:', error);
      }
    );
  }
  
  calculateScore(): void {
    const words = this.userResponse.split(' ');
    this.score = words.filter(word => word.trim() !== '').length;
  }

  quit() {
    this.router.navigate(['/end']);
  }

}
