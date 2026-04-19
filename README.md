# Event-driven demo — Azure Event Grid & Service Bus

Spring Boot **3.2** sample that models a **cloud-style pipeline** on your machine: local file detection → optional **Event Grid** publish → optional **Service Bus** send → **Service Bus consumer** → **JPA persistence**. Redis and PostgreSQL are optional; **H2** is the default for local profile.

## Design philosophy

1. **Observable pipeline** — Log lines are tagged with stages (`pipeline=1_STORAGE_WATCH` … `pipeline=5_DB_PERSIST`) and a **`traceId`** in MDC so you can follow one file end-to-end across components.
2. **Config-driven integration** — Azure features are toggled with `demo.azure.*` and `demo.storage.*` (see `application.yaml`, `application-local.yaml`, `application-cloud.yaml`). No Azure is required for `/api/review` or basic storage APIs.
3. **Separation of transport vs. domain** — Message **shape** for storage notifications is centralized (`LocalStorageFileNotificationFactory` + `StorageFileNotificationPayload`) so Event Grid and Service Bus stay aligned with what the consumer parses.
4. **Thin configuration classes** — Spring `@Configuration` beans wire Azure SDK clients or processor clients; **message handling** and **payload building** live in dedicated `@Component`s, not in config classes.
5. **Explicit profiles** — `local` vs `cloud`, plus `e2e` / `e2e-azure` for end-to-end demos with real Azure resources (see below).

## SOLID in this codebase

| Principle | How it shows up |
|-----------|-----------------|
| **S** — Single responsibility | `ServiceBusProcessorConfig` only builds and starts the `ServiceBusProcessorClient`. `ServiceBusIngestMessageHandler` only handles receive/complete/abandon and logging. `ServiceBusIngestService` only maps messages to entities and persists. `LocalStorageFileNotificationFactory` only builds notification maps from `StorageFileDetectedEvent`. |
| **O** — Open/closed | New transports or filters can be added by new components or factory methods without changing the core ingest mapping logic; toggles use Spring conditions (`@ConditionalOnProperty`, `@ConditionalOnBean`). |
| **L** — Liskov substitution | Spring Data repository and Azure clients are used through their intended abstractions; no fragile inheritance hierarchies in the domain. |
| **I** — Interface segregation | Small, focused collaborators (e.g. trace resolution) instead of one “god” service doing networking + parsing + persistence. |
| **D** — Dependency inversion | Application services depend on Spring-injected beans (`StorageFileEventRepository`, `ObjectMapper`, `ServiceBusTraceIdResolver`), not on concrete Azure wiring details inside config classes. |

Package-level intent is documented in `package-info.java` under `com.demo.azure.*`.

## Pipeline (conceptual)

```text
[Local WatchService] --> StorageFileDetectedEvent
       | optional                          | optional
       v                                   v
[Event Grid publish]              [Service Bus send]
       \___________ Cloud / E2E ___________/
                      |
                      v
            [Service Bus consumer] --> DB (ingest rows)
```

Cloud-native path (no local folder): **Blob** → **Event Grid** → (subscription) → **Service Bus** → your consumer — same consumer configuration idea as this app’s `demo.azure.service-bus` settings.

## Tech stack

- Java **17**, Spring Boot **3.2**, Spring Data JPA, optional Redis
- **Azure SDK**: Event Grid publisher, Service Bus sender & processor
- **springdoc-openapi** (Swagger UI)

## Spring profiles

| Profile(s) | Purpose |
|------------|---------|
| `local` (default) | H2 by default; local storage root; Azure integrations off unless you set env vars and flags. |
| `cloud` | PostgreSQL, env-driven storage and Azure settings for deployment. |
| `local` + `e2e` | Publish to Event Grid **and** send directly to Service Bus; consumer enabled — full shortcut pipeline. |
| `local` + `e2e-azure` | Publish to Event Grid only; Service Bus fed by **Azure Portal** event subscription; consumer still enabled. |

E2E profiles validate required Azure settings at startup (`E2eMandatoryAzureValidator`).

## Run

```bash
mvn spring-boot:run
```

Debug JVM on port **5005**:

```bash
mvn spring-boot:run -Pdebug
```

API smoke tests and notes: [`api-debug.http`](api-debug.http).  
Storage layout notes: [`src/main/resources/storage/README.txt`](src/main/resources/storage/README.txt).

## Configuration keys (short)

- `demo.storage.*` — root path, watch, forward to Event Grid / Service Bus  
- `demo.azure.event-grid.*` — topic endpoint & key  
- `demo.azure.service-bus.*` — connection string, queue or topic + subscription  

Use **environment variables** in production (`application-cloud.yaml`); never commit secrets.

## Repository

Upstream example: [event-driven-demo](https://github.com/YUANDONG-YANG/event-driven-demo) (deploy your fork as needed).
