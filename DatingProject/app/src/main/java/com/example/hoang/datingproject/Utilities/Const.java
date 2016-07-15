package com.example.hoang.datingproject.Utilities;

import com.example.hoang.datingproject.Fragment.WriteNoteDialog;

/**
 * Created by hoang on 4/5/2016.
 */
public class Const {

    public static final String TAB_1_TAG = "Feeds";
    public static final String TAB_2_TAG = "Friends";
    public static final String TAB_3_TAG = "Messages";
    public static final String TAB_4_TAG = "Dating";
    public static final String TAB_5_TAG = "LastFragment";

    public static final String LOG_TAG = "DATING_LOG";

    public static int SPLASH_TIME_OUT = 2000;

    public static final int REQUEST_IMAGE_CAPTURE = 1;

    final public static int PICTURE_CHOOSE = 2;

    public static int COVER_CHOOSE = 3;

    public static int PROFILE_CHOOSE = 4;

    public static int NEW_NOTE = 5;

    public static int RETURN_NEW_NOTE = 6;

    public static final int VIEW_TYPE_ITEM = 0;

    public static final int VIEW_TYPE_LOADING = 1;

    public static final int SENDER_IMAGE = 7;

    public static final int SENDER_MESSAGE = 8;

    public static final int RECEIVER_IMAGE = 9;

    public static final int RECEIVER_MEASSAGE = 10;

    public static final int SEARCH_FRAGMENT = 11;

    public static final int ITEM_CONTENT = 12;

    public static final int ITEM_IMAGE = 13;

    public static final int ITEM_BOTH = 14;

    public static final int FROM_FRIEND_ADAPTER = 15;

    public static final int FROM_FEED_ADAPTER = 16;

    public static final int FROM_MESSAGE_ADAPTER = 17;

    // feed fragment
    public static final String OFFSET = "offset";
    public static final String LIMIT = "limit";
    public static final String ID = "id";
    public static final String POST_USERID = "userID";
    public static final String POST_CONTENT = "content";
    public static final String ITEMID = "itemID";
    public static final String REPLY_CONTENT = "comments";
    public static final String WRITE_USER = "writeUserID";
    public static final String REPORT_ITEM = "reportBy";
    public static final String YEAROLD_FROM = "year_from";
    public static final String YEAROLD_TO = "year_to";
    public static final String USERID1 = "userID1";
    public static final String USERID2 = "userID2";
    public static final String FROM_USER = "fromUser";
    public static final String MESSAGE = "message";
    public static final String USERID = "userId";
    public static final String PROFILE_IMAGE = "profileImage";
    public static final String NICK_NAME = "nickname";
    public static final String GENDER = "gender";
    public static final String BIRTHDAY = "birthday";
    public static final String ADDRESS = "address";
    public static final String POSTTIME = "posttime";
    public static final String DESCRIPTION = "description";
    public static final String RESULT = "result";
    public static final String DATA = "data";
    public static final String PROFILE_UID = "uid";
    public static final String FRIEND_ID = "friend_id";
    public static final String IMAGE = "image";
    public static final String REPLY_TO = "replyTo";

    public static final String NOTIFICATION_TITLE = "title";
    public static final String NOTIFICATION_CONTENT = "content";
    public static final String NOTIFICATION_POSTTIME = "posttime";

    public static final String IP = "172.16.61.29";

    //url
    public static final String SOCKET_URL = "http://" + IP + ":3000";
    public static final String LIST_TIMELINE_URL = "http://" + IP + "/timeline/list.json?";
    public static final String POST_TIMELINE_URL = "http://" + IP + "/timeline/post";
    public static final String REPLY_TIMELINE_URL = "http://" + IP + "/timeline/reply";
    public static final String REPORT_TIMELINE_URL = "http://" + IP + "/timeline/report/";
    public static final String DELETE_TIMELINE_URL = "http://" + IP + "/timeline/delete/";
    public static final String PROFILE_TIMELINE_URL = "http://" + IP + "/timeline/profile/list.json?id=";
    public static final String SEARCH_LIST_USER_URL = "http://" + IP + "/search/list.json?";
    public static final String CHAT_LIST_URL = "http://" + IP + "/chat/list.json?";
    public static final String CHAT_LOG_URL = "http://" + IP + "/chat/chat.json?" + OFFSET + "=0" + "&" + LIMIT + "=100";
    public static final String CHAT_SEND_URL = "http://" + IP + "/chat/send";
    public static final String NOTIFICATION_SEND_URL = "http://" + IP + "/notification/post";
    public static final String LIST_NOTIFICATION_URL = "http://" + IP + "/notification/list.json?";
    public static final String FOOT_PRINT_URL = "http://" + IP + "/notification/footprint.json?";
    public static final String POST_FOOT_PRINT_URL = "http://" + IP + "/notification/footprint/post";
    public static final String USER_PROFILE_URL = "http://" + IP + "/user/profile.json/";
    public static final String USER_PROFILE_UPDATE_URL = "http://" + IP + "/user/profile/update/";
    public static final String USER_PROFILE_DELETE_URL = "http://" + IP + "/user/profile/delete/";
    public static final String USER_CREATE_URL = "http://" + IP + "/user/post";
    public static final String USER_LIST_URL = "http://" + IP + "/user/list.json";

    public static final String FIRE_BASE_URL = "https://glowing-inferno-4146.firebaseio.com/Notification";

}
