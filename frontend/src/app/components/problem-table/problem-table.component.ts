import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProblemService } from '../../services/problem.service';
import { FileService } from '../../services/file.service';
import {
  Problem, ProblemRequest, ProblemFile,
  Difficulty, QuestionType,
  DIFFICULTIES, QUESTION_TYPES, FILE_EXTENSIONS
} from '../../models/problem.model';

@Component({
  selector: 'app-problem-table',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './problem-table.component.html',
  styleUrl: './problem-table.component.css'
})
export class ProblemTableComponent implements OnInit {
  problems: Problem[] = [];
  filteredProblems: Problem[] = [];

  // Filter state
  filterDifficulty: Difficulty | '' = '';
  filterQuestionType: QuestionType | '' = '';
  filterMinRank: number | null = null;
  filterMaxRank: number | null = null;
  filterSearch = '';

  // Create/Edit modal
  showModal = false;
  editingProblem: Problem | null = null;
  formData: ProblemRequest = this.emptyForm();

  // Create file modal
  showFileModal = false;
  fileModalProblemId: number | null = null;
  newFileName = '';
  newFileExtension = 'txt';

  // Preview panel
  previewContent: string | null = null;
  previewFileName = '';
  previewVisible = false;

  // Constants
  difficulties = DIFFICULTIES;
  questionTypes = QUESTION_TYPES;
  fileExtensions = FILE_EXTENSIONS;

  constructor(
    private problemService: ProblemService,
    private fileService: FileService
  ) {}

  ngOnInit(): void {
    this.loadProblems();
  }

  loadProblems(): void {
    this.problemService.getAll().subscribe({
      next: (data) => {
        this.problems = data;
        this.applyFilters();
      },
      error: (err) => console.error('Failed to load problems:', err)
    });
  }

  applyFilters(): void {
    if (!this.filterDifficulty && !this.filterQuestionType &&
        !this.filterMinRank && !this.filterMaxRank && !this.filterSearch) {
      this.filteredProblems = [...this.problems];
      return;
    }

    this.problemService.search({
      difficulty: this.filterDifficulty || undefined,
      questionType: this.filterQuestionType || undefined,
      minRank: this.filterMinRank ?? undefined,
      maxRank: this.filterMaxRank ?? undefined,
      search: this.filterSearch || undefined
    }).subscribe({
      next: (data) => this.filteredProblems = data,
      error: (err) => console.error('Search failed:', err)
    });
  }

  clearFilters(): void {
    this.filterDifficulty = '';
    this.filterQuestionType = '';
    this.filterMinRank = null;
    this.filterMaxRank = null;
    this.filterSearch = '';
    this.applyFilters();
  }

  // Modal handlers
  openCreateModal(): void {
    this.editingProblem = null;
    this.formData = this.emptyForm();
    this.showModal = true;
  }

  openEditModal(problem: Problem): void {
    this.editingProblem = problem;
    this.formData = {
      problemCode: problem.problemCode,
      title: problem.title,
      leetcodeLink: problem.leetcodeLink,
      questionType: problem.questionType,
      solution: problem.solution,
      difficulty: problem.difficulty,
      customRank: problem.customRank
    };
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.editingProblem = null;
  }

  saveProblem(): void {
    if (this.editingProblem) {
      this.problemService.update(this.editingProblem.id, this.formData).subscribe({
        next: () => { this.closeModal(); this.loadProblems(); },
        error: (err) => console.error('Update failed:', err)
      });
    } else {
      this.problemService.create(this.formData).subscribe({
        next: () => { this.closeModal(); this.loadProblems(); },
        error: (err) => console.error('Create failed:', err)
      });
    }
  }

  deleteProblem(id: number): void {
    if (confirm('Are you sure you want to delete this problem and all associated files?')) {
      this.problemService.delete(id).subscribe({
        next: () => this.loadProblems(),
        error: (err) => console.error('Delete failed:', err)
      });
    }
  }

  // File operations
  openFileCreateModal(problemId: number): void {
    this.fileModalProblemId = problemId;
    this.newFileName = '';
    this.newFileExtension = 'txt';
    this.showFileModal = true;
  }

  closeFileModal(): void {
    this.showFileModal = false;
    this.fileModalProblemId = null;
  }

  createNewFile(): void {
    if (!this.fileModalProblemId || !this.newFileName) return;
    this.fileService.createFile(this.fileModalProblemId, {
      fileName: this.newFileName,
      fileExtension: this.newFileExtension
    }).subscribe({
      next: () => { this.closeFileModal(); this.loadProblems(); },
      error: (err) => console.error('File creation failed:', err)
    });
  }

  uploadFile(event: Event, problemId: number): void {
    const input = event.target as HTMLInputElement;
    if (!input.files?.length) return;
    this.fileService.uploadFile(problemId, input.files[0]).subscribe({
      next: () => this.loadProblems(),
      error: (err) => console.error('Upload failed:', err)
    });
    input.value = '';
  }

  openFile(file: ProblemFile): void {
    this.fileService.openFile(file);
  }

  deleteFileItem(fileId: number, event: Event): void {
    event.stopPropagation();
    if (confirm('Delete this file?')) {
      this.fileService.deleteFile(fileId).subscribe({
        next: () => this.loadProblems(),
        error: (err) => console.error('Delete file failed:', err)
      });
    }
  }

  // Preview on hover
  showPreview(file: ProblemFile): void {
    const textExts = ['txt', 'cpp', 'c', 'java', 'py', 'js', 'ts', 'go', 'rs', 'md', 'json', 'svg'];
    if (textExts.includes(file.fileExtension)) {
      this.fileService.getFileContent(file.id).subscribe({
        next: (res) => {
          this.previewContent = res.content;
          this.previewFileName = file.fileName;
          this.previewVisible = true;
        }
      });
    }
  }

  hidePreview(): void {
    this.previewVisible = false;
    this.previewContent = null;
  }

  // Helpers
  getFileIcon(ext: string): string {
    const entry = FILE_EXTENSIONS.find(f => f.ext === ext);
    return entry?.icon || 'insert_drive_file';
  }

  getDifficultyClass(d: Difficulty): string {
    return d.toLowerCase();
  }

  formatType(type: string): string {
    return type.replace(/_/g, ' ').replace(/\b\w/g, c => c.toUpperCase());
  }

  trackByProblem(index: number, item: Problem): number {
    return item.id;
  }

  private emptyForm(): ProblemRequest {
    return {
      problemCode: '',
      title: '',
      leetcodeLink: '',
      questionType: 'ARRAY',
      solution: '',
      difficulty: 'EASY',
      customRank: 5
    };
  }
}
