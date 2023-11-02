package com.foodiefinder.datapipeline.step;

import org.quartz.JobExecutionException;

public interface Step {
    void execute() throws JobExecutionException;
}