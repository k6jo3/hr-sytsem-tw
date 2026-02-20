import java.nio.file.*;
import java.util.regex.*;

public class SuppressWarningsAdder {
    public static void main(String[] args) throws Exception {
        String[] files = {
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-attendance/src/test/java/com/company/hrms/attendance/api/contract/AttendanceContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/test/java/com/company/hrms/common/test/base/BaseApiContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/test/java/com/company/hrms/common/test/base/BaseApiIntegrationTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/test/java/com/company/hrms/common/test/base/BaseRepositoryTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/test/java/com/company/hrms/common/test/contract/BaseContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/test/java/com/company/hrms/common/test/contract/CommandContractTestExample.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-document/src/main/java/com/company/hrms/document/api/controller/HR13DocumentQryController.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-document/src/test/java/com/company/hrms/document/application/service/DeleteDocumentServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-document/src/test/java/com/company/hrms/document/application/service/GenerateDocumentServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/test/java/com/company/hrms/iam/api/contract/AuthenticationApiContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/test/java/com/company/hrms/iam/api/contract/IamContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/test/java/com/company/hrms/iam/api/contract/ProfileApiContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/test/java/com/company/hrms/iam/application/service/auth/LoginServiceImplTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/test/java/com/company/hrms/iam/application/service/user/GetUserListServiceImplTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-notification/src/test/java/com/company/hrms/notification/api/controller/HR12NotificationCmdControllerTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-organization/src/test/java/com/company/hrms/organization/api/contract/OrganizationContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-performance/src/test/java/com/company/hrms/performance/application/service/StartCycleServiceEventTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-project/src/test/java/com/company/hrms/project/application/service/GetCustomerListServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-project/src/test/java/com/company/hrms/project/application/service/GetProjectListServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-recruitment/src/test/java/com/company/hrms/recruitment/application/contract/InterviewContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-recruitment/src/test/java/com/company/hrms/recruitment/application/contract/JobOpeningContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-recruitment/src/test/java/com/company/hrms/recruitment/application/contract/OfferContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-recruitment/src/test/java/com/company/hrms/recruitment/application/contract/ReportContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/test/java/com/company/hrms/reporting/contract/ReportingContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-timesheet/src/test/java/com/company/hrms/timesheet/application/service/GetMyTimesheetServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-timesheet/src/test/java/com/company/hrms/timesheet/application/service/GetProjectTimesheetSummaryServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-timesheet/src/test/java/com/company/hrms/timesheet/application/service/GetTimesheetSummaryServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/test/java/com/company/hrms/training/api/contract/TrainingApiContractTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/test/java/com/company/hrms/training/application/service/GetTrainingStatisticsServiceTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-workflow/src/test/java/com/company/hrms/workflow/api/controller/WorkflowCommandControllerTest.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/main/java/com/company/hrms/common/infrastructure/event/KafkaEventPublisher.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/main/java/com/company/hrms/common/infrastructure/persistence/querydsl/repository/QueryBaseRepository.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-common/src/main/java/com/company/hrms/common/query/Condition.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/main/java/com/company/hrms/iam/domain/service/PasswordResetTokenDomainService.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/main/java/com/company/hrms/iam/infrastructure/repository/RoleRepositoryImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-iam/src/main/java/com/company/hrms/iam/infrastructure/repository/UserRepositoryImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-insurance/src/main/java/com/company/hrms/insurance/infrastructure/event/InsuranceEventPublisher.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-notification/src/main/java/com/company/hrms/notification/infrastructure/channel/EmailChannelSender.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-notification/src/main/java/com/company/hrms/notification/infrastructure/channel/InAppChannelSender.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-notification/src/main/java/com/company/hrms/notification/infrastructure/persistence/repository/AnnouncementReadRecordRepositoryImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/api/controller/HR14ExportQryController.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/application/eventhandler/EmployeeEventHandler.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/application/service/export/task/LoadEmployeeRosterDataTask.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/application/service/report/GetEmployeeRosterServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/application/service/report/GetUtilizationRateServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-reporting/src/main/java/com/company/hrms/reporting/infrastructure/persistence/repository/DashboardRepositoryImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/main/java/com/company/hrms/training/api/controller/HR10StatisticsQryController.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/main/java/com/company/hrms/training/application/service/GetCertificatesServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/main/java/com/company/hrms/training/application/service/GetCoursesServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/main/java/com/company/hrms/training/application/service/GetEnrollmentsServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-training/src/main/java/com/company/hrms/training/application/service/GetMyTrainingsServiceImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-workflow/src/main/java/com/company/hrms/workflow/application/service/task/MapWorkflowInstanceToResponseTask.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-workflow/src/main/java/com/company/hrms/workflow/infrastructure/repository/ApprovalTaskRepositoryImpl.java",
            "f:/javawork/hr-sytsem-2/.claude/worktrees/agitated-cannon/backend/hrms-workflow/src/main/java/com/company/hrms/workflow/infrastructure/repository/WorkflowInstanceRepositoryImpl.java"
        };
        for (String file : files) {
            Path p = Paths.get(file);
            if (!Files.exists(p)) {
                System.out.println("File not found: " + file);
                continue;
            }
            String content = new String(Files.readAllBytes(p), "UTF-8");
            if (!content.contains("@SuppressWarnings(\"null\")")) {
                content = content.replaceFirst("(?s)((?:public\\s+)?(?:class|interface|enum)\\s+\\w+)", "@SuppressWarnings(\"null\")\n$1");
                Files.write(p, content.getBytes("UTF-8"));
                System.out.println("Updated " + file);
            }
        }
    }
}
