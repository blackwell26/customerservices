# Technical Design Specification (TDS)

# AI-Powered Customer Service Chatbot for E-Commerce Platform

Version 1.0

---

# 1. Introduction

## 1.1 Purpose
This Technical Design Specification (TDS) defines the architectural blueprint and technical details for implementing the AI-Powered Customer Service Chatbot. It expands upon the Software Requirements Specification (SRS) to provide developers with a clear roadmap for implementation.

## 1.2 System Overview
The system is built on a modern stack:
*   **Frontend:** Angular (SPA)
*   **Backend:** Spring Boot (Java 21)
*   **AI Engine:** Ollama (Local LLM)
*   **Orchestration:** LangChain4j / Spring AI
*   **Data:** PostgreSQL (Structured & Vector), MongoDB (Logs)
*   **Messaging:** Kafka

---

# 2. System Architecture

## 2.1 High-Level Architecture Diagram (Logical)
`	ext
[ Customer Browser ] <--- WebSocket / REST ---> [ Spring Boot Backend ]
                                                       |
        +----------------------------------------------+-----------------------------------------+
        |                      |                       |                       |                 |
[ PostgreSQL ]          [ MongoDB ]             [ Kafka ]             [ Ollama LLM ]      [ External APIs ]
(Data & Vectors)       (Chat Logs)           (Event Broker)          (AI Generation)      (Order/Shipping)
`

## 2.2 RAG (Retrieval-Augmented Generation) Workflow
1.  **Ingestion:** Knowledge documents (PDF/Markdown) are chunked, embedded, and stored in **PGVector**.
2.  **Query:** User sends a query.
3.  **Retrieval:** The system generates an embedding for the query and retrieves the Top-K relevant chunks from PostgreSQL.
4.  **Augmentation:** The retrieved context is injected into a system prompt.
5.  **Generation:** The LLM (Llama 3/Mistral via Ollama) generates a grounded response.
6.  **Streaming:** The response is streamed back to the user via WebSocket (STOMP).

---

# 3. Component Design

## 3.1 Frontend (Angular)
*   **Version:** Angular 17+ with Standalone Components.
*   **Real-time Interaction:** RxStomp for WebSocket communication.
*   **State Management:** Reactive services using BehaviorSubject for session and chat history.
*   **UI Framework:** Vanilla CSS with custom utility classes (as per design preference) and Angular CDK for accessibility.

## 3.2 Backend (Spring Boot)
*   **Spring AI / LangChain4j Integration:**
    *   ChatClient for interacting with Ollama.
    *   EmbeddingClient for generating vector representations.
    *   VectorStore implementation using PgVectorStore.
*   **Security:** Spring Security with JWT (Stateless).
*   **API Layer:**
    *   REST Controllers for CRUD and Auth.
    *   WebSocket Message Mappings for real-time chat.

## 3.3 AI Layer (Ollama)
*   **Model:** llama3:8b or mistral:7b for generation.
*   **Embedding Model:** 
omic-embed-text or ll-minilm (local).
*   **Prompt Engineering:**
    *   System Prompt: "You are a helpful customer service assistant for [Company Name]. Use the provided context to answer questions accurately..."

---

# 4. Data Design

## 4.1 Relational Schema (PostgreSQL)
*   **customers**: id (UUID), username, email, password_hash, ole.
*   **orders**: id (UUID), customer_id, order_number, status, 	otal_amount, created_at.
*   **knowledge_base**: id (UUID), content (TEXT), metadata (JSONB), embedding (VECTOR(384)).

## 4.2 Document Store (MongoDB)
*   **chat_history**:
    `json
    {
      "sessionId": "UUID",
      "customerId": "UUID",
      "messages": [
        { "role": "user", "text": "...", "timestamp": "ISODate" },
        { "role": "assistant", "text": "...", "timestamp": "ISODate" }
      ],
      "metadata": { "model": "llama3", "latency": 1200 }
    }
    `

---

# 5. API Design

## 5.1 REST Endpoints
| Method | Endpoint | Description |
| :--- | :--- | :--- |
| POST | /api/v1/auth/login | Authenticate and get JWT |
| GET | /api/v1/orders/track/{number} | Get order status |
| POST | /api/v1/kb/upload | Upload knowledge base doc |

## 5.2 WebSocket (STOMP)
| Destination | Type | Description |
| :--- | :--- | :--- |
| /app/chat | SEND | Send user message |
| /user/queue/reply | SUBSCRIBE | Receive AI response |

---

# 6. Infrastructure & Deployment

## 6.1 Containerization
*   Dockerfile.backend: Multi-stage build for Spring Boot JAR.
*   Dockerfile.frontend: Nginx-based build for Angular SPA.

## 6.2 Docker Compose (Development)
`yaml
services:
  postgres:
    image: ankane/pgvector
  mongodb:
    image: mongo
  ollama:
    image: ollama/ollama
  kafka:
    image: confluentinc/cp-kafka
  backend:
    build: ./backend
  frontend:
    build: ./frontend
`

## 6.3 Security Considerations
*   **Encryption:** TLS for all data in transit.
*   **Sanitization:** Input validation on all user messages to prevent prompt injection.
*   **Rate Limiting:** Implemented via Spring Cloud Gateway or Bucket4j.

---

# 7. Success Metrics & Validation
*   **Accuracy:** Validated against a ground truth dataset.
*   **Performance:** < 500ms for retrieval; < 3s for generation (on GPU).
*   **Reliability:** 99.9% uptime for the backend services.
