import { Injectable } from '@angular/core';
import { Dexie } from 'dexie';
import { Question } from './models';
import { Observable, Subject } from 'rxjs';

@Injectable()
export class QuestionStore extends Dexie {
  questions: Dexie.Table<Question, number>;
  private questionsSavedSubject: Subject<void> = new Subject<void>();

  constructor() {
    super('questions');
    this.version(1).stores({
        questions: '++id,questionText,category,edition,level,score,&questionNumber',
    });    
    this.questions = this.table('questions');
  }

  async handleQuestions(message: any): Promise<void> {
    const questions: Question[] = message.map((questionData: any) => {
      return {
        id: questionData.id,
        questionNumber: questionData.questionNumber,
        questionText: questionData.questionText,
        category: questionData.category,
        edition: questionData.edition,
        level: questionData.level,
      };
    });

    await this.questions.clear();
    await this.questions.bulkPut(questions);
    console.log('Questions saved to Dexie:', questions);
    this.questionsSavedSubject.next();
  }

  questionsSaved(): Observable<void> {
    return this.questionsSavedSubject.asObservable();
  }

  async getNextQuestion(round: number, lastDisplayedQuestionNumber: number): Promise<Question | undefined> {
    const question = await this.questions
      .where('questionNumber').above(lastDisplayedQuestionNumber)
      .first();

    if (question) {
      console.log('Question retrieved from Dexie:', question);
    } else {
      console.log('No more questions available for round ' + round);
    }

    return question;
  }

  async deleteQuestion(questionId: number): Promise<void> {
    await this.questions.delete(questionId);
  }
}
