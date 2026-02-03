package com.company.hrms.workflow.application.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.company.hrms.common.application.pipeline.BusinessPipeline;
import com.company.hrms.common.model.JWTModel;
import com.company.hrms.common.service.QueryApiService;
import com.company.hrms.workflow.api.request.GetMyApplicationsRequest;
import com.company.hrms.workflow.api.response.MyApplicationsResponse;
import com.company.hrms.workflow.application.service.context.MyApplicationsContext;
import com.company.hrms.workflow.application.service.task.instance.FetchMyApplicationsTask;
import com.company.hrms.workflow.application.service.task.instance.TransformMyApplicationsResponseTask;

import lombok.RequiredArgsConstructor;

@Service("getMyApplicationsServiceImpl")
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetMyApplicationsServiceImpl
                implements QueryApiService<GetMyApplicationsRequest, Page<MyApplicationsResponse>> {

        private final FetchMyApplicationsTask fetchTask;
        private final TransformMyApplicationsResponseTask transformTask;

        @Override
        public Page<MyApplicationsResponse> getResponse(GetMyApplicationsRequest req, JWTModel currentUser,
                        String... args)
                        throws Exception {

                // 1. Prepare Pagination (Default sort by startedAt desc)
                Pageable pageable = PageRequest.of(0, 10, Sort.by("startedAt").descending());
                if (args != null && args.length >= 2) {
                        int page = Integer.parseInt(args[0]);
                        int size = Integer.parseInt(args[1]);
                        pageable = PageRequest.of(page, size, Sort.by("startedAt").descending());
                }

                // 2. Setup Context
                MyApplicationsContext context = new MyApplicationsContext(req, currentUser, pageable);

                // 3. Execute Pipeline
                BusinessPipeline.start(context)
                                .next(fetchTask)
                                .next(transformTask)
                                .execute();

                return context.getResponse();
        }
}
