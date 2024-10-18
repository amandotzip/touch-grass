import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';


@Component({
  selector: 'app-quiz',
  templateUrl: './quiz.component.html',
  styleUrls: ['./quiz.component.css'],
  imports: [CommonModule],
  standalone: true
})
export class QuizComponent implements OnInit {
  questions = [
    {
      question: "What's your favorite season?",
      answers: ["Spring", "Summer", "Autumn", "Winter"],
      image: "../../assets/quiz-images/season.jpg" // Path to the image for this question
    },
    {
      question: "What type of landscape do you prefer?",
      answers: ["Mountains", "Beach", "Forest", "Desert"],
      image: "../../assets/quiz-images/landscape.jpg"
    },
    {
      question: "How do you like to spend your weekends?",
      answers: ["Hiking", "Reading", "Partying", "Relaxing"],
      image: "../../assets/quiz-images/_phoenix_slam_2.GIF" // Path to the image for this question
    }
  ];

  currentQuestionIndex = 0;
  selectedAnswer: string | null = null;
  showNextButton = false;

  constructor() {}

  ngOnInit(): void {}

  selectAnswer(answer: string) {
    this.selectedAnswer = answer;
    this.showNextButton = true;
  }

  nextQuestion() {
    this.currentQuestionIndex++;
    this.selectedAnswer = null;
    this.showNextButton = false;
  }

  isQuizFinished(): boolean {
    return this.currentQuestionIndex >= this.questions.length;
  }
}
