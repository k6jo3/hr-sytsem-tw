package com.company.hrms.common.test.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * 事件序列化測試輔助工具
 *
 * <p>提供事件物件的 JSON 序列化 / 反序列化 / 往返驗證方法，
 * 供跨服務事件整合測試使用。
 */
public final class EventSerializationTestHelper {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private EventSerializationTestHelper() {
        // 工具類禁止實例化
    }

    /**
     * 將事件物件序列化為 JSON 字串
     *
     * @param event 事件物件
     * @return JSON 字串
     */
    public static String toJson(Object event) {
        try {
            return OBJECT_MAPPER.writeValueAsString(event);
        } catch (Exception e) {
            throw new AssertionError("事件序列化失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將事件物件序列化後再反序列化（往返測試）
     *
     * @param event 原始事件物件
     * @param clazz 目標型別
     * @param <T>   事件型別
     * @return 反序列化後的物件
     */
    public static <T> T roundTrip(Object event, Class<T> clazz) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(event);
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new AssertionError("事件往返序列化失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 將事件物件轉為 JsonNode，便於欄位驗證
     *
     * @param event 事件物件
     * @return JsonNode
     */
    public static JsonNode toJsonNode(Object event) {
        try {
            String json = OBJECT_MAPPER.writeValueAsString(event);
            return OBJECT_MAPPER.readTree(json);
        } catch (Exception e) {
            throw new AssertionError("事件轉換 JsonNode 失敗: " + e.getMessage(), e);
        }
    }

    /**
     * 取得共用的 ObjectMapper 實例
     *
     * @return ObjectMapper
     */
    public static ObjectMapper objectMapper() {
        return OBJECT_MAPPER;
    }
}
