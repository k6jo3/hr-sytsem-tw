# Frontend Implementation Checklist - HR04 Payroll Management

**Module Code:** HR04 (PAY)
**Status:** 🔴 Not Started

---

## 👤 Employee Self-Service (ESS)
- [x] **HR04-P06 My Payslips**
    - [x] Yearly payslip list
    - [x] Detailed earnings/deductions breakdown
    - [x] Encrypted PDF download (Password: ID last 4 digits)

---

## 👨‍💼 Administrator Features (薪資管理員)
- [x] **HR04-P01 Salary Structure Management**
    - [x] Employee salary system (Monthly/Hourly)
    - [x] Base salary configuration
    - [x] Effective date tracking
- [x] **HR04-P02 Payroll Item Configuration**
    - [x] Define earnings (Allowances, Bonuses)
    - [x] Define deductions
    - [x] Taxable/Insurable flag settings
- [x] **HR04-P03 Payroll Calculation Batches**
    - [x] Create monthly calculation batches
    - [x] Batch status tracking (Draft -> Calculating -> Completed)
- [x] **HR04-P04 Payroll Batch Details**
    - [x] Execute calculation engine (Integration with ATT/INS)
    - [x] Review individual employee results
    - [x] Batch delete/re-calculate
- [x] **HR04-P05 Payroll Approval Workflow**
    - [x] Manager approval for batch results
    - [x] Lockdown data after approval
- [x] **HR04-P07 Payroll History Query**
    - [x] Cross-employee search
    - [x] Historical record auditing
- [x] **HR04-P08 Bank Transfer Generation**
    - [x] Generate media files for bank systems
    - [x] Bank distribution summary

---

## 🛠️ Infrastructure & Polish
- [ ] Redux slice for Payroll state management
- [ ] PDF generation integration (Browser-side or Backend link)
- [ ] SAGA workflow monitoring in UI
- [ ] Unit tests for Salary Calculation ViewModel
