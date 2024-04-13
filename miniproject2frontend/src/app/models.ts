export interface Question {
  id: number;
  questionNumber: number;
  questionText: string;
  category: string;
  edition: string;
  level: number;
  source?: string;
}
  
export interface Player {
  name: string;
  score?: number;
  gameId: string;
  responses?: string[];
}

export interface Response {
  round: number;
  response: string;
}

export interface Game {
  gameId: string;
  edition: string;
  players: Player[];
}

export interface NextQuestionRequest {
  type: 'next_question';
}