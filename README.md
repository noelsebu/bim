# BIM Metadata API

A Spring Boot REST API for parsing and querying Building Information Model (BIM) metadata from IFC files. Built as a side project inspired by Autodesk Forma's AEC Data platform.

## What it does

Upload `.ifc` files (Industry Foundation Classes — the open standard for BIM data), and the API will:
- Parse the STEP-format file and extract building elements, storeys, and properties
- Store everything in PostgreSQL
- Let you query elements by type, level, or property value
- Process large files asynchronously via Kafka so uploads return instantly

---

## Tech Stack

| Layer | Technology |
|---|---|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Database | PostgreSQL |
| Messaging | Apache Kafka |
| ORM | Spring Data JPA / Hibernate |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build | Maven |
| Testing | JUnit 5, AssertJ |

---

## Project Structure

```
src/main/java/com/autodesk/bim/
├── BimApplication.java
├── config/
│   ├── KafkaProducerConfig.java
│   └── KafkaConsumerConfig.java
├── controller/
│   ├── BimModelController.java       # model upload + CRUD
│   ├── BimElementController.java     # element queries
│   └── ParseJobController.java       # async job status
├── domain/
│   ├── BimModel.java
│   ├── BimLevel.java                 # building storey
│   ├── BimElement.java               # wall, slab, door, etc.
│   ├── BimProperty.java              # key-value metadata
│   ├── ParseJob.java                 # async job tracker
│   └── ParseJobStatus.java           # QUEUED / PROCESSING / COMPLETED / FAILED
├── dto/                              # request and response objects
├── exception/                        # global error handling
├── kafka/
│   ├── KafkaTopicConfig.java
│   ├── ParseJobMessage.java
│   ├── BimParseProducer.java
│   └── BimParseConsumer.java
├── repository/                       # Spring Data JPA interfaces
└── service/
    ├── IfcParserService.java         # STEP file parser
    ├── BimModelService.java
    ├── BimElementService.java
    └── ParseJobService.java
```

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+
- Apache Kafka 3.x (for async upload feature)

### 1. Database setup

```sql
CREATE DATABASE bim_db;
```

### 2. Configure environment

Set the following environment variables or update `application.yml`:

```bash
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
KAFKA_BOOTSTRAP_SERVERS=localhost:9092   # optional, defaults to localhost:9092
BIM_UPLOAD_DIR=/tmp/bim-uploads         # optional, temp dir for async uploads
```

### 3. Run the app

```bash
mvn spring-boot:run
```

The API will be available at `http://localhost:8080`  
Swagger UI at `http://localhost:8080/swagger-ui.html`

---

## API Reference

### Models

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/models/upload` | Upload & parse an IFC file (synchronous) |
| `GET` | `/api/models` | List all uploaded models |
| `GET` | `/api/models/{id}` | Get model details |
| `GET` | `/api/models/{id}/levels` | Get all building storeys for a model |
| `DELETE` | `/api/models/{id}` | Delete a model and all its data |

### Elements

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/models/{id}/elements` | Query elements (filter by type, level, property) |
| `GET` | `/api/models/{id}/elements/{elementId}` | Get element with all properties |
| `GET` | `/api/models/{id}/elements/types` | List distinct element types |
| `GET` | `/api/models/{id}/elements/properties/names` | List all property names in model |
| `GET` | `/api/models/{id}/elements/properties/values?propertyName=Material` | List distinct values for a property |

#### Element query examples

```bash
# All walls in the model
GET /api/models/1/elements?type=IFCWALL

# All elements on Ground Floor
GET /api/models/1/elements?levelName=Ground Floor

# Walls on Level 1
GET /api/models/1/elements?type=IFCWALL&levelName=Level 1

# Elements where Material = Concrete
GET /api/models/1/elements?propertyName=Material&propertyValue=Concrete
```

### Async Processing (Kafka)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `POST` | `/api/models/upload/async` | Upload IFC — returns jobId immediately (202 Accepted) |
| `GET` | `/api/jobs/{jobId}` | Poll job status |
| `GET` | `/api/jobs` | List all parse jobs |

#### Async upload flow

```bash
# 1. Upload file — returns immediately
curl -X POST http://localhost:8080/api/models/upload/async \
  -F "file=@building.ifc"

# Response: 202 Accepted
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "QUEUED",
  "message": "IFC file accepted for processing. Poll the status URL for updates.",
  "statusUrl": "/api/jobs/550e8400-e29b-41d4-a716-446655440000"
}

# 2. Poll until COMPLETED
curl http://localhost:8080/api/jobs/550e8400-e29b-41d4-a716-446655440000

# Response when done:
{
  "jobId": "550e8400-e29b-41d4-a716-446655440000",
  "status": "COMPLETED",
  "modelId": 7,
  "totalElements": 342,
  "totalLevels": 5,
  "completedAt": "2026-04-05T11:32:44"
}
```

---

## IFC Support

The parser handles IFC files in STEP format (ISO 10303-21), covering IFC2X3 and IFC4 schemas.

**Supported element types:**

`IFCWALL` · `IFCWALLSTANDARDCASE` · `IFCSLAB` · `IFCCOLUMN` · `IFCBEAM` · `IFCDOOR` · `IFCWINDOW` · `IFCSTAIR` · `IFCROOF` · `IFCSPACE` · `IFCZONE` · `IFCPLATE` · `IFCMEMBER` · `IFCFOOTING` · `IFCPILE` · `IFCFURNISHINGELEMENT` · `IFCFLOWSEGMENT` · `IFCPIPE` · `IFCDUCT`

**Extracted data per element:**
- Global ID (GUID)
- Element type and name
- Object type and tag
- Assigned building storey
- All property sets (`Pset_WallCommon`, `Pset_SlabCommon`, etc.)

---

## Running Tests

```bash
mvn test
```

Tests use H2 in-memory database — no PostgreSQL or Kafka needed.

---

## Roadmap

- [ ] JWT authentication and role-based access
- [ ] Model versioning and revision history
- [ ] Element search across all models
- [ ] Pagination for large IFC files
- [ ] Export to CSV / JSON
- [ ] Autodesk Platform Services (APS/Forge) integration
- [ ] GraphQL API layer
