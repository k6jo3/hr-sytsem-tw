# Knowledge Base (HR & Project Management System)

This Knowledge Base consolidates all documentation related to the Human Resources and Project Management System, organized into five core categories.

## Directory Structure

### [01. Client Requirements](./01_Client_Requirements/)
Contains the formal request specifications and project planning documents.
- **Formal Spec:** `人力資源暨專案管理系統_正式需求規格書.md`
- **Project Plan:** `系統開發工作計畫書.md`
- **Review Reports:** `PM需求審查報告.md`

### [02. Requirements Analysis](./02_Requirements_Analysis/)
Contains the System Analysis (SA) documents for each service module.
- Includes merged PM Review Supplements where applicable.
- Covers modules 01-14 (IAM, Employee, Attendance, Payroll, etc.)

### [03. System Architecture](./03_System_Architecture/)
Contains the high-level architecture and detailed design specifications.
- **Architecture:** `系統架構設計文件.md`, `Generic-Library-Architecture.md`
- **Detailed Design:** `Detailed_Design/` folder (Merged System Design Specifications for all modules).
- **Domain Logic:** `Logic_Specs/01_IAM_Domain_Layer_and_Data.md` (IAM Domain Events, Logic, Data Sources)
- **Logic Specs:** `Logic_Specs/` folder (Specific business logic algorithms).
- **Reports:** `Reports/` folder (Compliance and consistency checks).

### [04. API Standards](./04_API_Standards/)
Contains the standards and templates for API documentation.
- **Template:** `API文件標準模板.md`
- **Plan:** `API詳細規格補齊工作計畫.md`
- *Note: Actual API specifications are integrated into the [Detailed Design](./03_System_Architecture/Detailed_Design/) documents.*

### [05. Testing Standards](./05_Testing_Standards/)
Contains the testing methodology, architecture, and contracts.
- **Methodology:** `測試方法論.md`, `Contract-Driven Testing Whitepaper`.
- **Contracts:** `Contracts/` folder (Business contracts/test scenarios for each service).

## Usage Guide

1. **For New Developers:** Start with `03_System_Architecture/架構驅動.md` and `01_Client_Requirements`.
2. **For QA/SDET:** Focus on `05_Testing_Standards` and the specific `Contracts`.
3. **For Architects:** Maintain the standards in `03` and `04`.
