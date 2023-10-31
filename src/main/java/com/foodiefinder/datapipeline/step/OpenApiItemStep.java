package com.foodiefinder.datapipeline.step;

import com.foodiefinder.datapipeline.processor.ItemProcessor;
import com.foodiefinder.datapipeline.reader.ItemReader;
import com.foodiefinder.datapipeline.writer.ItemWriter;
import lombok.Builder;

@Builder
public class OpenApiItemStep<I, O> implements Step{
    private ItemReader<I> itemReader;
    private ItemProcessor<I, O> itemProcessor;
    private ItemWriter<O> itemWriter;

    @Override
    public void execute() {
        try {
            I readOutput = itemReader.read();
            O processOutput = itemProcessor.process(readOutput);
            itemWriter.write(processOutput);
        } catch (Exception e) {
            // 예외 처리 로직.
        }
    }
}