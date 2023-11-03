package com.foodiefinder.datapipeline.step;

import com.foodiefinder.datapipeline.processor.ItemProcessor;
import com.foodiefinder.datapipeline.reader.ItemReader;
import com.foodiefinder.datapipeline.writer.ItemWriter;
import lombok.Builder;
import org.quartz.JobExecutionException;

@Builder
public class ChunkOrientedStep<I, O> implements Step{
    private ItemReader<I> itemReader;
    private ItemProcessor<I, O> itemProcessor;
    private ItemWriter<O> itemWriter;

    @Override
    public void execute() throws JobExecutionException {
        try {
            itemReader.open();
            while (true) {
                I readInput = itemReader.read();
                if (readInput == null) {
                    if(itemReader.isEnd()) {
                        break;
                    }
                    else {
                        // 1개 url 읽어오기가 끝남
                        itemReader.update();
                    }
                }
                else {
                    // 1개 url 의 1개 페이징 읽어오기가 끝남
                    itemReader.update();
                    // TODO : 미 구현, 클래스를 입력 안할 시 에러가 발생하므로 먼저 주석처리 완성 후 변경
//                    O processOutput = itemProcessor.process(readInput);
//                    itemWriter.write(processOutput);
                }
            }
            itemReader.close();
        } catch (Exception e) {
            // 예외 처리 로직, 로깅
            itemReader.close();
            // 다시 재시도 익셉션 던짐
            throw new JobExecutionException();
        }
    }
}