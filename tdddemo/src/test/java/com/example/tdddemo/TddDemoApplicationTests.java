package com.example.tdddemo;

import com.example.tdddemo.service.DynamicDataService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.sql.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DynamicDataServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;
    @Mock
    private NamedParameterJdbcTemplate namedJdbcTemplate;
    @Mock
    private Connection connection;
    @Mock
    private DatabaseMetaData metaData;
    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private DynamicDataService service;

    @BeforeEach
    void setUp() throws SQLException {
        lenient().when(jdbcTemplate.execute(any(ConnectionCallback.class)))
                .thenAnswer(invocation -> {
                    ConnectionCallback action = (ConnectionCallback) invocation.getArgument(0);
                    return action.doInConnection(connection);
                });

        lenient().when(connection.getMetaData()).thenReturn(metaData);
    }

    @Test
    void readData_Success() throws SQLException {
        when(metaData.getColumns(any(), any(), eq("USERS"), any())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, false);
        when(resultSet.getString("COLUMN_NAME")).thenReturn("ID", "AVATAR");
        when(resultSet.getString("TYPE_NAME")).thenReturn("INT", "BLOB");

        Map<String, Object> mockRow = new HashMap<>();
        mockRow.put("ID", 1);
        mockRow.put("AVATAR", "Hello".getBytes()); // Raw DB bytes
        when(jdbcTemplate.queryForList(anyString())).thenReturn(List.of(mockRow));

        List<Map<String, Object>> result = service.readData("USERS");

        assertEquals(1, result.size());

        String expectedBase64 = Base64.getEncoder().encodeToString("Hello".getBytes());
        assertEquals(expectedBase64, result.get(0).get("AVATAR"));
    }

    @Test
    void readData_TableNotFound() throws SQLException {
        when(metaData.getColumns(any(), any(), anyString(), any())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(false);

        Exception exception = assertThrows(RuntimeException.class, () ->
                service.readData("UNKNOWN_TABLE")
        );
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    void writeData_Success() throws SQLException {
        String tableName = "EMPLOYEES";

        when(metaData.getColumns(any(), any(), eq(tableName), any())).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true, true, true, false);
        when(resultSet.getString("COLUMN_NAME")).thenReturn("ID", "SALARY", "JOINED");
        when(resultSet.getString("TYPE_NAME")).thenReturn("INT", "DECIMAL", "TIMESTAMP");

        Map<String, Object> input = new HashMap<>();
        input.put("ID", "100");
        input.put("SALARY", 5000.50);
        input.put("JOINED", "2023-01-01T10:00:00");

        service.writeData(tableName, input);

        ArgumentCaptor<MapSqlParameterSource> captor = ArgumentCaptor.forClass(MapSqlParameterSource.class);
        verify(namedJdbcTemplate).update(anyString(), captor.capture());

        MapSqlParameterSource params = captor.getValue();

        assertEquals(100L, params.getValue("ID"));
        assertEquals(5000L, params.getValue("SALARY"));
        assertTrue(params.getValue("JOINED") instanceof Timestamp);
    }
}
