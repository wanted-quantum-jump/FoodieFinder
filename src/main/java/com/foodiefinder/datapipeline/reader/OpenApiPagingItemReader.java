package com.foodiefinder.datapipeline.reader;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.util.response.RequestStrategy;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public class OpenApiPagingItemReader<I> extends AbstractPagingItemReader<I> {

    private RequestStrategy<I, Map<String, String>> request;
    private Class<I> itemType;

    private String endOfPageBody;
    private HttpStatusCode endOfPageHttpStatus;

    private List<String> apiUrlList;
    private int apiUrlListIndex = 0;


    @Override
    protected I doRead() {
        ResponseEntity<I> responseEntity = request.sendRequest(apiUrlList.get(apiUrlListIndex), getParams(), itemType);
        I body = responseEntity.getBody();
        HttpStatusCode statusCode = responseEntity.getStatusCode();

        if (isAtEndOfPage(body, statusCode)) {
            // 마지막 페이지
            if (!isAtEndOfUrlList()) {
                // 마지막 페이지지만 url list 는 안 끝남, 다음 Url index 로 진행
                apiUrlListIndex++;
                // 다음 인덱스로 진행하기에 page index 초기화
                getParams().put(getPageName(), String.valueOf(getPageStartIndex()));
                return null;
            }
        } else {
            // 마지막 페이지 아님 pageIndex 증가
            return body;
        }
        return null;
    }

    @Override
    protected boolean isPagingEnd() {
        return isAtEndOfUrlList();
    }

    @Override
    public void open() {
        super.open();
        Optional<Boolean> optionalRetry = getStateHandler().loadState(JobState.RETRY.name(), Boolean.class);
        Optional<Boolean> optionalNext = getStateHandler().loadState(JobState.NEXT.name(), Boolean.class);

        if (optionalRetry.isPresent() && Boolean.TRUE.equals(optionalRetry.get())) {
            apiUrlListIndex = getStateHandler().loadState("apiUrlListIndex", Integer.class)
                    .orElse(0);
        } else if (optionalNext.isPresent() && Boolean.TRUE.equals(optionalNext.get())) {
            apiUrlListIndex = getStateHandler().loadState("apiUrlListIndex", Integer.class)
                    .orElse(0) + 1;
        }
    }

    @Override
    public void update() {
        super.update();
        getStateHandler().saveState(JobState.NEXT.name(), false);
        getStateHandler().saveState("apiUrlListIndex", apiUrlListIndex);
    }

    @Override
    public void close() {
        super.close();
        apiUrlListIndex = 0;
    }

    private boolean isAtEndOfPage(I body, HttpStatusCode httpStatus) {
        if (endOfPageHttpStatus.equals(httpStatus) && endOfPageBody.equals(body)) {
            return true;
        }
        return false;
    }

    private boolean isAtEndOfUrlList() {
        return apiUrlList.size() <= apiUrlListIndex;
    }

    public void setEndOfPageResponse(String body, HttpStatusCode httpStatus) {
        this.endOfPageBody = body;
        this.endOfPageHttpStatus = httpStatus;
    }

    public void setApiUrlList(List<String> apiUrlList) {
        this.apiUrlList = apiUrlList;
    }

    public void setItemType(Class<I> itemType) {
        this.itemType = itemType;
    }

    public void setRequest(RequestStrategy<I, Map<String, String>> request) {
        this.request = request;
    }
}
