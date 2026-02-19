package com.example.tdddemo.controller;

import com.example.tdddemo.service.DynamicDataService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/data")
public class DynamicController {

    private final DynamicDataService dataService;

    public DynamicController(DynamicDataService dataService) {
        this.dataService = dataService;
    }

    @PostMapping("/{tableName}")
    public ResponseEntity<String> insertRecord(@PathVariable String tableName,
                                               @RequestBody Map<String, Object> data) {
        dataService.writeData(tableName, data);
        return ResponseEntity.status(201).body("Record inserted successfully");
    }

    @GetMapping("/{tableName}")
    public ResponseEntity<List<Map<String, Object>>> readRecords(@PathVariable String tableName) {
        return ResponseEntity.ok(dataService.readData(tableName));
    }
}
