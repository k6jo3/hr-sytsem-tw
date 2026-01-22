package com.company.hrms.training.api.request;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

import com.company.hrms.training.domain.model.valueobject.CourseCategory;
import com.company.hrms.training.domain.model.valueobject.CourseType;
import com.company.hrms.training.domain.model.valueobject.DeliveryMode;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
@Schema(description = "建立課程請求")
public class CreateCourseRequest {

    @Schema(description = "課程代碼", example = "TRN-001")
    @NotBlank(message = "課程代碼不能為空")
    private String courseCode;

    @Schema(description = "課程名稱", example = "Java 進階實戰")
    @NotBlank(message = "課程名稱不能為空")
    private String courseName;

    @Schema(description = "課程類型", example = "INTERNAL")
    @NotNull(message = "課程類型不能為空")
    private CourseType courseType;

    @Schema(description = "授課方式", example = "OFFLINE")
    @NotNull(message = "授課方式不能為空")
    private DeliveryMode deliveryMode;

    @Schema(description = "課程類別", example = "TECHNICAL")
    @NotNull(message = "課程類別不能為空")
    private CourseCategory category;

    @Schema(description = "課程描述")
    private String description;

    @Schema(description = "講師姓名", example = "張三")
    private String instructor;

    @Schema(description = "講師簡介")
    private String instructorInfo;

    @Schema(description = "訓練時數", example = "8.0")
    @NotNull(message = "訓練時數不能為空")
    private BigDecimal durationHours;

    @Schema(description = "最大人數", example = "30")
    private Integer maxParticipants;

    @Schema(description = "最小人數", example = "5")
    private Integer minParticipants;

    @Schema(description = "開始日期", example = "2026-06-01")
    @NotNull(message = "開始日期不能為空")
    @Future(message = "開始日期必須是未來")
    private LocalDate startDate;

    @Schema(description = "結束日期", example = "2026-06-01")
    @NotNull(message = "結束日期不能為空")
    private LocalDate endDate;

    @Schema(description = "上課時間", example = "09:00:00")
    private LocalTime startTime;

    @Schema(description = "下課時間", example = "18:00:00")
    private LocalTime endTime;

    @Schema(description = "上課地點", example = "會議室 A")
    private String location;

    @Schema(description = "課程費用", example = "0")
    private BigDecimal cost;

    @Schema(description = "是否必修", example = "false")
    private Boolean isMandatory;

    @Schema(description = "目標對象(JSON)", example = "[\"IT\", \"HR\"]")
    private String targetAudience;

    @Schema(description = "先修條件", example = "無")
    private String prerequisites;

    @Schema(description = "報名截止日", example = "2026-05-30")
    private LocalDate enrollmentDeadline;
}
