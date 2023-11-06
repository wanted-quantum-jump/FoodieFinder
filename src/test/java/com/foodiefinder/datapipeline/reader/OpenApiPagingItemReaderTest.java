package com.foodiefinder.datapipeline.reader;

import com.foodiefinder.datapipeline.enums.JobState;
import com.foodiefinder.datapipeline.enums.OpenApiUrl;
import com.foodiefinder.datapipeline.job.JobStateHandler;
import com.foodiefinder.datapipeline.util.UrlParamsRequestStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;


class OpenApiPagingItemReaderTest {
    @Mock
    private JobStateHandler stateHandler;
    @Mock
    private UrlParamsRequestStrategy<String> requestStrategy;

    private OpenApiPagingItemReader<String> reader;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        reader = new OpenApiPagingItemReader<>();

        // StateHandler 설정
        reader.setStateHandler(stateHandler);

        // Url List 설정
        List<String> urlList = new ArrayList<>();
        OpenApiUrl[] values = OpenApiUrl.values();
        for (OpenApiUrl value : values) {
            urlList.add(value.getUrl());
        }
        reader.setApiUrlList(urlList);

        // UrlParamsRequestStrategy 로 불러오는 ItemType 클래스 넘기기
        reader.setItemType(String.class);

        // url 고정 파라미터 설정
        Map<String, String> params = new HashMap<>();
        params.put("KEY", "118b362899f04bcaaa06b7d0cd22c72f");
        params.put("Type", "json");
        reader.setParams(params);

        // 한번에 읽어 올 페이지 사이즈 이름설정
        reader.setPageSize("pSize", 1000);

        // 페이지 인덱스 파라미터 이름, 페이지 시작 인덱스 설정
        reader.setPage("pIndex", 1);

        // 끝 페이지 임을 알리는 Response 작성
        reader.setEndOfPageResponse("EndOfData", HttpStatusCode.valueOf(200));

        // 요청 방법
        reader.setRequest(requestStrategy);
    }

    @Test
    @DisplayName("doRead - 정상 읽기")
    public void givenNotEndOfDataResponse_whenDoRead_thenShouldReturnData(){
        // Given
        given(requestStrategy.sendRequest(anyString(), anyMap(), eq(String.class)))
                .willReturn(ResponseEntity.ok("Not EndOfData"));

        // When
        String result = reader.doRead();

        // Then
        assertNotNull(result);
    }

    @Test
    @DisplayName("doRead - 정상 읽기, 마지막 페이지")
    void givenEndOfDataResponse_whenDoRead_thenShouldNotReturnData() {
        // Given
        given(requestStrategy.sendRequest(anyString(), anyMap(), eq(String.class)))
                .willReturn(ResponseEntity.ok("EndOfData"));

        // When
        String result = reader.doRead();

        // Then
        assertNull(result);
        assertEquals(reader.getParams().get(reader.getPageName()),"1");
    }

    @Test
    @DisplayName("open - 재시도")
    void givenRetryState_whenOpen_thenShouldDataUpdate() {
        // Given
        given(stateHandler.loadState(JobState.RETRY.name(), Boolean.class))
                .willReturn(Optional.of(Boolean.TRUE));
        given(stateHandler.loadState("currentPageIndex", Integer.class))
                .willReturn(Optional.of(1));
        given(stateHandler.loadState(JobState.RETRY.name(), Boolean.class))
                .willReturn(Optional.of(Boolean.TRUE));
        given(stateHandler.loadState("apiUrlListIndex", Integer.class))
                .willReturn(Optional.of(10)); // 재시도 상태에 따른 인덱스
        // When
        reader.open();

        // Then
        assertEquals(stateHandler.loadState("currentPageIndex",Integer.class).get(),1);
        assertEquals(stateHandler.loadState("apiUrlListIndex",Integer.class).get(),10);
    }

    @Test
    @DisplayName("open - 다음으로 넘어가기")
    void givenNextState_whenOpen_thenShouldDataUpdate() {
        // Given
        given(stateHandler.loadState(JobState.NEXT.name(), Boolean.class))
                .willReturn(Optional.of(Boolean.FALSE));
        given(stateHandler.loadState("currentPageIndex", Integer.class))
                .willReturn(Optional.of(1));
        given(stateHandler.loadState(JobState.RETRY.name(), Boolean.class))
                .willReturn(Optional.of(Boolean.FALSE));
        given(stateHandler.loadState(JobState.NEXT.name(), Boolean.class))
                .willReturn(Optional.of(Boolean.TRUE));
        given(stateHandler.loadState("apiUrlListIndex", Integer.class))
                .willReturn(Optional.of(10)); // 재시도 상태에 따른 인덱스
        // When
        reader.open();

        // Then
        assertEquals(stateHandler.loadState("currentPageIndex",Integer.class).get(),1);
        assertEquals(stateHandler.loadState("apiUrlListIndex",Integer.class).get(),10);
    }
}