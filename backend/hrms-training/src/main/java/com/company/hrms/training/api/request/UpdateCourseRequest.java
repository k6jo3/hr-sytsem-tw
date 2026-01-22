package com.company.hrms.training.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "更新課程請求")
public class UpdateCourseRequest {

    @Schema(description = "課程名稱")
    private String courseName;

    @Schema(description = "課程類型")
    private CourseType courseType;

    @Schema(description = "授課方式")
    private DeliveryMode deliveryMode;

    @Schema(description = "課程類別")
    private CourseCategory category;

    @Schema(description = "課程描述")
    private String description;

    @Schema(description = "講師姓名")
    private String instructor;

    @Schema(description = "講師簡介")
    private String instructorInfo;

    @Schema(description = "訓練時數")
    private BigDecimal durationHours;

    @Schema(description = "最大人數")
    private Integer maxParticipants;

    @Schema(description = "最小人數")
    private Integer minParticipants;

    @Schema(description = "開始日期")
    private LocalDate startDate;

    @Schema(description = "結束日期")
    private LocalDate endDate;

    @Schema(description = "上課時間")
    private LocalTime startTime;

    @Schema(description = "下課時間")
    private LocalTime endTime;

    @Schema(description = "上課地點")
    private String location;

    @Schema(description = "課程費用")
    private BigDecimal cost;

    @Schema(description = "是否必修")
    private Boolean isMandatory;

    @Schema(description = "目標對象(JSON)")
    private String targetAudience;

    @Schema(description = "先修條件")
    private String prerequisites;

    @Schema(description = "報名截止日")
    private LocalDate enrollmentDeadline;
}
