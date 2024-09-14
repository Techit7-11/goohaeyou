package com.ll.goohaeyou.notification.infrastructure;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import com.ll.goohaeyou.notification.domain.NotificationSender;
import com.ll.goohaeyou.notification.domain.type.CauseTypeCode;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class FCMService implements NotificationSender {

    private static final String NOTIFICATION_TITLE = "GooHaeYou";
    private static final String TTL_HEADER = "300";

    @Override
    public void send(String token, String postTitle, CauseTypeCode causeTypeCode) {
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", TTL_HEADER)
                        .setNotification(new WebpushNotification(NOTIFICATION_TITLE,
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
        return switch (causeTypeCode) {
            case POST_MODIFICATION -> " 공고가 수정 되었습니다.";
            case POST_DELETED -> " 공고가 삭제 되었습니다.";
            case POST_INTERESTED -> " 공고에서 흥미를 받았습니다.";
            case POST_DEADLINE -> " 공고 지원서 접수가 마감 되었습니다";
            case COMMENT_CREATED -> " 공고에서 댓글이 작성 되었습니다.";
            case APPLICATION_CREATED -> " 공고에서 지원서를 받았습니다.";
            case APPLICATION_MODIFICATION -> " 공고의 지원서가 수정 되었습니다.";
            case APPLICATION_APPROVED -> " 공고에서 지원서가 승인 되었습니다.";
            case APPLICATION_UNAPPROVED -> " 공고에서 지원서가 미승인 되었습니다.";
            case CHATROOM_CREATED -> " 공고에서 채팅방이 생성 되었습니다.";
            case CALCULATE_PAYMENT -> " 공고에 대한 대금이 정산 되었습니다.";
        };
    }
}
