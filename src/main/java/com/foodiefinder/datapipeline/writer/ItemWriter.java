package com.foodiefinder.datapipeline.writer;

public interface ItemWriter<I> {
    void write(I input);
}