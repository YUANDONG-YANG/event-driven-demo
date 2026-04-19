Local XML / file storage: one folder per day (yyyy-MM-dd) under this directory.

Azure Event Grid does not watch a local folder. This app uses Java WatchService; when a NEW file appears,
optional forwarding sends a CloudEvent to an Event Grid custom topic and/or a JSON message to Service Bus.
The Service Bus consumer persists rows to the DB (H2 by default on profile "local"; use SPRING_DATASOURCE_* for Docker Postgres).

End-to-end (local demo):
  1) Start Docker: PostgreSQL + Redis (optional; H2 works without Postgres).
  2) Profiles: local,e2e — set AZURE_EVENTGRID_* and AZURE_SERVICEBUS_* (same queue for send + consume).
  3) After the app is running, create a NEW xml under a date folder (WatchService only fires on create).
  4) GET /api/ingest/storage-events to verify DB rows.

Config files: application.yaml (shared), application-local.yaml (profile local), application-cloud.yaml (profile cloud).

云上标准路径：Blob 上传 -> Event Grid ->（订阅）-> Service Bus -> 你的消费者。
