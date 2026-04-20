import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Problem, ProblemRequest, Stats, Difficulty, QuestionType } from '../models/problem.model';

@Injectable({ providedIn: 'root' })
export class ProblemService {
  private readonly apiUrl = 'http://localhost:8080/api/problems';

  constructor(private http: HttpClient) {}

  getAll(): Observable<Problem[]> {
    return this.http.get<Problem[]>(this.apiUrl);
  }

  getById(id: number): Observable<Problem> {
    return this.http.get<Problem>(`${this.apiUrl}/${id}`);
  }

  create(request: ProblemRequest): Observable<Problem> {
    return this.http.post<Problem>(this.apiUrl, request);
  }

  update(id: number, request: ProblemRequest): Observable<Problem> {
    return this.http.put<Problem>(`${this.apiUrl}/${id}`, request);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  search(filters: {
    difficulty?: Difficulty;
    questionType?: QuestionType;
    minRank?: number;
    maxRank?: number;
    search?: string;
  }): Observable<Problem[]> {
    let params = new HttpParams();
    if (filters.difficulty) params = params.set('difficulty', filters.difficulty);
    if (filters.questionType) params = params.set('questionType', filters.questionType);
    if (filters.minRank) params = params.set('minRank', filters.minRank.toString());
    if (filters.maxRank) params = params.set('maxRank', filters.maxRank.toString());
    if (filters.search) params = params.set('search', filters.search);
    return this.http.get<Problem[]>(`${this.apiUrl}/search`, { params });
  }

  getStats(): Observable<Stats> {
    return this.http.get<Stats>(`${this.apiUrl}/stats`);
  }
}
