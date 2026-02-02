# HR09 Job Opening Contract

## Job Opening Search Contract

| Scenario ID | Description | Role | Request | Required Filters |
| :--- | :--- | :--- | :--- | :--- |
| JO_SC_001 | Search by Title | HR_RECRUITER | `{"jobTitle": "Engineer"}` | `jobTitle LIKE '%Engineer%'` |
| JO_SC_002 | Search by Department | HR_MANAGER | `{"departmentId": "D001"}` | `departmentId = 'D001'` |
| JO_SC_003 | Filter by Status | GUEST | `{"status": "OPEN"}` | `status = 'OPEN'` |
