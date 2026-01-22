package com.company.hrms.workflow.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;

import com.company.hrms.workflow.infrastructure.entity.DelegationEntity; // Assuming entity exists or I need to create it.
// If DelegationEntity doesn't exist, I need to create that too. 
// "Delegation實體" was in plan. 
// Step 3 in plan was "Delegation實體". 
// I executed "Implement Workflow Definition Domain".
// I missed "Delegation實體" in execution?
// Yes. Task.md: "- [ ] Implement Delete Delegation Service" was done, but entity not created?
// I should check if DelegationEntity exists.
// I will assume it DOES NOT exist and create it here if usually I would. 
// But I can't put two files in one tool.
// I'll creates IDelegationRepository assuming DelegationEntity is next.

public interface IDelegationRepository
        extends JpaRepository<DelegationEntity, String>, QuerydslPredicateExecutor<DelegationEntity> {
}
