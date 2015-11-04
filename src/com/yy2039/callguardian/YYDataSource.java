package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.view.View;
import android.widget.TextView;
import android.widget.ImageButton;
import android.app.AlertDialog;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Log;
import android.content.Intent;
import android.widget.Toast;
import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.TextUtils;
import android.provider.ContactsContract;
import android.net.Uri;

public class YYDataSource {
    private boolean bBTCallGuardianModeOn;
    private Integer nBTCallGuardianMode;    // 最终选中的
    private boolean bIsUseDefaultMessage;
    private Integer nBTCallGuardianMode_Custom_BlockedNumber;
    private Integer nBTCallGuardianMode_Custom_AllowedNumber;
    private Integer nBTCallGuardianMode_Custom_International;
    private Integer nBTCallGuardianMode_Custom_Withheld;
    private Integer nBTCallGuardianMode_Custom_Payphones;
    private Integer nBTCallGuardianMode_Custom_MobileNumbers;
    private Integer nBTCallGuardianMode_Custom_Unavailable;
    private Integer nBTCallGuardianMode_Custom_AllOtherNumbers;
    private Integer nMobileCallsMode;
    private Integer nInternationalCallsMode;
    private Integer nPreminumRateCallsMode;
    private Integer nAllDialledCallsMode;
    private boolean bIsFirstTimeUse;
    private String strPINNumber;

    private Integer nDoNotDisturb_CallsAndNotificationsArrive_Mode;
    private Integer nDoNotDisturb_Priority;
    private boolean bDoNotDisturb_EventAndReminders;
    private boolean bDoNotDisturb_Calls;
    private boolean bDoNotDisturb_Messages;
    private Integer nDoNotDisturb_Calls_Messages_From;
    private boolean bDoNotDisturb_Monday;
    private boolean bDoNotDisturb_Tuesday;
    private boolean bDoNotDisturb_Wednesday;
    private boolean bDoNotDisturb_Thursday;
    private boolean bDoNotDisturb_Friday;
    private boolean bDoNotDisturb_Saturday;
    private boolean bDoNotDisturb_Sunday;
    private Integer nDoNotDisturb_StartTimeHour;
    private Integer nDoNotDisturb_StartTimeMin;
    private Integer nDoNotDisturb_EndTimeHour;
    private Integer nDoNotDisturb_EndTimeMin;

    public interface concactSyncSuccessfully {
        void onSuccessfully();
    }

    public interface callsListItem {
        String getName();
        String getNumber();
        String getCallTime();
        int getState();
    }

    public interface contactsListItem {
        String getName();
        String getNumber();
    }

    private boolean bIsFirstTimeUseContacts = true;

    private CallGuardianActivity main_activity;
    public YYDataSource( CallGuardianActivity activity ) {
        main_activity = activity;

        bBTCallGuardianModeOn = true;
        nBTCallGuardianMode = YYCommon.BT_CALL_GUARDIAN_MODE_ANNOUNCE;
        bIsUseDefaultMessage = true;
        nBTCallGuardianMode_Custom_BlockedNumber = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_AllowedNumber = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_International = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_Withheld = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_Payphones = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_MobileNumbers = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_Unavailable = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;
        nBTCallGuardianMode_Custom_AllOtherNumbers = YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW;

        nDoNotDisturb_CallsAndNotificationsArrive_Mode = YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALWAYS_INTERRUPT;
        nDoNotDisturb_Priority = YYCommon.DO_NOT_DISTURB_PRIORITY_INDEFINITELY;
        bDoNotDisturb_EventAndReminders = true;
        bDoNotDisturb_Calls = true;
        bDoNotDisturb_Messages = true;
        nDoNotDisturb_Calls_Messages_From = YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_ANYONE;
        bDoNotDisturb_Monday = true;
        bDoNotDisturb_Tuesday = true;
        bDoNotDisturb_Wednesday = true;
        bDoNotDisturb_Thursday = true;
        bDoNotDisturb_Friday = true;
        bDoNotDisturb_Saturday = false;
        bDoNotDisturb_Sunday = false;
        nDoNotDisturb_StartTimeHour = 10;
        nDoNotDisturb_StartTimeMin = 10;
        nDoNotDisturb_EndTimeHour = 20;
        nDoNotDisturb_EndTimeMin = 20;

        nMobileCallsMode = YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED;
        nInternationalCallsMode = YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED;
        nPreminumRateCallsMode = YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED;
        nAllDialledCallsMode = YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED;
        bIsFirstTimeUse = true;
        strPINNumber = "";

        // 请求 BTＣall Guardian 设置
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GCCS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_GCCS ) );
            }
            public void onRecv( String data ) {
                String[] results = data.split( "," );

                // 00 为关，01 为开
                bBTCallGuardianModeOn = results[0].equals( "01" );

                // 00 : announce, 01 : international, 02 : answerphone, 03 : custom
                nBTCallGuardianMode = Integer.parseInt( results[1] );

                // 0 : allow, 1 : announce, 2 : block, 3 : answerphone
                char[] ch_custom = results[2].toCharArray();
                nBTCallGuardianMode_Custom_BlockedNumber = Integer.valueOf( String.valueOf( ch_custom[0] ) );
                nBTCallGuardianMode_Custom_AllowedNumber = Integer.valueOf( String.valueOf( ch_custom[1] ) );
                nBTCallGuardianMode_Custom_International = Integer.valueOf( String.valueOf( ch_custom[2] ) );
                nBTCallGuardianMode_Custom_Withheld = Integer.valueOf( String.valueOf( ch_custom[3] ) );
                nBTCallGuardianMode_Custom_Payphones = Integer.valueOf( String.valueOf( ch_custom[4] ) );
                nBTCallGuardianMode_Custom_MobileNumbers = Integer.valueOf( String.valueOf( ch_custom[5] ) );
                nBTCallGuardianMode_Custom_Unavailable = Integer.valueOf( String.valueOf( ch_custom[6] ) );
                nBTCallGuardianMode_Custom_AllOtherNumbers = Integer.valueOf( String.valueOf( ch_custom[7] ) );
            }
            public void onFailure() {
				Toast.makeText( main_activity, "load BT Call Guardian settings failed", Toast.LENGTH_LONG ).show();
            }
        });

        /* 测试 coom
        main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                tempIntent.putExtra( "operation", "0" );       // 0 : play , 3 : announce message
                tempIntent.putExtra( "type", "3" );       // 0 : play , 3 : announce message
                main_activity.sendBroadcast( tempIntent );

                Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT recv" );
            }
            public void onFailure() {
                Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT failed" );
            }
        });
        */

        /* 测试 PIN
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_CMPC_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent cmpcIntent = new Intent( YYCommand.CALL_GUARDIAN_CMPC );
                cmpcIntent.putExtra( "data", "0000" );
                main_activity.sendBroadcast( cmpcIntent );
                Log.v( "cconn", "CALL_GUARDIAN_CMPC_RESULT send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "CALL_GUARDIAN_CMPC_RESULT recv" );
            }
            public void onFailure() {
                Log.v( "cconn", "CALL_GUARDIAN_CMPC_RESULT failed" );
            }
        });
        */
    }

    // 
    public boolean getBTCallGuardianModeOn() {
        return bBTCallGuardianModeOn;
    }

    public void setBTCallGuardianModeOn( boolean bOn ) {
        bBTCallGuardianModeOn = bOn;

        updateBTCallGuardianConfig();

        //// 开启，如果需要的话
        //if( bBTCallGuardianModeOn && isNeedContactSyncWithBase() ) {
        //    String title = "Contact sync with base?";
        //    String tips = "This could take up to ten\r\n minutes";
        //    main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.sync_base, tips, R.drawable.alert_no, R.drawable.alert_yes, new YYShowAlertDialog.onAlertDialogClickHandler() {
        //        // 这里 OK 和 CANCEL 换了，OK 使用了 NO, CANCEL 使用了 YES
        //        public void onOK() {
        //            // start contact sync with base
        //            startContactSyncWithBase( new concactSyncSuccessfully() {
        //                public void onSuccessfully() {
        //                    String title_1 = "Synchronisation complete!";
        //                    String tips_1 = "Press OK to finish";
        //                    main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title_1, R.drawable.successfully, tips_1, 0, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
        //                        public void onOK() { }
        //                        public void onCancel() {
        //                            // exit application ? ? ? ?
        //                        }
        //                    });
        //                }
        //            });

        //            // show alert dialog
        //            showContactSyncWithBaseAttentionDialog();
        //        }
        //        public void onCancel() { showContactSynchronisingDialog(); }
        //    });
        //}
    }

    public boolean isNeedContactSyncWithBase() {
        return true;
    }

    public void showContactSynchronisingDialog() {
        String title_1 = "Contacts synchronising";
        String tips_1 = "Busy\r\n Synchronisation in progress";
        main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title_1, R.drawable.sync_base, tips_1, 0, R.drawable.alert_cancel, new YYShowAlertDialog.onAlertDialogClickHandler() {
            public void onOK() { }
            public void onCancel() { showContactSyncWithBaseAttentionDialog(); }
        });
    }

    public void showContactSyncWithBaseAttentionDialog() {
        main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
            public void onInit( AlertDialog ad, View view ) {
                TextView tv_tips = (TextView)view.findViewById( R.id.attention_text );
                tv_tips.setText( "If you cancel this synchroniation\r\nnow,your contacts will need to\r\nannounce their names when they call.\r\nAre you sure?" );

                ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_back ) );

                ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
            }
            public void onOK() { showContactSynchronisingDialog(); }
            public void onCancel() { stopContactSyncWithBase(); }
        });
    }

    public void startContactSyncWithBase( concactSyncSuccessfully sync ) {
    }

    public void stopContactSyncWithBase() {
    }
    // End sync contacts

    public Integer getBTCallGuardianMode() {
        return nBTCallGuardianMode;
    }

    public void setBTCallGuardianMode( Integer nMode ) {
        nBTCallGuardianMode = nMode;

        updateBTCallGuardianConfig();
    }

    public boolean getIsUseDefaultMessage() {
        return bIsUseDefaultMessage;
    }

    public void setIsUseDefaultMessage( boolean bDefault ) {
        bIsUseDefaultMessage = bDefault;
    }

    public Integer getBTCallGuardianMode_Custom_BlockedNumber() {
        return nBTCallGuardianMode_Custom_BlockedNumber;
    }

    public void setBTCallGuardianMode_Custom_BlockedNumber( Integer nCustomBlockedNumber ) {
        nBTCallGuardianMode_Custom_BlockedNumber = nCustomBlockedNumber;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_AllowedNumber() {
        return nBTCallGuardianMode_Custom_AllowedNumber;
    }

    public void setBTCallGuardianMode_Custom_AllowedNumber( Integer nCustomAllowedNumber ) {
        nBTCallGuardianMode_Custom_AllowedNumber = nCustomAllowedNumber;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_International() {
        return nBTCallGuardianMode_Custom_International;
    }

    public void setBTCallGuardianMode_Custom_International( Integer nInternational ) {
        nBTCallGuardianMode_Custom_International = nInternational;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_Withheld() {
        return nBTCallGuardianMode_Custom_Withheld;
    }

    public void setBTCallGuardianMode_Custom_Withheld( Integer nWithheld ) {
        nBTCallGuardianMode_Custom_Withheld = nWithheld;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_Payphones() {
        return nBTCallGuardianMode_Custom_Payphones;
    }

    public void setBTCallGuardianMode_Custom_Payphones( Integer nPayphones ) {
        nBTCallGuardianMode_Custom_Payphones = nPayphones;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_MobileNumbers() {
        return nBTCallGuardianMode_Custom_MobileNumbers;
    }

    public void setBTCallGuardianMode_Custom_MobileNumbers( Integer nMobileNumbers ) {
        nBTCallGuardianMode_Custom_MobileNumbers = nMobileNumbers;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_Unavailable() {
        return nBTCallGuardianMode_Custom_Unavailable;
    }

    public void setBTCallGuardianMode_Custom_Unavailable( Integer nUnavailable ) {
        nBTCallGuardianMode_Custom_Unavailable = nUnavailable;

        updateBTCallGuardianConfig();
    }

    public Integer getBTCallGuardianMode_Custom_AllOtherNumbers() {
        return nBTCallGuardianMode_Custom_AllOtherNumbers;
    }

    public void setBTCallGuardianMode_Custom_AllOtherNumbers( Integer nAllOtherNumbers ) {
        nBTCallGuardianMode_Custom_AllOtherNumbers = nAllOtherNumbers;

        updateBTCallGuardianConfig();
    }

    public void updateBTCallGuardianConfig() {
        // 更新 BTＣall Guardian 设置
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_SCCS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_SCCS );
                banbIntent.putExtra( "switch", bBTCallGuardianModeOn ? "1" : "0" );
                banbIntent.putExtra( "guardian_mode", String.format( "%d", nBTCallGuardianMode ) );
                banbIntent.putExtra( "num_type", String.format( "%d%d%d%d%d%d%d%d", nBTCallGuardianMode_Custom_BlockedNumber, nBTCallGuardianMode_Custom_AllowedNumber, nBTCallGuardianMode_Custom_International, nBTCallGuardianMode_Custom_Withheld, nBTCallGuardianMode_Custom_Payphones, nBTCallGuardianMode_Custom_MobileNumbers, nBTCallGuardianMode_Custom_Unavailable, nBTCallGuardianMode_Custom_AllOtherNumbers ) );

                main_activity.sendBroadcast( banbIntent );
            }
            public void onRecv( String data ) {
            }
            public void onFailure() {
				Toast.makeText( main_activity, "update BT Call Guardian settings failed", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    // 
    public interface onAddNumberSuccefully {
        void onSuccessfully();
        void onFailure();
    }

    public void addBlockNumber( int nNumberFrom, String number, onAddNumberSuccefully onAddNumberEvent ) {
        updateBlockAllowNumber( nNumberFrom, 0, number, onAddNumberEvent );
    }

    public void addAllowNumber( int nNumberFrom, String number, onAddNumberSuccefully onAddNumberEvent ) {
        updateBlockAllowNumber( nNumberFrom, 1, number, onAddNumberEvent );
    }

    // nNumberFrom : 0 from Contacts, 1 from call list, 2 from other
    // bNumberType : 0 for block, 1 for allow
    public void updateBlockAllowNumber( final int nNumberFrom, final int nNumberType, final String number, final onAddNumberSuccefully onAddNumberEvent ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_BANB_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_BANB );
                banbIntent.putExtra( "type", String.format( "%d", nNumberFrom ) );
                banbIntent.putExtra( "block_or_allow", String.format( "%d", nNumberType ) );
                banbIntent.putExtra( "num", number );
                main_activity.sendBroadcast( banbIntent );
            }
            public void onRecv( String data ) {
                onAddNumberEvent.onSuccessfully();
            }
            public void onFailure() {
                onAddNumberEvent.onFailure();
            }
        });
    }

    public interface onCallsListListener {
        void onSuccessfully();
        void onFailure();
    }

    public List<callsListItem> cur_calls_list = new ArrayList<callsListItem>();
    public void getCallsList( final onCallsListListener calls_list_listener ) {
        main_activity.yy_command.executeCallListCommand( YYCommand.CALL_LIST_GTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent calllistIntent = new Intent( YYCommand.CALL_LIST_GTCL );
                calllistIntent.putExtra( "data", "0" );
                main_activity.sendBroadcast( calllistIntent );
                Log.v( "cconn", "get calls list : send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "get calls list : recv" );
                String[] results = data.split( "," );
                Log.v( "prot", "getCallsList --------------------------------------------- data : " + data );

                cur_calls_list.clear();

                Log.v( "cconn", "get calls list length : " + results.length );
                int count = results.length / 4;
                Log.v( "cconn", "get calls list count : " + count );
                for( int i=0; i < count; ++i ) {
                    Log.v( "cconn", "i : " + i + " ============================================ " );
                    if( results[i*4+0].equals( "" ) ) {
                        continue;
                    }

                    //final int msg_type = Integer.valueOf( results[i*4+0], 16 );
                    final int msg_state = 1;
                    final String msg_name = results[i*4+1];
                    final String msg_number = results[i*4+2];
                    final String msg_datetime = results[i*4+3];
                    Log.v( "cconn", "msg_name : " + msg_name );
                    Log.v( "cconn", "msg_number : " + msg_number );
                    Log.v( "cconn", "msg_datetime : " + msg_datetime );

                    //final String year = msg_datetime.substring( 0, 4 );
                    //final String month = msg_datetime.substring( 4, 6 );
                    //final String day = msg_datetime.substring( 6, 8 );
                    //final String hour = msg_datetime.substring( 8, 10 );
                    //final String min = msg_datetime.substring( 10 );
                    final String year = "2015";
                    final String month = "11";
                    final String day = "1";
                    final String hour = "2";
                    final String min = "53";

                    cur_calls_list.add( new callsListItem() {
                        public String getName() { return msg_name; }
                        public String getNumber() { return msg_number; }
                        public String getCallTime() { return String.format( "%s/%s/%s %s:%s", month, day, year, hour, min ); }
                        //public int getState() { return msg_type; }
                        public int getState() { return msg_state; }
                    });
                }

                calls_list_listener.onSuccessfully();
            }
            public void onFailure() {
                calls_list_listener.onFailure();
                Log.v( "cconn", "get calls list : failed" );
            }
        });
    }

    public List<contactsListItem> getContactsList() {
        List<contactsListItem> ret_contacts_list = new ArrayList<contactsListItem>();

        Cursor cursor = null;
        try {
            cursor = main_activity.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null );
            while( cursor.moveToNext() ) {
                final String displayName = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ) );
                final String number = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER ) );
                ret_contacts_list.add( new contactsListItem() {
                    public String getName() { return displayName; }
                    public String getNumber() { return number; }
                });
            }
        } catch ( Exception e ) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if( cursor != null ){
                cursor.close();
            }
        }

        return ret_contacts_list;
    }

    public boolean areaCodesIsFull() {
        return false;
    }

    public interface onMedaListener {
        void onSuccessfully();
        void onFailure();
    }

    public void onMedaProcess( final int nType, final String newNum, final String oldNum, final onMedaListener medaListener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_MDEA_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_MDEA );
                banbIntent.putExtra( "type", String.format( "%d", nType ) );
                if( newNum != null ) { banbIntent.putExtra( "newnum", String.format( "%s", newNum ) ); }
                if( oldNum != null ) { banbIntent.putExtra( "oldnum", String.format( "%s", oldNum ) ); }
                main_activity.sendBroadcast( banbIntent );
            }
            public void onRecv( String data ) {
                medaListener.onSuccessfully();
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
            public void onFailure() {
                medaListener.onFailure();
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
            }
        });
    }

    public void removeAllAreaCodes() {
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public Integer getCallsAndNotificationsArriveMode() {
        return nDoNotDisturb_CallsAndNotificationsArrive_Mode;
    }

    public void setCallsAndNotificationsArriveMode( Integer nMode ) {
        nDoNotDisturb_CallsAndNotificationsArrive_Mode = nMode;
    }

    public Integer getDoNotDisturbPriority() {
        return nDoNotDisturb_Priority;
    }

    public void setDoNotDisturbPriority( Integer nPriority ) {
        nDoNotDisturb_Priority = nPriority;
    }

    public boolean getDoNotDisturbEventAndReminders() {
        return bDoNotDisturb_EventAndReminders;
    }

    public void setDoNotDisturbEventAndReminders( boolean bFlag ) {
        bDoNotDisturb_EventAndReminders = bFlag;
    }

    public boolean getDoNotDisturbCalls() { 
        return bDoNotDisturb_Calls;
    }

    public void setDoNotDisturbCalls( boolean bFlag ) {
        bDoNotDisturb_Calls = bFlag;
    }

    public boolean getDoNotDisturbMessages() {
        return bDoNotDisturb_Messages;
    }

    public void setDoNotDisturbMessages( boolean bFlag ) {
        bDoNotDisturb_Messages = bFlag;
    }

    public Integer getDoNotDisturbCallsMessagesFrom() {
        return nDoNotDisturb_Calls_Messages_From;
    }

    public void setDoNotDisturbCallsMessagesFrom( Integer nFrom ) {
        nDoNotDisturb_Calls_Messages_From = nFrom;
    }

    public boolean getDoNotDisturbMonday() {
        return bDoNotDisturb_Monday;
    }

    public void setDoNotDisturbMonday( boolean bFlag ) {
        bDoNotDisturb_Monday = bFlag;
    }

    public boolean getDoNotDisturbTuesday() {
        return bDoNotDisturb_Tuesday;
    }

    public void setDoNotDisturbTuesday( boolean bFlag ) {
        bDoNotDisturb_Tuesday = bFlag;
    }

    public boolean getDoNotDisturbWednesday() {
        return bDoNotDisturb_Wednesday;
    }

    public void setDoNotDisturbWednesday( boolean bFlag ) {
        bDoNotDisturb_Wednesday = bFlag;
    }

    public boolean getDoNotDisturbThursday() {
        return bDoNotDisturb_Thursday;
    }

    public void setDoNotDisturbThursday( boolean bFlag ) {
        bDoNotDisturb_Thursday = bFlag;
    }

    public boolean getDoNotDisturbFriday() {
        return bDoNotDisturb_Friday;
    }

    public void setDoNotDisturbFriday( boolean bFlag ) {
        bDoNotDisturb_Friday = bFlag;
    }

    public boolean getDoNotDisturbSaturday() {
        return bDoNotDisturb_Saturday;
    }

    public void setDoNotDisturbSaturday( boolean bFlag ) {
        bDoNotDisturb_Saturday = bFlag;
    }

    public boolean getDoNotDisturbSunday() {
        return bDoNotDisturb_Sunday;
    }

    public void setDoNotDisturbSunday( boolean bFlag ) {
        bDoNotDisturb_Sunday = bFlag;
    }

    public Integer getDoNotDisturbStartTimeHour() {
        return nDoNotDisturb_StartTimeHour;
    }

    public Integer getDoNotDisturbStartTimeMin() {
        return nDoNotDisturb_StartTimeMin;
    }

    public void setDoNotDisturbStartTime( Integer nHour, Integer nMin ) {
        nDoNotDisturb_StartTimeHour = nHour;
        nDoNotDisturb_StartTimeMin = nMin;
    }

    public Integer getDoNotDisturbEndTimeHour() {
        return nDoNotDisturb_EndTimeHour;
    }

    public Integer getDoNotDisturbEndTimeMin() {
        return nDoNotDisturb_EndTimeMin;
    }

    public void setDoNotDisturbEndTime( Integer nHour, Integer nMin ) {
        nDoNotDisturb_EndTimeHour = nHour;
        nDoNotDisturb_EndTimeMin = nMin;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PIN
    public boolean IsFirstTimeUse() {
        return bIsFirstTimeUse;
    }

    public void setIsFirstTimeUse( boolean bIsFirst ) {
        bIsFirstTimeUse = bIsFirst;

        main_activity.saveSharedPreferences();
    }

    public String getPINNumber() {
        return strPINNumber;
    }

    public void setPINNumber( String pin_number ) {
        strPINNumber = pin_number;

        main_activity.saveSharedPreferences();
    }

    public Integer getMobileCallsMode() {
        return nMobileCallsMode;
    }

    public void setMobileCallsMode( Integer nMode ) {
        nMobileCallsMode = nMode;

        main_activity.saveSharedPreferences();
    }

    public Integer getInternationalCallsMode() {
        return nInternationalCallsMode;
    }

    public void setInternationalCallsMode( Integer nMode ) {
        nInternationalCallsMode = nMode;

        main_activity.saveSharedPreferences();
    }

    public Integer getPreminumRateCallsMode() {
        return nPreminumRateCallsMode;
    }

    public void setPreminumRateCallsMode( Integer nMode ) {
        nPreminumRateCallsMode = nMode;

        main_activity.saveSharedPreferences();
    }

    public Integer getAllDialledCallsMode() {
        return nAllDialledCallsMode;
    }

    public void setAllDialledCallsMode( Integer nMode ) {
        nAllDialledCallsMode = nMode;

        main_activity.saveSharedPreferences();
    }

}

