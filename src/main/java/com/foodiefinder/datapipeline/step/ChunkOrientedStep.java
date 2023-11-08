package com.foodiefinder.datapipeline.step;

import com.foodiefinder.datapipeline.processor.ItemProcessor;
import com.foodiefinder.datapipeline.reader.ItemReader;
import com.foodiefinder.datapipeline.writer.ItemWriter;
import lombok.Builder;

@Builder
public class ChunkOrientedStep<I, O> implements Step{
    private ItemReader<I> itemReader;
    private ItemProcessor<I, O> itemProcessor;
    private ItemWriter<O> itemWriter;

    @Override
    public void execute() {
        itemReader.open();
        while (true) {
            I readInput = itemReader.read();
            if (readInput == null) {
                if (itemReader.isEnd()) {
                    break;
                } else {
                    itemReader.update();
                }
            } else {
                itemReader.update();
                O processOutput = itemProcessor.process(readInput);
                itemWriter.write(processOutput);
            }
        }
        itemReader.close();
    }
}