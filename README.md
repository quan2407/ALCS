# Smart Note (ALCS)

Smart Note is a modern note-taking application designed to help users capture, organize, and review knowledge effectively.
The system focuses on transforming raw notes into structured knowledge units through AI-powered extraction mechanisms.

---

## Core Intelligence

The system is not just a text storage tool, but a knowledge processing engine:

### Knowledge Atomization (S.I.N.G)

Automatically decomposes notes into independent Knowledge Atoms.
Each atom is minimal, self-contained, and highly connected.

### Semantic Deduplication (768-dimensional Vectors)

Uses `gemini-embedding-2-preview` to generate vector representations for each knowledge unit.
The system applies cosine similarity to detect and remove semantically duplicated content, even when written differently.

---

## Tech Stack

### Backend (Core Service)

* Java 21 & Spring Boot 3
* Spring Data JPA (PostgreSQL)
* WebClient (Reactive) for communication with AI Service

### AI Service

* Python (FastAPI)
* Google Gemini 2.0 Flash (structured JSON extraction)
* Google Gemini Embedding (768-dimensional vector processing)
* NumPy (vector similarity computation)

### Frontend

* React + Vite + TypeScript
* Ant Design
* Zustand

---

## Architecture Overview

The system follows a lightweight microservices architecture:

1. Frontend (React) sends a `noteId` to Core Service
2. Core Service (Spring Boot) retrieves note content from the database
3. Core Service forwards the content to AI Service
4. AI Service performs atomization and semantic filtering
5. Core Service receives processed data and stores it in PostgreSQL

---

## Features and Roadmap

### Implemented

* Note Management: CRUD operations with a modern sidebar interface
* AI Extraction Workflow: End-to-end pipeline (Java → Python → Gemini)
* Semantic Deduplication: Vector-based filtering for knowledge redundancy

### Planned

* Smart Tagging: Automatic tagging based on semantic meaning
* Spaced Repetition System: Review system based on spaced repetition algorithms
* Knowledge Graph: Visualization of relationships between knowledge atoms

---

## Getting Started

### Clone repository

```bash id="c1"
git clone <your-repo-url>
cd project-root
```

### Run AI service

```bash id="c2"
cd ai-service
pip install -r requirements.txt
python main.py
```

AI Service runs at:

```
http://localhost:8000
```

### Run Core service

```bash id="c3"
cd core-service
./mvnw spring-boot:run
```

API runs at:

```
http://localhost:8080
```

### Run Frontend

```bash id="c4"
cd front-end
npm install
npm run dev
```

---

## Author

Quân Nguyễn
Software Developer at FPT Software

---

## License

This project is developed for educational and personal development purposes.
