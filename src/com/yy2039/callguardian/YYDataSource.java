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

import java.util.Timer;
import java.util.TimerTask;

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
    //private String strPINNumber;

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
        List<String> getNumber();
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
        bIsFirstTimeUse = false;
        //strPINNumber = "";

        // 请求 BTＣall Guardian 设置
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GCCS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_GCCS ) );
            }
            public void onRecv( String data ) {
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GCCS_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    String[] results = data.split( "," );
                    if( results.length < 3 ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GCCS_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    }
                    else {
                        try {
                            // 00 为关，01 为开
                            bBTCallGuardianModeOn = results[0].equals( "01" );

                            // 00 : announce, 01 : international, 02 : answerphone, 03 : custom
                            nBTCallGuardianMode = Integer.parseInt( results[1] );

                            // 0 : allow, 1 : announce, 2 : block, 3 : answerphone
                            char[] ch_custom = results[2].toCharArray();
                            if( ch_custom.length < 8 ) {
                                String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GCCS_RESULT, data );
                                //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                            }
                            else {
                                nBTCallGuardianMode_Custom_BlockedNumber = Integer.valueOf( String.valueOf( ch_custom[0] ) );
                                nBTCallGuardianMode_Custom_AllowedNumber = Integer.valueOf( String.valueOf( ch_custom[1] ) );
                                nBTCallGuardianMode_Custom_International = Integer.valueOf( String.valueOf( ch_custom[2] ) );
                                nBTCallGuardianMode_Custom_Withheld = Integer.valueOf( String.valueOf( ch_custom[3] ) );
                                nBTCallGuardianMode_Custom_Payphones = Integer.valueOf( String.valueOf( ch_custom[4] ) );
                                nBTCallGuardianMode_Custom_MobileNumbers = Integer.valueOf( String.valueOf( ch_custom[5] ) );
                                nBTCallGuardianMode_Custom_Unavailable = Integer.valueOf( String.valueOf( ch_custom[6] ) );
                                nBTCallGuardianMode_Custom_AllOtherNumbers = Integer.valueOf( String.valueOf( ch_custom[7] ) );
                            }
                        } catch ( Exception e ) {
                            String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GCCS_RESULT, data );
                            //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                        }
                    }
                }
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "load BT Call Guardian settings failed", Toast.LENGTH_LONG ).show();
            }
        });

        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_GDMS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent dmIntent = new Intent( YYCommand.ANSWER_MACHINE_GDMS );
                dmIntent.putExtra( "type", "3" );
                main_activity.sendBroadcast( dmIntent );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "gdms recv : " + data );
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.ANSWER_MACHINE_GDMS_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    String[] results = data.split( "," );
                    if( results.length < 2 ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.ANSWER_MACHINE_GDMS_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    } else {
                        bIsUseDefaultMessage = ( Integer.parseInt( results[0] ) == 0 ? true : false );
                    }
                }
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "load gdms failed", Toast.LENGTH_LONG ).show();
            }
        });
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
        String title_1 = "Adding contacts to allowed list";
        String tips_1 = "Busy\r\n Synchronisation in progress";
        main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title_1, R.drawable.sync_base, tips_1, 0, R.drawable.alert_cancel, new YYShowAlertDialog.onAlertDialogClickHandler() {
            public boolean getIsCancelEnable() { return true; }
            public int getKeybackIsCancel() { return 2; }
            public void onOK() { }
            public void onCancel() { showContactSyncWithBaseAttentionDialog(); }
            public void onKeyback() {}
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
            public boolean getIsCancelEnable() { return true; }
            public int getKeybackIsCancel() { return 1; }
            public void onOK() { showContactSynchronisingDialog(); }
            public void onCancel() { stopContactSyncWithBase(); }
            public void onKeyback() {}
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

    public void initIsUseDefaultMessage( boolean bDefault ) {
        bIsUseDefaultMessage = bDefault;
    }

    public void setIsUseDefaultMessage( boolean bDefault ) {
        bIsUseDefaultMessage = bDefault;

        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.ANSWER_MACHINE_SDMS_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent dmIntent = new Intent( YYCommand.ANSWER_MACHINE_SDMS );
                dmIntent.putExtra( "status", bIsUseDefaultMessage ? "0" : "1" );
                dmIntent.putExtra( "type", "3" );
                main_activity.sendBroadcast( dmIntent );
            }
            public void onRecv( String data ) {
                if( data != null && data.equals( "SUCCESS" ) ) {
                    // 成功
                }
                else {
                    // 失败
                    //Toast.makeText( main_activity, "operation failed", Toast.LENGTH_LONG ).show();
                }
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "operation failed", Toast.LENGTH_LONG ).show();
            }
        });
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
                Log.v( "cconn", "CALL_GUARDIAN_SCCS_RESULT : " + data );
            }
            public void onFailure() {
				//Toast.makeText( main_activity, "update BT Call Guardian settings failed", Toast.LENGTH_SHORT ).show();
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

    private String valid_chars = new String( "0123456789PR*#" );
    private boolean isValidNumber( char ch ) {
        for( int i=0; i < valid_chars.length(); ++i ) {
            if( ch == valid_chars.charAt( i ) ) {
                return true;
            }
        }
        return false;
    }

    // nNumberFrom : 0 from Contacts, 1 from call list, 2 from other
    // bNumberType : 0 for block, 1 for allow
    public void updateBlockAllowNumber( final int nNumberFrom, final int nNumberType, String number, final onAddNumberSuccefully onAddNumberEvent ) {
        String new_number = "";
        for( int i=0; i < number.length(); ++i ) {
            if( isValidNumber( number.charAt( i ) ) ) {
                new_number += number.charAt( i );
            }
        }

        final String send_number = new_number;
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_BANB_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_BANB );
                banbIntent.putExtra( "type", String.format( "%d", nNumberFrom ) );
                banbIntent.putExtra( "block_or_allow", String.format( "%d", nNumberType ) );
                banbIntent.putExtra( "num", send_number );
                main_activity.sendBroadcast( banbIntent );
                Log.v( "cconn", String.format( "from : %d, type : %d, number : %s", nNumberFrom, nNumberType, send_number ) );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "CALL_GUARDIAN_BANB_RESULT : " + data );
                if( data != null && data.equals( "SUCCESS" ) ) {
                    onAddNumberEvent.onSuccessfully();
                }
                else {
                    onAddNumberEvent.onFailure();
                }
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
                main_activity.yy_schedule.scheduleOnceTime( 2000, new YYSchedule.onScheduleAction() {
                    public void doSomething() {
                        Intent calllistIntent = new Intent( YYCommand.CALL_LIST_GTCL );
                        calllistIntent.putExtra( "data", "0" );
                        main_activity.sendBroadcast( calllistIntent );
                    }
                });

                Log.v( "cconn", "get calls list : send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "get calls list recv : " + data );
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GCCS_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    String[] results = data.split( "," );

                    cur_calls_list.clear();

                    try {
                        int count = results.length / 4;
                        for( int i=0; i < count; ++i ) {
                            if( results[i*4+1].equals( "" ) && results[i*4+2].equals( "" ) ) {
                                continue;
                            }

                            //final int msg_type = Integer.valueOf( results[i*4+0], 16 );
                            // "00","02"
                            Log.v( "cconn", "results0 : " + results[i*4+0] );
                            int temp_state = 1;
                            char[] ch_custom = results[i*4+0].toCharArray();
                            if( ch_custom[0] == '0' ) { temp_state = 3; }
                            if( ch_custom[0] == '1' ) { temp_state = 1; }
                            if( ch_custom[0] == '2' ) { temp_state = 2; }
                            if( ch_custom[0] == '3' ) { temp_state = 4; }

                            final int msg_state = temp_state;
                            Log.v( "cconn", "results1 : " + results[i*4+1] );
                            final String msg_name = results[i*4+1];
                            Log.v( "cconn", "results2 : " + results[i*4+2] );
                            final String msg_number = results[i*4+2];
                            Log.v( "cconn", "results3 : " + results[i*4+3] );
                            final String msg_datetime = results[i*4+3];

                            final String year = msg_datetime.substring( 0, 4 );
                            Log.v( "cconn", "year : " + year );
                            final String month = msg_datetime.substring( 4, 6 );
                            Log.v( "cconn", "month : " + month );
                            final String day = msg_datetime.substring( 6, 8 );
                            Log.v( "cconn", "day : " + day );
                            final String hour = msg_datetime.substring( 8, 10 );
                            Log.v( "cconn", "hour : " + hour );
                            final String min = msg_datetime.substring( 10 );
                            Log.v( "cconn", "min : " + min );

                            cur_calls_list.add( new callsListItem() {
                                public String getName() { return msg_name; }
                                public String getNumber() { return msg_number; }
                                public String getCallTime() { return String.format( "%s/%s/%s %s:%s", month, day, year, hour, min ); }
                                public int getState() { return msg_state; }
                            });
                        }

                        calls_list_listener.onSuccessfully();
                    } catch ( Exception e ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_GCCS_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                        Log.v( "cconn", text );
                    }
                }
            }
            public void onFailure() {
                calls_list_listener.onFailure();
                Log.v( "cconn", "get calls list : failed" );
            }
        });
    }

    public List<contactsListItem> getContactsList() {
        Map<String,List<String>> name_number_list = new HashMap<String,List<String>>();

        List<String> sortList = new ArrayList<String>();

        Cursor cursor = null;
        try {
            cursor = main_activity.getContentResolver().query( ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null );
            while( cursor.moveToNext() ) {
                final String displayName = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME ) );
                final String number = cursor.getString( cursor.getColumnIndex( ContactsContract.CommonDataKinds.Phone.NUMBER ) );

                List<String> number_list = name_number_list.get( displayName );
                if( number_list == null ) {
                    name_number_list.put( displayName, new ArrayList<String>() );
                    number_list = name_number_list.get( displayName );

                    sortList.add( displayName );
                }
                number_list.add( number );
            }
        } catch ( Exception e ) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if( cursor != null ){
                cursor.close();
            }
        }

        List<contactsListItem> ret_contacts_list = new ArrayList<contactsListItem>();

        for( int i=0; i < sortList.size(); ++i ) {
            final String displayName = sortList.get( i );
            final List<String> numbers = name_number_list.get( displayName );

            ret_contacts_list.add( new contactsListItem() {
                public String getName() { return displayName; }
                public List<String> getNumber() { return numbers; }
            });
        }

        //for( Map.Entry<String,List<String>> item_entry : name_number_list.entrySet() ) {
        //    final String displayName = item_entry.getKey();
        //    final List<String> numbers = item_entry.getValue();

        //    ret_contacts_list.add( new contactsListItem() {
        //        public String getName() { return displayName; }
        //        public List<String> getNumber() { return numbers; }
        //    });
        //}

        return ret_contacts_list;
    }

    // 
    public interface onAreaCodesIsFullListener {
        void onAreaCodeFullCallback( boolean bIsFull );
    }

    public void areaCodesIsFull( final onAreaCodesIsFullListener area_code_listener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_MDEA_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_MDEA ) );
                Log.v( "cconn", "areaCodesIsFull : send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "areaCodesIsFull recv : " + data );
                if( data != null && data.equals( "00" ) ) {
                    boolean bIsFull = false;
                    area_code_listener.onAreaCodeFullCallback( false );
                }
                else {
                    //Toast.makeText( main_activity, "request area code is full failed", Toast.LENGTH_SHORT ).show();
                }
            }
            public void onFailure() {
                Log.v( "cconn", "areaCodesIsFull : failed" );
                //Toast.makeText( main_activity, "request area code is full failed", Toast.LENGTH_SHORT ).show();
            }
        });
    }

    public interface onMedaListener {
        void onSuccessfully();
        void onFailure( int err_code );
    }

    public void onMedaProcess( final int nType, final String newNum, final String oldNum, final onMedaListener medaListener ) {
        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_MDEA_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Log.v( "cconn", "onMedaProcess type : " + nType );
                Log.v( "cconn", "onMedaProcess new : " + newNum );
                Log.v( "cconn", "onMedaProcess old : " + oldNum );
                Intent banbIntent = new Intent( YYCommand.CALL_GUARDIAN_MDEA );
                banbIntent.putExtra( "type", String.format( "%d", nType ) );
                if( newNum != null ) { banbIntent.putExtra( "newnum", String.format( "%s", newNum ) ); }
                if( oldNum != null ) { banbIntent.putExtra( "oldnum", String.format( "%s", oldNum ) ); }
                main_activity.sendBroadcast( banbIntent );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "CALL_GUARDIAN_MDEA_RESULT : " + data );
                if( data != null ) {
                    try {
                        int code = Integer.parseInt( data );
                        if( code == 0 ) {
                            medaListener.onSuccessfully();
                        } else {
                            medaListener.onFailure( code );
                        }
                    } catch ( Exception e ) {
                        String text = String.format( "%s recv data error : %s", YYCommand.CALL_GUARDIAN_MDEA_RESULT, data );
                        //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                    }
                } else {
                    medaListener.onFailure( 1 );
                }
            }
            public void onFailure() {
                medaListener.onFailure( 1 );
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

    //public String getPINNumber() {
    //    return strPINNumber;
    //}

    //public void setPINNumber( String pin_number ) {
    //    strPINNumber = pin_number;

    //    main_activity.saveSharedPreferences();
    //}

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

    public interface syncLisenter {
        void onSuccessfully();
        void onFailure();
    }

    private int total_count = 0;
    private int counter = 0;
    private boolean bSuccess = false;
    private boolean bFail = false;
    public void syncContactToBase( final syncLisenter sync_lisenter ) {
        List<YYDataSource.contactsListItem> contacts_list = main_activity.yy_data_source.getContactsList();

        total_count = 0;
        for( int i=0; i < contacts_list.size(); ++i ) {
            final YYDataSource.contactsListItem item_info = contacts_list.get( i );
            List<String> number_list = item_info.getNumber();
            for( int j=0; j < number_list.size(); ++j ) {
                total_count = total_count + 1;
            }
        }

        // 没有记录的话，就直接返回
        if( total_count <= 0 ) {
            sync_lisenter.onSuccessfully();

            return;
        }

        counter = 0;
        bSuccess = false;
        bFail = false;

        for( int i=0; i < contacts_list.size(); ++i ) {
            final YYDataSource.contactsListItem item_info = contacts_list.get( i );
            List<String> number_list = item_info.getNumber();
            for( int j=0; j < number_list.size(); ++j ) {
                main_activity.yy_data_source.addAllowNumber( 2, number_list.get( j ), new YYDataSource.onAddNumberSuccefully() {
                    public void onSuccessfully() {
                        counter = counter + 1;
                        bSuccess = true;

                        if( counter >= total_count ) {
                            if( bFail ) {
                                sync_lisenter.onFailure();
                            } else {
                                sync_lisenter.onSuccessfully();
                            }
                        }
                    }
                    public void onFailure() {
                        counter = counter + 1;
                        bFail = true;

                        if( counter >= total_count ) {
                            sync_lisenter.onFailure();
                        }
                    }
                });
            }
        }
    }

    public void syncContactSuccessfully() {
        String title = "Contact sync with base?";
        String tips = "synchronisation contacts to base ok";
        main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.synchronising, tips, 0, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
            public boolean getIsCancelEnable() { return true; }
            public int getKeybackIsCancel() { return 0; }
            public void onOK() { }
            public void onCancel() { }
            public void onKeyback() {}
        });
    }

    public void syncContactFailure() {
        String title = "Contact sync with base?";
        String tips = "synchronisation contacts to base failed";
        main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.synchronising, tips, 0, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
            public boolean getIsCancelEnable() { return true; }
            public int getKeybackIsCancel() { return 0; }
            public void onOK() { }
            public void onCancel() { }
            public void onKeyback() {}
        });
    }

}

