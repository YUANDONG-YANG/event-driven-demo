package com.demo.azure.ingest;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StorageFileEventRepository extends JpaRepository<StorageFileEventEntity, Long> {

    List<StorageFileEventEntity> findTop50ByOrderByPersistedAtDesc();
}
