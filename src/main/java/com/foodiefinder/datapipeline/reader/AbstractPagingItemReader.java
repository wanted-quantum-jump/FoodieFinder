package com.foodiefinder.datapipeline.reader;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.observer.StateHandler;

import java.util.Map;
import java.util.Optional;

public  abstract class AbstractPagingItemReader<I> implements ItemReader<I> {
    private Map<String, String> params;
    private String pageSizeName;
    private int pageSize = 100;
    private String pageName;
    private int pageStartIndex = 0;
    private int currentPageIndex = 0;
    private StateHandler stateHandler;

    protected abstract I doRead();
    public I read() {
        I result = this.doRead();
        if (result == null) {
            return null;
        }
        currentPageIndex++;
        params.put(pageName, String.valueOf(currentPageIndex));

        return result;
    }

    protected abstract boolean isPagingEnd();
    @Override
    public boolean isEnd() {
        return isPagingEnd();
    }

    public void open() {
        params.put(pageSizeName, String.valueOf(pageSize));

        Optional<Boolean> optionalRetry = stateHandler.loadState(JobState.RETRY.name(), Boolean.class);

        if(optionalRetry.isPresent() && Boolean.TRUE.equals(optionalRetry.get())) {
            // 재시도 로직인경우 시작위치 변경
            currentPageIndex = stateHandler.loadState("currentPageIndex", Integer.class)
                    .orElse(pageStartIndex);

            params.put(pageName, String.valueOf(currentPageIndex));
        } else {
            currentPageIndex = pageStartIndex;
            params.put(pageName, String.valueOf(currentPageIndex));
        }
    }

    @Override
    public void update() {
        // 완료되었을때 다음 시작 위치 저장.
        stateHandler.saveState("currentPageIndex", currentPageIndex);
        stateHandler.saveState(JobState.RETRY.name(), false);
    }

    @Override
    public void close() {
        this.currentPageIndex = pageStartIndex;
        this.params.put(pageName, String.valueOf(pageStartIndex));
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    protected Map<String, String> getParams() {
        return params;
    }

    public void setPageSize(String pageSizeName, int pageSize) {
        this.pageSizeName = pageSizeName;
        this.pageSize = pageSize;
    }

    public void setPage(String pageName, int pageIndex) {
        this.currentPageIndex = pageIndex;
        this.pageName = pageName;
        this.pageStartIndex = pageIndex;
    }

    protected String getPageName() {
        return pageName;
    }

    protected int getPageStartIndex() {
        this.currentPageIndex = pageStartIndex;
        return pageStartIndex;
    }

    protected StateHandler getStateHandler() {
        return stateHandler;
    }

    public void setStateHandler(StateHandler stateHandler) {
        this.stateHandler = stateHandler;
    }
}
