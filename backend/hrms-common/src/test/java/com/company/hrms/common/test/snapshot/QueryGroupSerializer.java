package com.company.hrms.common.test.snapshot;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.QueryGroup;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * QueryGroup JSON 序列化器
 * 將 QueryGroup 序列化為簡潔可讀的 JSON 格式
 *
 * <p>輸出格式:
 * <pre>
 * {
 *   "junction": "AND",
 *   "conditions": [
 *     { "f": "status", "op": "EQ", "v": "ACTIVE" },
 *     { "f": "department.name", "op": "LIKE", "v": "研發" }
 *   ],
 *   "subGroups": [
 *     {
 *       "junction": "OR",
 *       "conditions": [...]
 *     }
 *   ]
 * }
 * </pre>
 */
public class QueryGroupSerializer extends JsonSerializer<QueryGroup> {

    @Override
    public void serialize(QueryGroup group, JsonGenerator gen, SerializerProvider provider)
            throws IOException {
        gen.writeStartObject();

        // 寫入 junction
        gen.writeStringField("junction", group.getJunction().name());

        // 寫入 conditions
        gen.writeArrayFieldStart("conditions");
        for (FilterUnit unit : group.getConditions()) {
            writeFilterUnit(unit, gen);
        }
        gen.writeEndArray();

        // 寫入 subGroups (遞迴)
        if (!group.getSubGroups().isEmpty()) {
            gen.writeArrayFieldStart("subGroups");
            for (QueryGroup subGroup : group.getSubGroups()) {
                serialize(subGroup, gen, provider);
            }
            gen.writeEndArray();
        }

        gen.writeEndObject();
    }

    /**
     * 寫入單一 FilterUnit
     */
    private void writeFilterUnit(FilterUnit unit, JsonGenerator gen) throws IOException {
        gen.writeStartObject();
        gen.writeStringField("f", unit.getField());
        gen.writeStringField("op", unit.getOp().name());

        // 根據值的類型決定如何寫入
        Object value = unit.getValue();
        if (value == null) {
            gen.writeNullField("v");
        } else if (value instanceof Number) {
            gen.writeObjectField("v", value);
        } else if (value instanceof Boolean) {
            gen.writeBooleanField("v", (Boolean) value);
        } else if (value.getClass().isArray()) {
            gen.writeArrayFieldStart("v");
            Object[] arr = (Object[]) value;
            for (Object item : arr) {
                gen.writeObject(item);
            }
            gen.writeEndArray();
        } else {
            gen.writeStringField("v", String.valueOf(value));
        }

        gen.writeEndObject();
    }

    @Override
    public Class<QueryGroup> handledType() {
        return QueryGroup.class;
    }
}
