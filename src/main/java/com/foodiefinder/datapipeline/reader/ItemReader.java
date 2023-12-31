package com.foodiefinder.datapipeline.reader;

public interface ItemReader<I> {
    I read();
    void open();
    void update();
    void close();
    boolean isEnd();
}
