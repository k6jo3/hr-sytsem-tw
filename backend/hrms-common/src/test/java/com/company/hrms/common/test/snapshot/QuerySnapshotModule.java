package com.company.hrms.common.test.snapshot;

import com.company.hrms.common.query.QueryGroup;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * QueryGroup 序列化模組
 * 提供 QueryGroup 專用的 ObjectMapper 配置
 *
 * <p>使用範例:
 * <pre>
 * ObjectMapper mapper = QuerySnapshotModule.createMapper();
 * String json = mapper.writeValueAsString(queryGroup);
 * </pre>
 */
public class QuerySnapshotModule extends SimpleModule {

    private static final long serialVersionUID = 1L;

    public QuerySnapshotModule() {
        super("QuerySnapshotModule");
        addSerializer(QueryGroup.class, new QueryGroupSerializer());
    }

    /**
     * 建立配置好的 ObjectMapper
     */
    public static ObjectMapper createMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new QuerySnapshotModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }
}
