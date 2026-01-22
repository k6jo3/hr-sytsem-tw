package com.company.hrms.workflow.infrastructure.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.company.hrms.workflow.domain.model.enums.FlowType;
import com.company.hrms.workflow.domain.model.enums.InstanceStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "workflow_instances")
@Getter
@Setter
public class WorkflowInstanceEntity {

    @Id
    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "definition_id")
    private String definitionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "flow_type")
    private FlowType flowType;

    @Column(name = "business_type")
    private String businessType;

    @Column(name = "business_id")
    private String businessId;

    @Column(name = "business_url")
    private String businessUrl;

    @Column(name = "applicant_id")
    private String applicantId;

    @Column(name = "applicant_name")
    private String applicantName;

    @Column(name = "department_id")
    private String departmentId;

    @Column(name = "department_name")
    private String departmentName;

    @Column(name = "summary")
    private String summary;

    @Column(name = "variables_json", columnDefinition = "TEXT")
    private String variablesJson;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private InstanceStatus status;

    @Column(name = "current_node_id")
    private String currentNodeId;

    @Column(name = "current_node_name")
    private String currentNodeName;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @OneToMany(mappedBy = "workflowInstance", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ApprovalTaskEntity> tasks = new ArrayList<>();
}
