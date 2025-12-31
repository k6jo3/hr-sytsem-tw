package com.company.hrms.insurance.api.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.company.hrms.common.annotation.CurrentUser;
import com.company.hrms.common.controller.QueryBaseController;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.insurance.domain.model.aggregate.InsuranceLevel;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * HR05 投保級距 Query Controller
 */
@RestController
@RequestMapping("/api/v1/insurance/levels")
@Tag(name = "HR05-Level", description = "投保級距")
public class HR05LevelQryController extends QueryBaseController {

    @GetMapping
    @Operation(summary = "查詢投保級距", operationId = "getLevels")
    public ResponseEntity<List<InsuranceLevel>> getLevels(
            @Parameter(hidden = true) @CurrentUser JWTModel currentUser) throws Exception {

        return ResponseEntity.ok(getResponse(null, currentUser));
    }
}
