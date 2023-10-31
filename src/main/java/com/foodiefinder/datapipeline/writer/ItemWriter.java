package com.foodiefinder.datapipeline.writer;

public interface ItemWriter<I> {
    public void write(I input);
}