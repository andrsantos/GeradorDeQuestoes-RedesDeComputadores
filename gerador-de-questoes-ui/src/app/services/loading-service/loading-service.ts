import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class LoadingService {
  private loadingSubject = new BehaviorSubject<boolean>(false);
  loading$ = this.loadingSubject.asObservable();

  private activeRequests = 0;
  private startTime = 0;
  
  private minDuration = 800; 

  show() {
    if (this.activeRequests === 0) {
      this.startTime = Date.now();
      this.loadingSubject.next(true); 
    }
    this.activeRequests++;
  }

  hide() {
    this.activeRequests--;
    
    if (this.activeRequests <= 0) {
      this.activeRequests = 0;
      
      const elapsedTime = Date.now() - this.startTime;
      const remainingTime = Math.max(0, this.minDuration - elapsedTime);

      if (remainingTime > 0) {
        setTimeout(() => {
          if (this.activeRequests === 0) {
            this.loadingSubject.next(false);
          }
        }, remainingTime);
      } else {
        this.loadingSubject.next(false);
      }
    }
  }
}