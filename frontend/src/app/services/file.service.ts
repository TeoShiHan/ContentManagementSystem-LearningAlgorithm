import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProblemFile, CreateFileRequest } from '../models/problem.model';

@Injectable({ providedIn: 'root' })
export class FileService {
  private readonly apiUrl = 'http://localhost:8080/api/files';

  constructor(private http: HttpClient) {}

  getFilesForProblem(problemId: number): Observable<ProblemFile[]> {
    return this.http.get<ProblemFile[]>(`${this.apiUrl}/problem/${problemId}`);
  }

  createFile(problemId: number, request: CreateFileRequest): Observable<ProblemFile> {
    return this.http.post<ProblemFile>(`${this.apiUrl}/problem/${problemId}/create`, request);
  }

  uploadFile(problemId: number, file: File): Observable<ProblemFile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ProblemFile>(`${this.apiUrl}/problem/${problemId}/upload`, formData);
  }

  getFileContent(fileId: number): Observable<{ content: string }> {
    return this.http.get<{ content: string }>(`${this.apiUrl}/${fileId}/content`);
  }

  saveFileContent(fileId: number, content: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/${fileId}/content`, { content });
  }

  deleteFile(fileId: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${fileId}`);
  }

  getDownloadUrl(fileId: number): string {
    return `${this.apiUrl}/${fileId}/download`;
  }

  getSupportedTypes(): Observable<{ [key: string]: string }> {
    return this.http.get<{ [key: string]: string }>(`${this.apiUrl}/supported-types`);
  }

  openFile(file: ProblemFile): void {
    switch (file.openWith) {
      case 'web-excalidraw':
        // Load content and open in Excalidraw
        window.open('https://excalidraw.com/', '_blank');
        break;
      case 'web-drawio':
        window.open('https://app.diagrams.net/', '_blank');
        break;
      case 'vscode':
        // Download to open in local editor
        window.open(this.getDownloadUrl(file.id), '_blank');
        break;
      default:
        window.open(this.getDownloadUrl(file.id), '_blank');
    }
  }
}
