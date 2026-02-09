package com.example.etldemo.repository;

import com.example.etldemo.entity.SalesRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SalesRepository extends JpaRepository<SalesRecord, Long> {
}
