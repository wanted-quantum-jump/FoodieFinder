package com.foodiefinder.datapipeline.job;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.observer.JobStateHandler;
import lombok.RequiredArgsConstructor;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OpenApiJobFailedListener implements JobListener {

    private final String RETRY_COUNT_KEY = "RETRY_COUNT";
    @Value("${RETRY_COUNT_VALUE}")
    private int retryCountValue;
    private final JobStateHandler jobStateHandler;

    @Override
    public String getName() {
        return "OpenApiJobFailedListener";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {

    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {

    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        if(jobException != null) {
            jobStateHandler.saveState(JobState.RETRY.name(), true);
            int retryCount = jobStateHandler.loadState(RETRY_COUNT_KEY, Integer.class)
                    .orElse(0);

            if (retryCount < retryCountValue) {
                jobStateHandler.saveState(RETRY_COUNT_KEY, retryCount + 1);
                jobException.setRefireImmediately(true);
            } else {
                jobStateHandler.saveState(JobState.RETRY.name(), false);
                jobStateHandler.saveState(JobState.NEXT.name(), true);

                jobStateHandler.saveState(RETRY_COUNT_KEY, 0);
                jobException.setRefireImmediately(true);
            }
        }
    }
}
