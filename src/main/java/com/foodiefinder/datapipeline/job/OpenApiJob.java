package com.foodiefinder.datapipeline.job;

import com.foodiefinder.datapipeline.processor.dto.CombineRestaurantProcessorResultData;
import com.foodiefinder.datapipeline.step.ChunkOrientedStep;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

@Slf4j
@RequiredArgsConstructor
public class OpenApiJob implements Job {
    private final ChunkOrientedStep<String, CombineRestaurantProcessorResultData> chunkOrientedStep;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            chunkOrientedStep.execute();

        } catch (Exception e) {
            log.error("작업 도중 에러 발생.",e);

            // JobListener 에서 처리
            throw new JobExecutionException();
        }
    }
}
