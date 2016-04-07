package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.view.View;
import android.widget.Switch;
import android.widget.CompoundButton;
import android.widget.Button;
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.widget.Toast;
import android.support.v4.app.FragmentActivity;

import com.fourmob.datetimepicker.date.DatePickerDialog;
import com.fourmob.datetimepicker.date.DatePickerDialog.OnDateSetListener;
import com.sleepbot.datetimepicker.time.RadialPickerLayout;
import com.sleepbot.datetimepicker.time.TimePickerDialog;

import java.util.Calendar;

public class DoNotDisturbView extends YYViewBack implements TimePickerDialog.OnTimeSetListener {
    public static final String TIMEPICKER_TAG = "timepicker";
    public TimePickerDialog timePickerDialog;
    private boolean is_pick_start_time;
    public DoNotDisturbView() {
        view_layout_res_id = R.layout.do_not_disturb;
        is_pick_start_time = true;

        Calendar calendar = Calendar.getInstance();
        timePickerDialog = TimePickerDialog.newInstance( this, calendar.get( Calendar.HOUR_OF_DAY ) ,calendar.get( Calendar.MINUTE ), false, false );

        TimePickerDialog tpd = (TimePickerDialog)main_activity.getSupportFragmentManager().findFragmentByTag( TIMEPICKER_TAG );
        if( tpd != null )
            tpd.setOnTimeSetListener( this );
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        // 
        updateCallsAndNotificatiosArriveButton();
        Button btn_calls_and_notifications_arrive = (Button)main_activity.findViewById( R.id.btn_calls_notifications_arrive );
        btn_calls_and_notifications_arrive.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                main_activity.yy_show_alert_dialog.showMenuAlertDialog( R.layout.alert_calls_and_notifications_arrive, 0, -280, new YYShowAlertDialog.onAlertDialogHandler() {
                    public void onInit( AlertDialog ad, View view ) {
                        final AlertDialog ad_1 = ad;

                        Button btn_always = (Button)view.findViewById( R.id.btn_1 );
                        btn_always.setText( "Always interrupt" );
                        btn_always.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setCallsAndNotificationsArriveMode( YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALWAYS_INTERRUPT );

                                updateCallsAndNotificatiosArriveButton();
                            }
                        });

                        Button btn_allow = (Button)view.findViewById( R.id.btn_2 );
                        btn_allow.setText( "Allow only priority interruptions" );
                        btn_allow.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setCallsAndNotificationsArriveMode( YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALLOW_ONLY_PRIORITY_INTERRUPTIONS );

                                updateCallsAndNotificatiosArriveButton();
                            }
                        });

                        Button btn_dont = (Button)view.findViewById( R.id.btn_3 );
                        btn_dont.setText( "Don't interrupt" );
                        btn_dont.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setCallsAndNotificationsArriveMode( YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_DONT_INTERRUPT );

                                updateCallsAndNotificatiosArriveButton();
                            }
                        });
                    }
                    public boolean getIsCancelEnable() { return false; }
                    public int getKeybackIsCancel() { return 0; }
                    public void onOK() { }
                    public void onCancel() { }
                    public void onKeyback() {}
                });
            }
        });

        // 
        updatePriorltyInterruptionsButton();
        Button btn_priorlty_interruptions = (Button)main_activity.findViewById( R.id.btn_priorlty_interruptions );
        btn_priorlty_interruptions.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "Indefinitely"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_INDEFINITELY ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_INDEFINITELY; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 8 hours"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_8_HOURS ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_8_HOURS; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 4 hours"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_4_HOURS ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_4_HOURS; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 3 hours"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_3_HOURS ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_3_HOURS; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 2 hours"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_2_HOURS ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_2_HOURS; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 1 hours"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_1_HOURS ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_1_HOURS; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 45 minutes"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_45_MIN ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_45_MIN; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 30 minutes"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_30_MIN ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_30_MIN; }
                });
                item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                    public String getRadioText() { return "For 15 minutes"; }
                    public void onRadioClick() { yy_view_self.yy_temp_data.put( "do_not_disturb_priority", YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_15_MIN ); }
                    public boolean isRadioChecked() { return main_activity.yy_data_source.getDoNotDisturbPriority() == YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_15_MIN; }
                });

                main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Only allow priority interruptions", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                    public boolean getIsCancelEnable() { return false; }
                    public int getKeybackIsCancel() { return 0; }
                    public void onOK() {
                        Integer priority = (Integer)yy_view_self.yy_temp_data.get( "do_not_disturb_priority" );
                        if( priority != null ) {
                            main_activity.yy_data_source.setDoNotDisturbPriority( priority );

                            updatePriorltyInterruptionsButton();
                        }
                    }
                    public void onCancel() { }
                    public void onKeyback() {}
                });
            }
        });

        // 
        Switch btn_event_and_reminders = (Switch)main_activity.findViewById( R.id.btn_event_and_reminders_state );
        btn_event_and_reminders.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                main_activity.yy_data_source.setDoNotDisturbEventAndReminders( isChecked );
            }
        });
        btn_event_and_reminders.setChecked( main_activity.yy_data_source.getDoNotDisturbEventAndReminders() );

        // 
        Switch btn_calls = (Switch)main_activity.findViewById( R.id.btn_calls_state );
        btn_calls.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                main_activity.yy_data_source.setDoNotDisturbCalls( isChecked );
            }
        });
        btn_calls.setChecked( main_activity.yy_data_source.getDoNotDisturbCalls() );

        // 
        Switch btn_messages = (Switch)main_activity.findViewById( R.id.btn_messages_state );
        btn_messages.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                main_activity.yy_data_source.setDoNotDisturbMessages( isChecked );
            }
        });
        btn_messages.setChecked( main_activity.yy_data_source.getDoNotDisturbMessages() );

        //
        updateCallsMessagesFromButton();
        Button btn_calls_messages_from = (Button)main_activity.findViewById( R.id.btn_calls_msg_from );
        btn_calls_messages_from.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                main_activity.yy_show_alert_dialog.showMenuAlertDialog( R.layout.alert_calls_and_notifications_arrive, 0, -10, new YYShowAlertDialog.onAlertDialogHandler() {
                    public void onInit( AlertDialog ad, View view ) {
                        final AlertDialog ad_1 = ad;

                        Button btn_always = (Button)view.findViewById( R.id.btn_1 );
                        btn_always.setText( "Anyone" );
                        btn_always.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setDoNotDisturbCallsMessagesFrom( YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_ANYONE );

                                updateCallsMessagesFromButton();
                            }
                        });

                        Button btn_allow = (Button)view.findViewById( R.id.btn_2 );
                        btn_allow.setText( "Starred contacts only" );
                        btn_allow.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setDoNotDisturbCallsMessagesFrom( YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_STARRED_CONTACTS_ONLY );

                                updateCallsMessagesFromButton();
                            }
                        });

                        Button btn_dont = (Button)view.findViewById( R.id.btn_3 );
                        btn_dont.setText( "Contacts only" );
                        btn_dont.setOnClickListener( new View.OnClickListener () {
                            public void onClick( View v ){
                                ad_1.hide();

                                main_activity.yy_data_source.setDoNotDisturbCallsMessagesFrom( YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_CONTACTS_ONLY );

                                updateCallsMessagesFromButton();
                            }
                        });
                    }
                    public boolean getIsCancelEnable() { return false; }
                    public int getKeybackIsCancel() { return 0; }
                    public void onOK() { }
                    public void onCancel() { }
                    public void onKeyback() {}
                });
            }
        });

        //
        updateAlarmsButton();
        Button btn_alarms = (Button)main_activity.findViewById( R.id.btn_alerms );
        btn_alarms.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
            }
        });

        //
        updateDowntimeButton();
        Button btn_downtime = (Button)main_activity.findViewById( R.id.btn_downtime );
        btn_downtime.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
            }
        });

        //
        updateDaysButton();
        Button btn_days = (Button)main_activity.findViewById( R.id.btn_days );
        btn_days.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                main_activity.yy_show_alert_dialog.showMenuAlertDialog( R.layout.alert_check_box, 0, -50, new YYShowAlertDialog.onAlertDialogHandler() {
                    public void onInit( AlertDialog ad, View view ) {
                        final CheckBox cb_monday = (CheckBox)view.findViewById( R.id.check_box_monday );
                        cb_monday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_monday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_monday", arg1 );
                            }
                        });
                        boolean bMonday = main_activity.yy_data_source.getDoNotDisturbMonday();
                        cb_monday.setChecked( bMonday );
                        cb_monday.setButtonDrawable( bMonday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_monday", bMonday );

                        final CheckBox cb_tuesday = (CheckBox)view.findViewById( R.id.check_box_tuesday );
                        cb_tuesday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_tuesday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_tuesday", arg1 );
                            }
                        });
                        boolean bTuesday = main_activity.yy_data_source.getDoNotDisturbTuesday();
                        cb_tuesday.setChecked( bTuesday );
                        cb_tuesday.setButtonDrawable( bTuesday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_tuesday", bTuesday );

                        final CheckBox cb_wednesday = (CheckBox)view.findViewById( R.id.check_box_wednesday );
                        cb_wednesday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_wednesday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_wednesday", arg1 );
                            }
                        });
                        boolean bWednesday = main_activity.yy_data_source.getDoNotDisturbWednesday();
                        cb_wednesday.setChecked( bWednesday );
                        cb_wednesday.setButtonDrawable( bWednesday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_wednesday", bWednesday );

                        final CheckBox cb_thursday = (CheckBox)view.findViewById( R.id.check_box_thursday );
                        cb_thursday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_thursday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_thursday", arg1 );
                            }
                        });
                        boolean bThursday = main_activity.yy_data_source.getDoNotDisturbThursday();
                        cb_thursday.setChecked( bThursday );
                        cb_thursday.setButtonDrawable( bThursday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_thursday", bThursday );

                        final CheckBox cb_friday = (CheckBox)view.findViewById( R.id.check_box_friday );
                        cb_friday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_friday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_friday", arg1 );
                            }
                        });
                        boolean bFriday = main_activity.yy_data_source.getDoNotDisturbFriday();
                        cb_friday.setChecked( bFriday );
                        cb_friday.setButtonDrawable( bFriday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_friday", bFriday );

                        final CheckBox cb_saturday = (CheckBox)view.findViewById( R.id.check_box_saturday );
                        cb_saturday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_saturday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_saturday", arg1 );
                            }
                        });
                        boolean bSaturday = main_activity.yy_data_source.getDoNotDisturbSaturday();
                        cb_saturday.setChecked( bSaturday );
                        cb_saturday.setButtonDrawable( bSaturday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_saturday", bSaturday );

                        final CheckBox cb_sunday = (CheckBox)view.findViewById( R.id.check_box_sunday );
                        cb_sunday.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener () {
                            public void onCheckedChanged( CompoundButton arg0, boolean arg1 ) {
                                cb_sunday.setButtonDrawable( arg1 ? R.drawable.check_box_select : R.drawable.check_box_normal );
                                yy_view_self.yy_temp_data.put( "do_not_disturb_sunday", arg1 );
                            }
                        });
                        boolean bSunday = main_activity.yy_data_source.getDoNotDisturbSunday();
                        cb_sunday.setChecked( bSunday );
                        cb_sunday.setButtonDrawable( bSunday ? R.drawable.check_box_select : R.drawable.check_box_normal );
                        yy_view_self.yy_temp_data.put( "do_not_disturb_sunday", bSunday );
                    }
                    public boolean getIsCancelEnable() { return false; }
                    public int getKeybackIsCancel() { return 0; }
                    public void onOK() {
                        main_activity.yy_data_source.setDoNotDisturbMonday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_monday" ) );
                        main_activity.yy_data_source.setDoNotDisturbTuesday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_tuesday" ) );
                        main_activity.yy_data_source.setDoNotDisturbWednesday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_wednesday" ) );
                        main_activity.yy_data_source.setDoNotDisturbThursday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_thursday" ) );
                        main_activity.yy_data_source.setDoNotDisturbFriday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_friday" ) );
                        main_activity.yy_data_source.setDoNotDisturbSaturday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_saturday" ) );
                        main_activity.yy_data_source.setDoNotDisturbSunday( (Boolean)yy_view_self.yy_temp_data.get( "do_not_disturb_sunday" ) );

                        updateDaysButton();
                    }
                    public void onCancel() { }
                    public void onKeyback() {}
                });
            }
        });

        //
        updateStartTimeButton();
        Button btn_start_time = (Button)main_activity.findViewById( R.id.btn_start_time );
        btn_start_time.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                is_pick_start_time = true;

                timePickerDialog.setVibrate( true );
                timePickerDialog.setCloseOnSingleTapMinute( false );
                timePickerDialog.show( main_activity.getSupportFragmentManager(), TIMEPICKER_TAG );
            }
        });

        //
        updateEndTimeButton();
        Button btn_end_time = (Button)main_activity.findViewById( R.id.btn_end_time );
        btn_end_time.setOnClickListener( new View.OnClickListener() {
            public void onClick( View v ) {
                is_pick_start_time = false;

                timePickerDialog.setVibrate( true );
                timePickerDialog.setCloseOnSingleTapMinute( false );
                timePickerDialog.show( main_activity.getSupportFragmentManager(), TIMEPICKER_TAG );
            }
        });
    }

    public String getViewTitle() { return "Do Not Disturb"; }

    @Override
    public void onTimeSet( RadialPickerLayout view, int hourOfDay, int minute ) {
        if( is_pick_start_time ) {
            main_activity.yy_data_source.setDoNotDisturbStartTime( hourOfDay, minute );

            updateStartTimeButton();
        }
        else {
            main_activity.yy_data_source.setDoNotDisturbEndTime( hourOfDay, minute );

            updateEndTimeButton();
        }
    }

    public void updateCallsAndNotificatiosArriveButton() {
        Integer nCallsAndNotificationsArriveMode = main_activity.yy_data_source.getCallsAndNotificationsArriveMode();

        String temp_text = "Always inrerrupt";
        if( nCallsAndNotificationsArriveMode == YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_ALLOW_ONLY_PRIORITY_INTERRUPTIONS )
            temp_text = "Allow only priority interruptions";

        if( nCallsAndNotificationsArriveMode == YYCommon.DO_NOT_DISTURB_CALLS_AND_NOTIFICATIONS_ARRIVE_DONT_INTERRUPT )
            temp_text = "Don't interrupt";

        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_calls_notifications_arrive );
        btn_obj.setText( YYViewBase.transferText( "When calls and notifications arrive", temp_text ) );
    }

    public void updatePriorltyInterruptionsButton() {
        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_priorlty_interruptions );
        switch( main_activity.yy_data_source.getDoNotDisturbPriority() ) {
            case YYCommon.DO_NOT_DISTURB_PRIORITY_INDEFINITELY: btn_obj.setText( "Priorlty Interruptions" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_8_HOURS: btn_obj.setText( "Priorlty For 8 Hours" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_4_HOURS: btn_obj.setText( "Priorlty For 4 Hours" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_3_HOURS: btn_obj.setText( "Priorlty For 3 Hours" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_2_HOURS: btn_obj.setText( "Priorlty For 2 Hours" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_1_HOURS: btn_obj.setText( "Priorlty For 1 Hours" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_45_MIN: btn_obj.setText( "Priorlty For 45 Minutes" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_30_MIN: btn_obj.setText( "Priorlty For 30 Minutes" ); break;
            case YYCommon.DO_NOT_DISTURB_PRIORITY_FOR_15_MIN: btn_obj.setText( "Priorlty For 15 Minutes" ); break;
        }
    }

    public void updateCallsMessagesFromButton() {
        Integer nCallsMessagesFrom = main_activity.yy_data_source.getDoNotDisturbCallsMessagesFrom();

        String temp_text = "Anyone";
        if( nCallsMessagesFrom == YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_STARRED_CONTACTS_ONLY )
            temp_text = "Starred contacts only";

        if( nCallsMessagesFrom == YYCommon.DO_NOT_DISTURB_CALLS_MESSAGES_FROM_CONTACTS_ONLY )
            temp_text = "Contacts only";

        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_calls_msg_from );
        btn_obj.setText( YYViewBase.transferText( "Calls/messages from", temp_text ) );
    }

    public void updateAlarmsButton() {
        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_alerms );
        btn_obj.setText( "Alarms are always priorty interruptons" );
    }

    public void updateDowntimeButton() {
        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_downtime );
        btn_obj.setText( "Downtime(priority Interruptions only)" );
    }

    public void updateDaysButton() {
        String days_text = "";
        if( main_activity.yy_data_source.getDoNotDisturbMonday() )
            days_text = days_text + "Mon";
        if( main_activity.yy_data_source.getDoNotDisturbTuesday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Tue" : "Tue" );
        if( main_activity.yy_data_source.getDoNotDisturbWednesday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Wed" : "Wed" );
        if( main_activity.yy_data_source.getDoNotDisturbThursday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Thur" : "Thur" );
        if( main_activity.yy_data_source.getDoNotDisturbFriday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Fri" : "Fri" );
        if( main_activity.yy_data_source.getDoNotDisturbSaturday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Sat" : "Sat" );
        if( main_activity.yy_data_source.getDoNotDisturbSunday() )
            days_text = days_text + ( days_text.length() > 0 ? ".Sun" : "Sun" );

        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_days );
        btn_obj.setText( YYViewBase.transferText( "Days", days_text ) );
    }

    public void updateStartTimeButton() {
        Integer nHour = main_activity.yy_data_source.getDoNotDisturbStartTimeHour();
        Integer nMin = main_activity.yy_data_source.getDoNotDisturbStartTimeMin();

        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_start_time );
        btn_obj.setText( YYViewBase.transferText( "Start time", String.format( "%02d:%02d", nHour, nMin ) ) );
    }

    public void updateEndTimeButton() {
        Integer nHour = main_activity.yy_data_source.getDoNotDisturbEndTimeHour();
        Integer nMin = main_activity.yy_data_source.getDoNotDisturbEndTimeMin();

        Button btn_obj = (Button)main_activity.findViewById( R.id.btn_end_time );
        btn_obj.setText( YYViewBase.transferText( "End time", String.format( "%02d:%02d next day", nHour, nMin ) ) );
    }
}
