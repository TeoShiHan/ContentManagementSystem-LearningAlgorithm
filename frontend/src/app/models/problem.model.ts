export interface Problem {
  id: string;           // folder name on disk
  problemCode: string;
  title: string;
  leetcodeLink: string;
  questionType: QuestionType;
  solution: string;
  difficulty: Difficulty;
  customRank: number;
  files: ProblemFile[];
}

export interface ProblemRequest {
  problemCode: string;
  title: string;
  leetcodeLink: string;
  questionType: QuestionType;
  solution: string;
  difficulty: Difficulty;
  customRank: number;
}

export interface ProblemFile {
  id: string;           // file name on disk
  fileName: string;
  fileExtension: string;
  filePath: string;     // absolute path on disk
  fileSize: number;
  openWith: string;
}

export interface CreateFileRequest {
  fileName: string;
  fileExtension: string;
}

export interface Stats {
  totalProblems: number;
  easyCount: number;
  mediumCount: number;
  hardCount: number;
  byQuestionType: { [key: string]: number };
  averageCustomRank: number;
}

export type Difficulty = 'EASY' | 'MEDIUM' | 'HARD';

export type QuestionType =
  'ARRAY' | 'STRING' | 'HASH_TABLE' | 'DYNAMIC_PROGRAMMING' | 'MATH' |
  'SORTING' | 'GREEDY' | 'DEPTH_FIRST_SEARCH' | 'BINARY_SEARCH' |
  'BREADTH_FIRST_SEARCH' | 'TREE' | 'MATRIX' | 'TWO_POINTERS' |
  'BIT_MANIPULATION' | 'STACK' | 'HEAP' | 'GRAPH' | 'LINKED_LIST' |
  'SLIDING_WINDOW' | 'BACKTRACKING' | 'DIVIDE_AND_CONQUER' | 'TRIE' |
  'UNION_FIND' | 'MONOTONIC_STACK' | 'SEGMENT_TREE' | 'OTHER';

export const DIFFICULTIES: Difficulty[] = ['EASY', 'MEDIUM', 'HARD'];

export const QUESTION_TYPES: QuestionType[] = [
  'ARRAY', 'STRING', 'HASH_TABLE', 'DYNAMIC_PROGRAMMING', 'MATH',
  'SORTING', 'GREEDY', 'DEPTH_FIRST_SEARCH', 'BINARY_SEARCH',
  'BREADTH_FIRST_SEARCH', 'TREE', 'MATRIX', 'TWO_POINTERS',
  'BIT_MANIPULATION', 'STACK', 'HEAP', 'GRAPH', 'LINKED_LIST',
  'SLIDING_WINDOW', 'BACKTRACKING', 'DIVIDE_AND_CONQUER', 'TRIE',
  'UNION_FIND', 'MONOTONIC_STACK', 'SEGMENT_TREE', 'OTHER'
];

export const FILE_EXTENSIONS = [
  { ext: 'txt', label: 'Text File', icon: 'description' },
  { ext: 'md', label: 'Markdown', icon: 'article' },
  { ext: 'cpp', label: 'C++', icon: 'code' },
  { ext: 'c', label: 'C', icon: 'code' },
  { ext: 'java', label: 'Java', icon: 'code' },
  { ext: 'py', label: 'Python', icon: 'code' },
  { ext: 'js', label: 'JavaScript', icon: 'code' },
  { ext: 'ts', label: 'TypeScript', icon: 'code' },
  { ext: 'go', label: 'Go', icon: 'code' },
  { ext: 'rs', label: 'Rust', icon: 'code' },
  { ext: 'excalidraw', label: 'Excalidraw', icon: 'draw' },
  { ext: 'drawio', label: 'Draw.io', icon: 'account_tree' },
  { ext: 'json', label: 'JSON', icon: 'data_object' },
  { ext: 'svg', label: 'SVG', icon: 'image' },
];
