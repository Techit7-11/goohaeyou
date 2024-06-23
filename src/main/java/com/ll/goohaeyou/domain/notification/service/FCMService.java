package com.ll.goohaeyou.domain.notification.service;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.ll.goohaeyou.domain.notification.entity.type.CauseTypeCode;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FCMService {

    public void send(String token, String postTitle, CauseTypeCode causeTypeCode) {
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification("GooHaeYou",
                                "\""+postTitle+"\""+getContent(causeTypeCode)))
                        .build())
                .build();

        try {
            String response = FirebaseMessaging.getInstance().sendAsync(message).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    private String getContent(CauseTypeCode causeTypeCode) {
        switch (causeTypeCode) {
            case POST_MODIFICATION :
                return " 공고가 수정 되었습니다.";
            case POST_DELETED :
                return " 공고가 삭제 되었습니다.";
            case POST_INTERESTED :
                return " 공고에서 흥미를 받았습니다.";
            case POST_DEADLINE :
                return " 공고 지원서 접수가 마감 되었습니다";
            case COMMENT_CREATED :
                return " 공고에서 댓글이 작성 되었습니다.";
            case APPLICATION_CREATED :
                return " 공고에서 지원서를 받았습니다.";
            case APPLICATION_MODIFICATION :
                return " 공고의 지원서가 수정 되었습니다.";
            case APPLICATION_APPROVED :
                return " 공고에서 지원서가 승인 되었습니다.";
            case APPLICATION_UNAPPROVE :
                return " 공고에서 지원서가 미승인 되었습니다.";
            case CHATROOM_CREATED :
                return " 공고에서 채팅방이 생성 되었습니다.";
            case CALCULATE_PAYMENT:
                return " 공고에 대한 대금이 정산 되었습니다.";
            default:
                throw new IllegalArgumentException("Unsupported cause type: " + causeTypeCode);
        }
    }
}
