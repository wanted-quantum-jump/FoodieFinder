package com.foodiefinder.datapipeline.step;

import com.foodiefinder.datapipeline.processor.ItemProcessor;
import com.foodiefinder.datapipeline.reader.ItemReader;
import com.foodiefinder.datapipeline.writer.ItemWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class ChunkOrientedStepTest {
    @Mock
    private ItemReader<String> itemReader;

    @Mock
    private ItemProcessor<String, Integer> itemProcessor;

    @Mock
    private ItemWriter<Integer> itemWriter;

    private ChunkOrientedStep<String, Integer> chunkOrientedStep;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        chunkOrientedStep = ChunkOrientedStep.<String, Integer>builder()
                .itemReader(itemReader)
                .itemProcessor(itemProcessor)
                .itemWriter(itemWriter)
                .build();
    }

    @Test
    @DisplayName("execute - read 데이터 읽기 성공")
    void executeTest() {
        // Given
        when(itemReader.read()).thenReturn("data1", "data2", null, null);
        when(itemProcessor.process("data1")).thenReturn(1);
        when(itemProcessor.process("data2")).thenReturn(2);
        when(itemReader.isEnd()).thenReturn(false, true); // 처음은 false, 다음은 true로 끝남을 암시

        // When
        chunkOrientedStep.execute();
        
        // Then - 순서 방법
        InOrder inOrder = inOrder(itemReader, itemProcessor, itemWriter);

        inOrder.verify(itemReader).open();
        
        // 1번째 읽기 성공
        inOrder.verify(itemReader).read();
        inOrder.verify(itemReader).update();
        inOrder.verify(itemProcessor).process("data1");
        inOrder.verify(itemWriter).write(1);

        // 2번째 읽기 성공
        inOrder.verify(itemReader).read();
        inOrder.verify(itemReader).update();
        inOrder.verify(itemProcessor).process("data2");
        inOrder.verify(itemWriter).write(2);

        // 3번쨰 읽기 null - 마지막 아님
        inOrder.verify(itemReader).read();
        inOrder.verify(itemReader).isEnd();
        inOrder.verify(itemReader).update();

        // 4번째 읽기 null - 마지막
        inOrder.verify(itemReader).read();
        inOrder.verify(itemReader).isEnd();

        inOrder.verify(itemReader).close();
    }
}