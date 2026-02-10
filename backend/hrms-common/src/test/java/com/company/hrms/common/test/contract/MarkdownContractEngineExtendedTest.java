package com.company.hrms.common.test.contract;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * MarkdownContractEngine 擴展功能測試
 * 測試 JSON 合約解析和完整驗證功能
 */
class MarkdownContractEngineExtendedTest {

    private MarkdownContractEngine engine;

    @BeforeEach
    void setUp() {
        engine = new MarkdownContractEngine();
    }

    @Test
    @DisplayName("應能從 Markdown 中提取 JSON 合約")
    void shouldExtractJsonContractFromMarkdown() {
        String markdown = """
                # 測試合約

                ## 場景一

                ```json
                {
                  "scenarioId": "TEST_001",
                  "apiEndpoint": "GET /api/v1/test"
                }
                ```

                ## 場景二

                ```json
                {
                  "scenarioId": "TEST_002",
                  "apiEndpoint": "POST /api/v1/test"
                }
                ```
                """;

        String json = engine.extractJsonContract(markdown, "TEST_001");
        assertTrue(json.contains("\"scenarioId\": \"TEST_001\""));
        assertTrue(json.contains("\"apiEndpoint\": \"GET /api/v1/test\""));
    }

    @Test
    @DisplayName("當找不到指定場景 ID 時應拋出異常")
    void shouldThrowExceptionWhenScenarioNotFound() {
        String markdown = """
                ```json
                {
                  "scenarioId": "TEST_001"
                }
                ```
                """;

        assertThrows(IllegalArgumentException.class, () -> {
            engine.extractJsonContract(markdown, "TEST_999");
        });
    }

    @Test
    @DisplayName("應能解析 JSON 合約為 ContractSpec 物件")
    void shouldParseJsonToContractSpec() {
        String json = """
                {
                  "scenarioId": "TEST_001",
                  "apiEndpoint": "GET /api/v1/test",
                  "expectedQueryFilters": [
                    {"field": "status", "operator": "=", "value": "ACTIVE"}
                  ]
                }
                """;

        ContractSpec contract = engine.parseContract(json);

        assertEquals("TEST_001", contract.getScenarioId());
        assertEquals("GET /api/v1/test", contract.getApiEndpoint());
        assertNotNull(contract.getExpectedQueryFilters());
        assertEquals(1, contract.getExpectedQueryFilters().size());
    }

    @Test
    @DisplayName("應能驗證 API 回應的資料筆數")
    void shouldValidateResponseRecordCount() {
        String responseJson = """
                {
                  "data": {
                    "content": [
                      {"id": "1", "name": "Test 1"},
                      {"id": "2", "name": "Test 2"}
                    ]
                  }
                }
                """;

        ExpectedResponse expected = new ExpectedResponse();
        expected.setDataPath("data.content");
        expected.setMinRecords(1);
        expected.setMaxRecords(5);

        assertDoesNotThrow(() -> {
            engine.assertResponse(responseJson, expected, "TEST_001");
        });
    }

    @Test
    @DisplayName("當資料筆數不符時應拋出異常")
    void shouldThrowExceptionWhenRecordCountMismatch() {
        String responseJson = """
                {
                  "data": {
                    "content": []
                  }
                }
                """;

        ExpectedResponse expected = new ExpectedResponse();
        expected.setDataPath("data.content");
        expected.setMinRecords(1);

        ContractViolationException ex = assertThrows(ContractViolationException.class, () -> {
            engine.assertResponse(responseJson, expected, "TEST_001");
        });

        assertTrue(ex.getMessage().contains("資料筆數少於預期"));
    }

    @Test
    @DisplayName("應能驗證必要欄位的存在性和型別")
    void shouldValidateRequiredFields() {
        String responseJson = """
                {
                  "data": {
                    "content": [
                      {
                        "id": "550e8400-e29b-41d4-a716-446655440000",
                        "name": "Test User",
                        "age": 30,
                        "email": "test@example.com"
                      }
                    ]
                  }
                }
                """;

        ExpectedResponse expected = new ExpectedResponse();
        expected.setDataPath("data.content");

        List<RequiredField> requiredFields = new ArrayList<>();

        RequiredField idField = new RequiredField();
        idField.setName("id");
        idField.setType("uuid");
        requiredFields.add(idField);

        RequiredField nameField = new RequiredField();
        nameField.setName("name");
        nameField.setType("string");
        requiredFields.add(nameField);

        RequiredField ageField = new RequiredField();
        ageField.setName("age");
        ageField.setType("integer");
        requiredFields.add(ageField);

        RequiredField emailField = new RequiredField();
        emailField.setName("email");
        emailField.setType("email");
        requiredFields.add(emailField);

        expected.setRequiredFields(requiredFields);

        assertDoesNotThrow(() -> {
            engine.assertResponse(responseJson, expected, "TEST_001");
        });
    }

    @Test
    @DisplayName("當欄位型別不符時應拋出異常")
    void shouldThrowExceptionWhenFieldTypeMismatch() {
        String responseJson = """
                {
                  "data": {
                    "content": [
                      {"id": "invalid-uuid", "name": "Test"}
                    ]
                  }
                }
                """;

        ExpectedResponse expected = new ExpectedResponse();
        expected.setDataPath("data.content");

        List<RequiredField> requiredFields = new ArrayList<>();
        RequiredField idField = new RequiredField();
        idField.setName("id");
        idField.setType("uuid");
        requiredFields.add(idField);

        expected.setRequiredFields(requiredFields);

        ContractViolationException ex = assertThrows(ContractViolationException.class, () -> {
            engine.assertResponse(responseJson, expected, "TEST_001");
        });

        assertTrue(ex.getMessage().contains("不是有效的 UUID 格式"));
    }

    @Test
    @DisplayName("應能驗證遮罩格式欄位")
    void shouldValidateMaskedField() {
        String responseJson = """
                {
                  "data": {
                    "content": [
                      {"nationalId": "A12****789"}
                    ]
                  }
                }
                """;

        ExpectedResponse expected = new ExpectedResponse();
        expected.setDataPath("data.content");

        List<RequiredField> requiredFields = new ArrayList<>();
        RequiredField maskedField = new RequiredField();
        maskedField.setName("nationalId");
        maskedField.setType("string");
        maskedField.setFormat("masked");
        requiredFields.add(maskedField);

        expected.setRequiredFields(requiredFields);

        assertDoesNotThrow(() -> {
            engine.assertResponse(responseJson, expected, "TEST_001");
        });
    }

    @Test
    @DisplayName("應能驗證資料異動 - INSERT")
    void shouldValidateDataChangesInsert() {
        Map<String, List<Map<String, Object>>> beforeSnapshot = new HashMap<>();
        beforeSnapshot.put("users", new ArrayList<>());

        Map<String, List<Map<String, Object>>> afterSnapshot = new HashMap<>();
        List<Map<String, Object>> afterUsers = new ArrayList<>();
        Map<String, Object> newUser = new HashMap<>();
        newUser.put("id", "user-001");
        newUser.put("name", "Test User");
        afterUsers.add(newUser);
        afterSnapshot.put("users", afterUsers);

        List<ExpectedDataChange> expectedChanges = new ArrayList<>();
        ExpectedDataChange insertChange = new ExpectedDataChange();
        insertChange.setAction("INSERT");
        insertChange.setTable("users");
        insertChange.setCount(1);

        List<FieldAssertion> assertions = new ArrayList<>();
        FieldAssertion idAssertion = new FieldAssertion();
        idAssertion.setField("id");
        idAssertion.setOperator("notNull");
        assertions.add(idAssertion);

        insertChange.setAssertions(assertions);
        expectedChanges.add(insertChange);

        assertDoesNotThrow(() -> {
            engine.assertDataChanges(beforeSnapshot, afterSnapshot, expectedChanges, "TEST_001");
        });
    }

    @Test
    @DisplayName("應能驗證資料異動 - SOFT_DELETE")
    void shouldValidateDataChangesSoftDelete() {
        Map<String, List<Map<String, Object>>> beforeSnapshot = new HashMap<>();
        List<Map<String, Object>> beforeUsers = new ArrayList<>();
        Map<String, Object> user = new HashMap<>();
        user.put("id", "user-001");
        user.put("is_deleted", false);
        beforeUsers.add(user);
        beforeSnapshot.put("users", beforeUsers);

        Map<String, List<Map<String, Object>>> afterSnapshot = new HashMap<>();
        List<Map<String, Object>> afterUsers = new ArrayList<>();
        Map<String, Object> deletedUser = new HashMap<>();
        deletedUser.put("id", "user-001");
        deletedUser.put("is_deleted", true);
        afterUsers.add(deletedUser);
        afterSnapshot.put("users", afterUsers);

        List<ExpectedDataChange> expectedChanges = new ArrayList<>();
        ExpectedDataChange softDeleteChange = new ExpectedDataChange();
        softDeleteChange.setAction("SOFT_DELETE");
        softDeleteChange.setTable("users");
        softDeleteChange.setCount(1);
        expectedChanges.add(softDeleteChange);

        assertDoesNotThrow(() -> {
            engine.assertDataChanges(beforeSnapshot, afterSnapshot, expectedChanges, "TEST_001");
        });
    }

    @Test
    @DisplayName("應能驗證領域事件發布")
    void shouldValidateEvents() {
        List<Map<String, Object>> capturedEvents = new ArrayList<>();
        Map<String, Object> event = new HashMap<>();
        event.put("eventType", "UserCreatedEvent");

        Map<String, Object> payload = new HashMap<>();
        payload.put("userId", "user-001");
        payload.put("userName", "Test User");
        event.put("payload", payload);

        capturedEvents.add(event);

        List<ExpectedEvent> expectedEvents = new ArrayList<>();
        ExpectedEvent expectedEvent = new ExpectedEvent();
        expectedEvent.setEventType("UserCreatedEvent");

        List<FieldAssertion> payloadAssertions = new ArrayList<>();
        FieldAssertion userIdAssertion = new FieldAssertion();
        userIdAssertion.setField("userId");
        userIdAssertion.setOperator("notNull");
        payloadAssertions.add(userIdAssertion);

        expectedEvent.setPayload(payloadAssertions);
        expectedEvents.add(expectedEvent);

        assertDoesNotThrow(() -> {
            engine.assertEvents(capturedEvents, expectedEvents, "TEST_001");
        });
    }

    @Test
    @DisplayName("當缺少預期事件時應拋出異常")
    void shouldThrowExceptionWhenEventMissing() {
        List<Map<String, Object>> capturedEvents = new ArrayList<>();

        List<ExpectedEvent> expectedEvents = new ArrayList<>();
        ExpectedEvent expectedEvent = new ExpectedEvent();
        expectedEvent.setEventType("UserCreatedEvent");
        expectedEvents.add(expectedEvent);

        ContractViolationException ex = assertThrows(ContractViolationException.class, () -> {
            engine.assertEvents(capturedEvents, expectedEvents, "TEST_001");
        });

        assertTrue(ex.getMessage().contains("缺少預期的領域事件"));
    }
}
