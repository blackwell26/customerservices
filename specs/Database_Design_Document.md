For an enterprise-grade AI Customer Service Chatbot integrated with an e-commerce platform, the Database Design Document (DDD) should cover **Operational Data (PostgreSQL), AI Knowledge Data (Vector DB), Chat Data (MongoDB), Cache (Redis), and Analytics Data**.

# Database Design Document (DDD)

# AI-Powered Customer Service Chatbot for E-Commerce Platform

Version: 1.0

---

# 1. Purpose

This document defines the logical and physical database design required to support:

* E-Commerce Operations
* AI Customer Service Chatbot
* Retrieval-Augmented Generation (RAG)
* Customer Support
* Conversation Management
* Analytics and Reporting

---

# 2. Database Architecture Overview

## Polyglot Persistence Strategy

| Database              | Purpose                 |
| --------------------- | ----------------------- |
| PostgreSQL            | Transactional Data      |
| MongoDB               | Chat Conversations      |
| Redis                 | Cache & Session Store   |
| PostgreSQL + pgvector | AI Knowledge Embeddings |
| OpenSearch            | Search & Analytics      |

---

# 3. High-Level Data Domains

```text
Customer Domain
Product Domain
Order Domain
Payment Domain
Inventory Domain
Shipping Domain
Support Domain
Chatbot Domain
Knowledge Base Domain
Analytics Domain
```

---

# 4. PostgreSQL Operational Database

Schema Name:

```sql
commerce
```

---

# 5. Customer Domain

## customers

```sql
CREATE TABLE customers (
    customer_id UUID PRIMARY KEY,
    username VARCHAR(100) UNIQUE,
    email VARCHAR(255) UNIQUE,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone_number VARCHAR(30),

    status VARCHAR(20),

    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

### Purpose

Stores customer profiles.

---

## customer_addresses

```sql
CREATE TABLE customer_addresses (
    address_id UUID PRIMARY KEY,

    customer_id UUID NOT NULL,

    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),

    city VARCHAR(100),
    province VARCHAR(100),
    postal_code VARCHAR(20),
    country VARCHAR(100),

    is_default BOOLEAN,

    FOREIGN KEY(customer_id)
        REFERENCES customers(customer_id)
);
```

---

# 6. Product Domain

## products

```sql
CREATE TABLE products (
    product_id UUID PRIMARY KEY,

    sku VARCHAR(100) UNIQUE,
    isbn VARCHAR(30),

    title VARCHAR(255),
    description TEXT,

    category VARCHAR(100),

    price NUMERIC(12,2),

    active BOOLEAN,

    created_at TIMESTAMP,
    updated_at TIMESTAMP
);
```

---

## inventory

```sql
CREATE TABLE inventory (
    inventory_id UUID PRIMARY KEY,

    product_id UUID NOT NULL,

    quantity_available INTEGER,

    reorder_level INTEGER,

    updated_at TIMESTAMP,

    FOREIGN KEY(product_id)
        REFERENCES products(product_id)
);
```

---

# 7. Order Domain

## orders

```sql
CREATE TABLE orders (
    order_id UUID PRIMARY KEY,

    customer_id UUID NOT NULL,

    order_number VARCHAR(50) UNIQUE,

    status VARCHAR(30),

    subtotal NUMERIC(12,2),
    tax NUMERIC(12,2),
    shipping_fee NUMERIC(12,2),

    total_amount NUMERIC(12,2),

    created_at TIMESTAMP,

    FOREIGN KEY(customer_id)
        REFERENCES customers(customer_id)
);
```

---

## order_items

```sql
CREATE TABLE order_items (
    order_item_id UUID PRIMARY KEY,

    order_id UUID NOT NULL,
    product_id UUID NOT NULL,

    quantity INTEGER,

    unit_price NUMERIC(12,2),

    FOREIGN KEY(order_id)
        REFERENCES orders(order_id),

    FOREIGN KEY(product_id)
        REFERENCES products(product_id)
);
```

---

# 8. Payment Domain

## payments

```sql
CREATE TABLE payments (
    payment_id UUID PRIMARY KEY,

    order_id UUID NOT NULL,

    payment_provider VARCHAR(50),

    transaction_reference VARCHAR(255),

    payment_status VARCHAR(50),

    amount NUMERIC(12,2),

    created_at TIMESTAMP,

    FOREIGN KEY(order_id)
        REFERENCES orders(order_id)
);
```

---

## refunds

```sql
CREATE TABLE refunds (
    refund_id UUID PRIMARY KEY,

    payment_id UUID NOT NULL,

    refund_amount NUMERIC(12,2),

    refund_reason TEXT,

    refund_status VARCHAR(50),

    created_at TIMESTAMP,

    FOREIGN KEY(payment_id)
        REFERENCES payments(payment_id)
);
```

---

# 9. Shipping Domain

## shipments

```sql
CREATE TABLE shipments (
    shipment_id UUID PRIMARY KEY,

    order_id UUID NOT NULL,

    tracking_number VARCHAR(100),

    carrier VARCHAR(100),

    shipment_status VARCHAR(50),

    shipped_at TIMESTAMP,

    delivered_at TIMESTAMP,

    FOREIGN KEY(order_id)
        REFERENCES orders(order_id)
);
```

---

# 10. Support Domain

## support_tickets

```sql
CREATE TABLE support_tickets (
    ticket_id UUID PRIMARY KEY,

    customer_id UUID NOT NULL,

    category VARCHAR(100),

    priority VARCHAR(20),

    status VARCHAR(30),

    assigned_agent VARCHAR(100),

    created_at TIMESTAMP,

    FOREIGN KEY(customer_id)
        REFERENCES customers(customer_id)
);
```

---

## ticket_comments

```sql
CREATE TABLE ticket_comments (
    comment_id UUID PRIMARY KEY,

    ticket_id UUID NOT NULL,

    author_type VARCHAR(20),

    comment_text TEXT,

    created_at TIMESTAMP,

    FOREIGN KEY(ticket_id)
        REFERENCES support_tickets(ticket_id)
);
```

---

# 11. Chatbot Domain (MongoDB)

Database:

```text
chatbot_db
```

Collection:

```text
chat_sessions
```

Document Example

```json
{
  "_id": "session_001",

  "customerId": "cust001",

  "startedAt": "2026-01-01T12:00:00",

  "endedAt": null,

  "status": "ACTIVE",

  "messages": [
    {
      "sender": "CUSTOMER",
      "message": "Where is my order?",
      "timestamp": "2026-01-01T12:01:00"
    },
    {
      "sender": "BOT",
      "message": "Your order has shipped.",
      "timestamp": "2026-01-01T12:01:05"
    }
  ]
}
```

---

# 12. Human Escalation Domain

## chatbot_escalations

```sql
CREATE TABLE chatbot_escalations (
    escalation_id UUID PRIMARY KEY,

    session_id VARCHAR(100),

    customer_id UUID,

    ticket_id UUID,

    escalation_reason TEXT,

    escalated_at TIMESTAMP
);
```

---

# 13. AI Knowledge Base

Schema

```sql
knowledge
```

---

## knowledge_documents

```sql
CREATE TABLE knowledge_documents (
    document_id UUID PRIMARY KEY,

    title VARCHAR(500),

    document_type VARCHAR(100),

    source_url TEXT,

    version INTEGER,

    created_at TIMESTAMP
);
```

---

## knowledge_chunks

```sql
CREATE TABLE knowledge_chunks (
    chunk_id UUID PRIMARY KEY,

    document_id UUID,

    chunk_text TEXT,

    chunk_sequence INTEGER,

    FOREIGN KEY(document_id)
        REFERENCES knowledge_documents(document_id)
);
```

---

# 14. Vector Embedding Storage

Using pgvector.

```sql
CREATE EXTENSION vector;
```

---

## document_embeddings

```sql
CREATE TABLE document_embeddings (
    embedding_id UUID PRIMARY KEY,

    chunk_id UUID,

    embedding VECTOR(1536),

    created_at TIMESTAMP
);
```

---

## Vector Index

```sql
CREATE INDEX idx_document_embedding
ON document_embeddings
USING ivfflat (embedding vector_cosine_ops);
```

---

# 15. AI Prompt Management

## prompt_templates

```sql
CREATE TABLE prompt_templates (
    template_id UUID PRIMARY KEY,

    template_name VARCHAR(100),

    system_prompt TEXT,

    active BOOLEAN,

    created_at TIMESTAMP
);
```

---

# 16. AI Model Registry

## ai_models

```sql
CREATE TABLE ai_models (
    model_id UUID PRIMARY KEY,

    model_name VARCHAR(100),

    model_version VARCHAR(50),

    provider VARCHAR(50),

    deployment_type VARCHAR(50),

    active BOOLEAN
);
```

Examples:

```text
llama3:8b
llama3:70b
mistral
qwen3
gemma3
```

---

# 17. Analytics Domain

## chatbot_metrics

```sql
CREATE TABLE chatbot_metrics (
    metric_id UUID PRIMARY KEY,

    metric_date DATE,

    total_sessions INTEGER,

    total_messages INTEGER,

    successful_responses INTEGER,

    escalations INTEGER,

    average_response_time_ms BIGINT
);
```

---

# 18. Redis Data Model

## Session Cache

```text
session:{sessionId}
```

Stores:

```json
{
  "customerId":"123",
  "currentIntent":"ORDER_TRACKING",
  "conversationState":"ACTIVE"
}
```

TTL:

```text
30 minutes
```

---

## AI Context Cache

```text
rag:{queryHash}
```

Stores:

```text
Retrieved document chunks
```

TTL:

```text
1 hour
```

---

# 19. OpenSearch Indexes

## chat-logs

Stores:

* Conversation logs
* Searchable transcripts

## application-logs

Stores:

* Microservice logs

## ai-logs

Stores:

* Prompt execution logs
* Model performance logs

---

# 20. Data Retention Policy

| Data            | Retention |
| --------------- | --------- |
| Chat Sessions   | 2 Years   |
| Orders          | 7 Years   |
| Payments        | 7 Years   |
| Support Tickets | 5 Years   |
| AI Logs         | 1 Year    |
| Audit Logs      | 7 Years   |

---

# 21. Database Security

## Encryption

* TLS in transit
* AES-256 at rest

## Access Control

* RBAC
* Database roles
* Least privilege principle

## Auditing

* Data access logs
* Administrative activity logs

## PII Protection

* Email masking
* Phone masking
* Address encryption

---

# 22. Database Capacity Planning

Initial Capacity:

Customers:
1,000,000

Products:
500,000

Orders:
20,000,000

Chat Sessions:
50,000,000

Knowledge Chunks:
10,000,000

Vector Embeddings:
10,000,000+

Expected Growth:
20% annually

---

# 23. Backup and Recovery

PostgreSQL

* Daily Full Backup
* Hourly WAL Archiving

MongoDB

* Replica Set
* Daily Snapshot

Redis

* AOF Persistence

Vector Database

* Daily Backup

Recovery Objective:

RPO: 15 Minutes

RTO: 1 Hour

For a production-grade solution, the next step would be to split this database design into **microservice-owned schemas** (Customer Service DB, Product Catalog DB, Order DB, Payment DB, Chatbot DB, Knowledge Base DB) following Domain-Driven Design (DDD) and event-driven microservice principles rather than using a single shared database. This is the approach typically used in large-scale enterprise e-commerce platforms.
