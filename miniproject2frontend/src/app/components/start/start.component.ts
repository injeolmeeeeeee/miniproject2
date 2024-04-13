import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-start',
  templateUrl: './start.component.html',
  styleUrls: ['./start.component.css']
})
export class StartComponent {

  constructor(private router: Router) {}

  navigateToCreate(): void {
    this.router.navigate(['/create']);
  }

  navigateToEnter(): void {
    this.router.navigate(['/enter']);
  }
}
