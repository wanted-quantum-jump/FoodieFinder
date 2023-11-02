package com.foodiefinder.datapipeline.job;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.step.ChunkOrientedStep;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Value;

@RequiredArgsConstructor
public class OpenApiJob implements Job {
    private final String RETRY_COUNT_KEY = "RETRY_COUNT";
    @Value("${RETRY_COUNT_VALUE}")
    private int retryCountValue;
    private final JobStateHandler jobStateHandler;
    private final ChunkOrientedStep<String, String> chunkOrientedStep;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        jobStateHandler.setJobDataMap(context.getJobDetail().getJobDataMap());

        JobExecutionException jobExecutionException = null;

        try {
            // 작업 시작
            chunkOrientedStep.execute();

        } catch (Exception e) {
            jobStateHandler.saveState(JobState.RETRY.name(), true);
            int retryCount = jobStateHandler.loadState(RETRY_COUNT_KEY, Integer.class)
                    .orElse(0);

            if (retryCount < retryCountValue) {
                jobStateHandler.saveState(RETRY_COUNT_KEY, retryCount + 1);
                jobExecutionException = new JobExecutionException(e);
                jobExecutionException.setRefireImmediately(true); // 작업을 즉시 재시도
            } else {
                // 재 시도횟수 초과시 다음으로 항목으로 넘어가기
                jobStateHandler.saveState(JobState.RETRY.name(), false);
                jobStateHandler.saveState(JobState.NEXT.name(), true);

                jobStateHandler.saveState(RETRY_COUNT_KEY, 0);
                jobExecutionException = new JobExecutionException(e);
                jobExecutionException.setRefireImmediately(true); // 작업을 즉시 재시도
            }
        }

        if (jobExecutionException != null) {
            throw jobExecutionException;
        }
    }
}
