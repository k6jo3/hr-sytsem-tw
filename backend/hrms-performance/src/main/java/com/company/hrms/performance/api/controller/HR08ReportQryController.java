package com.company.hrms.performance.api.controller;

// ...

@Operation(summary="查詢績效分布",operationId="getDistribution")@GetMapping("/distribution/{cycleId}")public ResponseEntity<GetDistributionResponse>getDistribution(@PathVariable String cycleId,@CurrentUser JWTModel currentUser)throws Exception{GetDistributionRequest request=GetDistributionRequest.builder().cycleId(cycleId).build();return ResponseEntity.ok(getResponse(request,currentUser));}

@Operation(summary="匯出績效報表",operationId="exportReport")@GetMapping("/export/{cycleId}")public ResponseEntity<SuccessResponse>exportReport(@PathVariable String cycleId,@CurrentUser JWTModel currentUser)throws Exception{ExportReportRequest request=ExportReportRequest.builder().cycleId(cycleId).build();return ResponseEntity.ok(getResponse(request,currentUser));}}
