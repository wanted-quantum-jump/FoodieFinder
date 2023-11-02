package com.foodiefinder.datapipeline.reader;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.provider.UrlProvider;
import com.foodiefinder.datapipeline.util.HttpRequest;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;


public class OpenApiPagingItemReader<I> extends AbstractPagingItemReader<I>{

    private UrlProvider<String> urlProvider;
    private HttpRequest<I> httpRequest;

    /**
     * Java에서 제네릭 타입의 클래스 정보를 얻기 위해서는 리플렉션을 사용해야
     * 하지만 자바의 타입 소거 때문에 런타임에 제네릭 타입의 정보가 손실됩니다.
     * 다음과 같은 이유로 생성
     */
    private Class<I> itemType;

    private I endOfPageBody;
    private HttpStatusCode endOfPageHttpStatus;

    private List<String> apiUrlList;
    private int apiUrlListIndex = 0;


    @Override
    protected I doRead() {
        // Parameter 로 Url 을 만들고 요청을 한다.
        String url = urlProvider.getUrl(apiUrlList.get(apiUrlListIndex), getParams());
        ResponseEntity<I> responseEntity = httpRequest.sendRequest(url, itemType);

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
        }
        else{
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
        // 재시도 로직인경우 url 리스트 인덱스 변경, 재시도 횟수 초과후에는 다음 url로 변경
        Optional<Boolean> optionalRetry = getStateHandler().loadState(JobState.RETRY.name(), Boolean.class);
        Optional<Boolean> optionalNext = getStateHandler().loadState(JobState.NEXT.name(), Boolean.class);

        if(optionalRetry.isPresent() && Boolean.TRUE.equals(optionalRetry.get())) {
            apiUrlListIndex = getStateHandler().loadState("apiUrlListIndex", Integer.class)
                    .orElse(0);
        }
        else if (optionalNext.isPresent() && Boolean.TRUE.equals(optionalNext.get())) {
            apiUrlListIndex = getStateHandler().loadState("apiUrlListIndex", Integer.class)
                    .orElse(0) + 1;
        }

        httpRequest = new HttpRequest<>();
    }

    @Override
    public void update() {
        super.update();
        // 한번의 리드가 끝나면 상태 저장
        getStateHandler().saveState(JobState.NEXT.name(), false);
        getStateHandler().saveState("apiUrlListIndex",apiUrlListIndex);
    }

    @Override
    public void close() {
        super.close();
        apiUrlListIndex = 0;
    }

    // 특정 응답을 받으면 페이지의 끝 임을 인지하도록
    private boolean isAtEndOfPage(I body, HttpStatusCode httpStatus) {
        if (endOfPageHttpStatus.equals(httpStatus) && endOfPageBody.equals(body)){
            return true;
        }
        return false;
    }

    private boolean isAtEndOfUrlList() {
        return apiUrlList.size() <= apiUrlListIndex;
    }

    /**
     * 마지막 페이지의 body, status
     * @param body
     * @param httpStatus
     */
    public void setEndOfPageResponse(I body, HttpStatusCode httpStatus) {
        this.endOfPageBody = body;
        this.endOfPageHttpStatus = httpStatus;
    }

    // 페이징을 할 url 생성자
    public void setUrlProvider(UrlProvider urlProvider) {
        this.urlProvider = urlProvider;
    }

    public void setApiUrlList(List<String> apiUrlList) {
        this.apiUrlList = apiUrlList;
    }

    public void setItemType(Class<I> itemType) {
        this.itemType = itemType;
    }
}
