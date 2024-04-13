import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  isAdmin: boolean = false;

  constructor() { }
}
