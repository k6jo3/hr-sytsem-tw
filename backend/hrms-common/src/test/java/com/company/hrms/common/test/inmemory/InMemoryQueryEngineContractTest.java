package com.company.hrms.common.test.inmemory;

import com.company.hrms.common.query.FilterUnit;
import com.company.hrms.common.query.LogicalOp;
import com.company.hrms.common.query.Operator;
import com.company.hrms.common.query.QueryGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InMemoryQueryEngine 合約測試
 * 驗證全部 12 個 Operator 在記憶體集合中的行為
 * 對齊 UltimateQueryEngine 的行為規格
 */
class InMemoryQueryEngineContractTest {

    private List<TestEntity> testData;

    @BeforeEach
    void setUp() {
        testData = List.of(
                new TestEntity("1", "王小明", "ACTIVE", 30, LocalDate.of(2020, 1, 15),
                        LocalDateTime.of(2024, 6, 1, 9, 0), "Engineering", null),
                new TestEntity("2", "李小花", "ACTIVE", 25, LocalDate.of(2021, 6, 20),
                        LocalDateTime.of(2024, 6, 15, 10, 30), "HR", "team-a"),
                new TestEntity("3", "張大明", "INACTIVE", 35, LocalDate.of(2019, 3, 10),
                        LocalDateTime.of(2024, 5, 1, 8, 0), "Engineering", "team-b"),
                new TestEntity("4", "陳美麗", "PROBATION", 28, LocalDate.of(2024, 1, 5),
                        LocalDateTime.of(2024, 7, 1, 14, 0), "Sales", null),
                new TestEntity("5", "林大壯", "ACTIVE", 40, LocalDate.of(2018, 11, 1),
                        LocalDateTime.of(2024, 4, 15, 16, 30), "Engineering", "team-a")
        );
    }

    // ==================== 基本 Operator 測試 ====================

    @Nested
    @DisplayName("EQ 運算子")
    class EqTests {
        @Test
        @DisplayName("字串相等")
        void stringEq() {
            QueryGroup group = QueryGroup.and().eq("status", "ACTIVE");
            long count = filter(group);
            assertThat(count).isEqualTo(3);
        }

        @Test
        @DisplayName("數字相等")
        void numberEq() {
            QueryGroup group = QueryGroup.and().eq("age", 30);
            long count = filter(group);
            assertThat(count).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("NE 運算子")
    class NeTests {
        @Test
        @DisplayName("不等於")
        void ne() {
            QueryGroup group = QueryGroup.and().ne("status", "ACTIVE");
            long count = filter(group);
            assertThat(count).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("比較運算子 GT/GTE/LT/LTE")
    class ComparisonTests {
        @Test
        void gt() {
            QueryGroup group = QueryGroup.and().gt("age", 30);
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 35, 40
        }

        @Test
        void gte() {
            QueryGroup group = QueryGroup.and().gte("age", 30);
            long count = filter(group);
            assertThat(count).isEqualTo(3); // 30, 35, 40
        }

        @Test
        void lt() {
            QueryGroup group = QueryGroup.and().lt("age", 30);
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 25, 28
        }

        @Test
        void lte() {
            QueryGroup group = QueryGroup.and().lte("age", 30);
            long count = filter(group);
            assertThat(count).isEqualTo(3); // 25, 28, 30
        }
    }

    @Nested
    @DisplayName("LIKE 運算子")
    class LikeTests {
        @Test
        @DisplayName("containsIgnoreCase（無通配符）")
        void containsIgnoreCase() {
            QueryGroup group = QueryGroup.and().like("name", "大");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 張大明, 林大壯
        }

        @Test
        @DisplayName("通配符前綴匹配")
        void wildcardPrefix() {
            QueryGroup group = QueryGroup.and().like("name", "%明");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 王小明, 張大明
        }

        @Test
        @DisplayName("通配符兩端匹配")
        void wildcardBoth() {
            QueryGroup group = QueryGroup.and().like("name", "%小%");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 王小明, 李小花
        }
    }

    @Nested
    @DisplayName("IN / NOT_IN 運算子")
    class InTests {
        @Test
        void in() {
            QueryGroup group = QueryGroup.and().in("status", "ACTIVE", "PROBATION");
            long count = filter(group);
            assertThat(count).isEqualTo(4); // 3 ACTIVE + 1 PROBATION
        }

        @Test
        void notIn() {
            QueryGroup group = QueryGroup.and()
                    .add(FilterUnit.notIn("status", "ACTIVE", "PROBATION"));
            long count = filter(group);
            assertThat(count).isEqualTo(1); // INACTIVE
        }
    }

    @Nested
    @DisplayName("BETWEEN 運算子")
    class BetweenTests {
        @Test
        void betweenNumbers() {
            QueryGroup group = QueryGroup.and()
                    .add(FilterUnit.between("age", 25, 30));
            long count = filter(group);
            assertThat(count).isEqualTo(3); // 25, 28, 30
        }

        @Test
        void betweenDates() {
            QueryGroup group = QueryGroup.and()
                    .add(FilterUnit.between("hireDate",
                            LocalDate.of(2020, 1, 1), LocalDate.of(2021, 12, 31)));
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 2020-01-15, 2021-06-20
        }
    }

    @Nested
    @DisplayName("IS_NULL / IS_NOT_NULL 運算子")
    class NullTests {
        @Test
        void isNull() {
            QueryGroup group = QueryGroup.and().isNull("teamId");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // id=1, id=4
        }

        @Test
        void isNotNull() {
            QueryGroup group = QueryGroup.and().isNotNull("teamId");
            long count = filter(group);
            assertThat(count).isEqualTo(3); // id=2, id=3, id=5
        }
    }

    // ==================== 複合條件測試 ====================

    @Nested
    @DisplayName("複合條件")
    class CompositeTests {
        @Test
        @DisplayName("AND 條件")
        void andConditions() {
            QueryGroup group = QueryGroup.and()
                    .eq("status", "ACTIVE")
                    .eq("department", "Engineering");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 王小明, 林大壯
        }

        @Test
        @DisplayName("OR 條件")
        void orConditions() {
            QueryGroup group = QueryGroup.or()
                    .eq("status", "INACTIVE")
                    .eq("status", "PROBATION");
            long count = filter(group);
            assertThat(count).isEqualTo(2);
        }

        @Test
        @DisplayName("巢狀 AND + OR 子群組")
        void nestedGroups() {
            // status = 'ACTIVE' AND (department = 'Engineering' OR department = 'HR')
            QueryGroup group = QueryGroup.and()
                    .eq("status", "ACTIVE")
                    .orGroup(sub -> sub
                            .eq("department", "Engineering")
                            .eq("department", "HR"));
            long count = filter(group);
            assertThat(count).isEqualTo(3); // 王小明, 李小花, 林大壯
        }
    }

    // ==================== null 值守衛測試 ====================

    @Nested
    @DisplayName("null 值守衛")
    class NullGuardTests {
        @Test
        @DisplayName("EQ 遇 null value 應跳過（視為不篩選）")
        void eqWithNullValue() {
            QueryGroup group = QueryGroup.and()
                    .eq("status", null)
                    .eq("department", "Engineering");
            long count = filter(group);
            // status=null 跳過 → 只篩 department=Engineering → 3筆
            assertThat(count).isEqualTo(3);
        }
    }

    // ==================== 空 QueryGroup 測試 ====================

    @Test
    @DisplayName("空 QueryGroup 應回傳全部資料")
    void emptyGroup() {
        QueryGroup group = QueryGroup.and();
        long count = filter(group);
        assertThat(count).isEqualTo(5);
    }

    @Test
    @DisplayName("null QueryGroup 應回傳全部資料")
    void nullGroup() {
        Predicate<TestEntity> predicate = InMemoryQueryEngine.toPredicate(null, TestEntity.class);
        long count = testData.stream().filter(predicate).count();
        assertThat(count).isEqualTo(5);
    }

    // ==================== 日期字串轉換測試 ====================

    @Nested
    @DisplayName("日期字串自動轉換")
    class DateConversionTests {
        @Test
        @DisplayName("LocalDate 字串比較")
        void localDateStringComparison() {
            QueryGroup group = QueryGroup.and()
                    .gte("hireDate", "2021-01-01");
            long count = filter(group);
            assertThat(count).isEqualTo(2); // 2021-06-20, 2024-01-05
        }

        @Test
        @DisplayName("LocalDateTime 字串比較")
        void localDateTimeStringComparison() {
            QueryGroup group = QueryGroup.and()
                    .gte("createdAt", "2024-06-01T00:00:00");
            long count = filter(group);
            assertThat(count).isEqualTo(3); // 2024-06-01, 2024-06-15, 2024-07-01
        }
    }

    // ==================== Helper ====================

    private long filter(QueryGroup group) {
        Predicate<TestEntity> predicate = InMemoryQueryEngine.toPredicate(group, TestEntity.class);
        return testData.stream().filter(predicate).count();
    }

    // ==================== 測試用實體 ====================

    /**
     * 測試用簡單實體（非 DDD 領域物件，純 POJO）
     */
    static class TestEntity {
        private String id;
        private String name;
        private String status;
        private int age;
        private LocalDate hireDate;
        private LocalDateTime createdAt;
        private String department;
        private String teamId;

        TestEntity(String id, String name, String status, int age,
                   LocalDate hireDate, LocalDateTime createdAt,
                   String department, String teamId) {
            this.id = id;
            this.name = name;
            this.status = status;
            this.age = age;
            this.hireDate = hireDate;
            this.createdAt = createdAt;
            this.department = department;
            this.teamId = teamId;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getStatus() { return status; }
        public int getAge() { return age; }
        public LocalDate getHireDate() { return hireDate; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public String getDepartment() { return department; }
        public String getTeamId() { return teamId; }
    }
}
