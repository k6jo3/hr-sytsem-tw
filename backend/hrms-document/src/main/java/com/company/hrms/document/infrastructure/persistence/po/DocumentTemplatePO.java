package com.company.hrms.document.infrastructure.persistence.po;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 文件範本持久化物件
 */
@Data
@Entity
@Table(name = "hrs_document_templates")
public class DocumentTemplatePO {
    @Id
    @Column(name = "template_id")
    private String id;

    @Column(name = "template_code", unique = true)
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "content")
    private String content;

    @Column(name = "category")
    private String category;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;
}
