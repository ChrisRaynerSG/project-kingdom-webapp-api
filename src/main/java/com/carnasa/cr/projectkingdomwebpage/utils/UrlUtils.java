package com.carnasa.cr.projectkingdomwebpage.utils;

public class UrlUtils {

    private UrlUtils() {
    }



    public static final String BASE_URL = "/api/v1/project-kingdom";

    public static final String DEV_LOG_POST_URL = BASE_URL + "/devLogs";
    public static final String DEV_LOG_POST_URL_ID = DEV_LOG_POST_URL + "/{postId}";
    public static final String DEV_LOG_POST_REPLY_URL = DEV_LOG_POST_URL_ID + "/replies";
    public static final String DEV_LOG_POST_LIKES_URL = DEV_LOG_POST_URL_ID + "/likes";
    public static final String DEV_LOG_POST_REPLY_URL_ID = DEV_LOG_POST_REPLY_URL + "/{replyId}";
    public static final String DEV_LOG_POST_REPLY_URL_ID_LIKES = DEV_LOG_POST_REPLY_URL_ID + "/likes";
    public static final String DEV_LOG_POST_CATEGORY_URL = DEV_LOG_POST_URL + "/categories";

    public static final String USER_URI = BASE_URL + "/users";
    public static final String USER_URI_ID = USER_URI + "/{id}";
    public static final String USER_URI_ME = USER_URI + "/me";

    public static final String ADMIN_URL = BASE_URL + "/admin";
    public static final String ADMIN_USERS_URL = ADMIN_URL + "/users";
    public static final String ADMIN_USERS_URL_ID = ADMIN_USERS_URL + "/{userId}";
}
