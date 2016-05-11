package com.ciklum.firstresponder;

/**
 * Created by victorllana on 5/11/16.
 */
public class Urls {
    public static String getRoomMessagesUrl() {
        return MessagesAdapter.MESSAGES_URL +
                "?" +
                MessagesAdapter.ROOM_ID_QUERY +
                "=" +
                MessagesAdapter.ROOM_ID;
    }
}
