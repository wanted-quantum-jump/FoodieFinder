package com.foodiefinder.datapipeline.processor;

public interface ItemProcessor<I,O> {
    O process(I item);
}