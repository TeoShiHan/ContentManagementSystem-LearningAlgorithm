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

  /** Ask the backend to open the file with the OS default app (e.g. VS Code, Excalidraw desktop). */
  openFileLocally(fileId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/${fileId}/open-local`, {});
  }

  getStoragePath(): Observable<{ path: string }> {
    return this.http.get<{ path: string }>(`${this.apiUrl}/config/storage-path`);
  }

  setStoragePath(path: string): Observable<{ path: string }> {
    return this.http.post<{ path: string }>(`${this.apiUrl}/config/storage-path`, { path });
  }

  openFile(file: ProblemFile): void {
    // Always open via backend so the OS uses the correct native app
    this.openFileLocally(file.id).subscribe({
      error: (err) => console.error('Failed to open file locally:', err)
    });
  }
}

