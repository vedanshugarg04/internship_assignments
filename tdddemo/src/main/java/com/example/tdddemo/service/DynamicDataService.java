package com.example.tdddemo.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.*;

@Service
public class DynamicDataService {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedJdbcTemplate;

    public DynamicDataService(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedJdbcTemplate = namedJdbcTemplate;
    }

    public List<Map<String, Object>> readData(String tableName) {
        if (!isValidTable(tableName)) {
            throw new IllegalArgumentException("Invalid Table Name: " + tableName);
        }

        if (getTableSchema(tableName).isEmpty()) {
            throw new RuntimeException("Table '" + tableName + "' does not exist in the database.");
        }

        String sql = "SELECT * FROM " + tableName + " LIMIT 100";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);

        for (Map<String, Object> row : rows) {
            for (Map.Entry<String, Object> entry : row.entrySet()) {
                if (entry.getValue() instanceof byte[]) {
                    String base64 = Base64.getEncoder().encodeToString((byte[]) entry.getValue());
                    entry.setValue(base64);
                }
            }
        }
        return rows;
    }

    public void writeData(String tableName, Map<String, Object> inputData) {
        if (!isValidTable(tableName)) {
            throw new IllegalArgumentException("Invalid Table Name: " + tableName);
        }

        Map<String, String> schema = getTableSchema(tableName);
        if (schema.isEmpty()) {
            throw new RuntimeException("Table '" + tableName + "' does not exist.");
        }

        StringBuilder columns = new StringBuilder();
        StringBuilder values = new StringBuilder();
        MapSqlParameterSource params = new MapSqlParameterSource();

        for (String jsonKey : inputData.keySet()) {
            String dbColName = jsonKey.toUpperCase();

            if (!schema.containsKey(dbColName)) continue;

            if (columns.length() > 0) {
                columns.append(", ");
                values.append(", ");
            }

            columns.append(jsonKey);
            values.append(":").append(jsonKey);

            Object typedValue = convertToDbType(inputData.get(jsonKey), schema.get(dbColName));
            params.addValue(jsonKey, typedValue);
        }

        if (columns.length() == 0) {
            throw new IllegalArgumentException("No valid columns provided for table " + tableName);
        }

        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
        namedJdbcTemplate.update(sql, params);
    }

    private Map<String, String> getTableSchema(String tableName) {
        return jdbcTemplate.execute((java.sql.Connection con) -> {
            Map<String, String> schema = new HashMap<>();
            DatabaseMetaData meta = con.getMetaData();
            try (ResultSet rs = meta.getColumns(null, null, tableName.toUpperCase(), null)) {
                while (rs.next()) {
                    schema.put(rs.getString("COLUMN_NAME").toUpperCase(), rs.getString("TYPE_NAME").toUpperCase());
                }
            }
            return schema;
        });
    }

    private Object convertToDbType(Object value, String dbType) {
        if (value == null) return null;
        String valStr = value.toString();

        if (dbType.contains("BLOB") || dbType.contains("BINARY")) {
            try {
                return Base64.getDecoder().decode(valStr);
            } catch (Exception e) {
                return valStr.getBytes();
            }
        }
        if (dbType.contains("TIMESTAMP") || dbType.contains("DATE")) {
            try {
                return Timestamp.valueOf(valStr.replace("T", " "));
            } catch (Exception e) {
                return valStr;
            }
        }
        if (dbType.contains("INT") || dbType.contains("DECIMAL") || dbType.contains("FLOAT")) {
            if (value instanceof Number) {
                return ((Number) value).longValue();
            }
            try {
                return Long.parseLong(valStr);
            } catch (Exception e) {
                return 0;
            }
        }
        return value;
    }

    private boolean isValidTable(String tableName) {
        return tableName.matches("^[a-zA-Z0-9_]+$");
    }
}
