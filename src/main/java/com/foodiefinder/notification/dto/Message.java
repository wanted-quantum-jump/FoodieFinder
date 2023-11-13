package com.foodiefinder.notification.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Message<T> {

    // 전송될 URL
    String webhookUrl;

    //메시지 내용
    T data;

    public Message(String webhookUrl, T data) {
        this.webhookUrl = webhookUrl;
        this.data = data;
    }
}
