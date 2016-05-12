package com.ciklum.firstresponder;

/**
 * Created by victorllana on 5/11/16.
 */
public class Urls {

    public static final String ROOM_ID = "Y2lzY29zcGFyazovL3VzL1JPT00vYzI1ZWIzZjAtMTdiMy0xMWU2LTllNDQtYzlkMTE2OTMzMGY4";
    public static final String AUTHORIZATION = "Bearer MTA5YWFhZjYtOWM2MS00NTdkLWFkMzctYWQwMzI2OWZlYjZlZTM3ZjUxZTctODQy";
    public static final String MESSAGES_URL = "https://api.ciscospark.com/v1/messages"; //https://api.ciscospark.com/v1/messages

    public static final String ROOM_ID_QUERY = "roomId";
    public static final String TEXT = "text";

    public static String getRoomMessagesUrl() {
        return MESSAGES_URL +
                "?" +
                ROOM_ID_QUERY +
                "=" +
                ROOM_ID;
    }

    public static String getPostRoomMessageUrl() {
        return MESSAGES_URL;
    }
}
