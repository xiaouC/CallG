package com.yy2039.callguardian_1;

public class YYCommon {
    public static final int BT_CALL_GUARDIAN_MODE_ANNOUNCE = 0;
    public static final int BT_CALL_GUARDIAN_MODE_INTERNATIONAL = 1;
    public static final int BT_CALL_GUARDIAN_MODE_ANSWERPHONE = 2;
    public static final int BT_CALL_GUARDIAN_MODE_CUSTOM = 3;

    public static final int BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW = 0;
    public static final int BT_CALL_GUARDIAN_MODE_CUSTOM_ANNOUNCE = 1;
    public static final int BT_CALL_GUARDIAN_MODE_CUSTOM_BLOCK = 2;
    public static final int BT_CALL_GUARDIAN_MODE_CUSTOM_ANSWERPHONE = 3;

    public static final int CALLS_LIST_STATE_1 = 1;
    public static final int CALLS_LIST_STATE_2 = 2;
    public static final int CALLS_LIST_STATE_3 = 3;
    public static final int CALLS_LIST_STATE_4 = 4;

    // 
    public static final int DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALWAYS_INTERRUPT = 1;
    public static final int DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALLOW_ONLY_PRIORITY_INTERRUPTIONS = 2;
    public static final int DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_DONT_INTERRUPT = 3;

    public static final int DO_NOT_DISTURB_PRIORITY_INDEFINITELY = 1;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_8_HOURS = 2;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_4_HOURS = 3;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_3_HOURS = 4;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_2_HOURS = 5;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_1_HOURS = 6;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_45_MIN = 7;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_30_MIN = 8;
    public static final int DO_NOT_DISTURB_PRIORITY_FOR_15_MIN = 9;

    public static final int DO_NOT_DISTURB_CALLS_MESSAGES_FROM_ANYONE = 1;
    public static final int DO_NOT_DISTURB_CALLS_MESSAGES_FROM_STARRED_CONTACTS_ONLY = 2;
    public static final int DO_NOT_DISTURB_CALLS_MESSAGES_FROM_CONTACTS_ONLY = 3;

    // 
    public static final int OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED = 0;
    public static final int OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED = 1;

    public static final int OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED = 0;
    public static final int OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED = 1;

    public static final int OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED = 0;
    public static final int OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED = 1;

    public static final int OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED = 0;
    public static final int OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED = 1;

    // ListView 对应的 item 可能使用到的信息
    public class YYViewInfo {
        public YYViewInfo( int nObjectType, int nTextType ) {
            this.nObjectType = nObjectType;
            this.nTextType = nTextType;
        }

        int nObjectType;        // YY_VIEW_OBJECT_TYPE_BUTTON 等等
        int nTextType;          // YY_LIST_ADAPTER_UPDATE_TEXT_TYPE_STRING 等等
    }
}
