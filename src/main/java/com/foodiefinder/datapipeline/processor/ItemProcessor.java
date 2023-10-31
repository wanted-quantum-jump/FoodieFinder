package com.foodiefinder.datapipeline.processor;

public interface ItemProcessor<I,O> {
    public O process(I item);
}