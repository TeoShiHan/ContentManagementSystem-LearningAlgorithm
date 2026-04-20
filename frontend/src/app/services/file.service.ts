import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProblemFile, CreateFileRequest } from '../models/problem.model';

@Injectable({ providedIn: 'root' })
export class FileService {
  private readonly apiUrl = 'http://localhost:8080/api/files';

  constructor(private http: HttpClient) {}

  createFile(folderId: string, request: CreateFileRequest): Observable<ProblemFile> {
    return this.http.post<ProblemFile>(`${this.apiUrl}/problem/${folderId}/create`, request);
  }

  uploadFile(folderId: string, file: File): Observable<ProblemFile> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<ProblemFile>(`${this.apiUrl}/problem/${folderId}/upload`, formData);
  }

  getFileContent(folderId: string, fileName: string): Observable<{ content: string }> {
    return this.http.get<{ content: string }>(`${this.apiUrl}/problem/${folderId}/${fileName}/content`);
  }

  saveFileContent(folderId: string, fileName: string, content: string): Observable<void> {
    return this.http.put<void>(`${this.apiUrl}/problem/${folderId}/${fileName}/content`, { content });
  }

  deleteFile(folderId: string, fileName: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/problem/${folderId}/${fileName}`);
  }

  openFileLocally(folderId: string, fileName: string): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/problem/${folderId}/${fileName}/open`, {});
  }

  getSupportedTypes(): Observable<{ [key: string]: string }> {
    return this.http.get<{ [key: string]: string }>(`${this.apiUrl}/supported-types`);
  }

  getStoragePath(): Observable<{ path: string }> {
    return this.http.get<{ path: string }>(`${this.apiUrl}/config/storage-path`);
  }

  setStoragePath(path: string): Observable<{ path: string }> {
    return this.http.post<{ path: string }>(`${this.apiUrl}/config/storage-path`, { path });
  }

  openFile(folderId: string, file: ProblemFile): void {
    this.openFileLocally(folderId, file.fileName).subscribe({
      error: (err) => console.error('Failed to open file locally:', err)
    });
  }
}

