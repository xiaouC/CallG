package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.os.Build;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.graphics.Color;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.content.Context;  
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.content.SharedPreferences;
import android.os.RemoteException;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.app.AlertDialog;
import android.os.PowerManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.os.Parcelable;

public class CallGuardianActivity extends FragmentActivity
{
    public static CallGuardianActivity main_activity;
    public boolean bIsDestroy = false;

    public YYCommon yy_common;
    public YYSchedule yy_schedule;
    public YYDataSource yy_data_source;
    public YYCommand yy_command;
    public CallControlView call_control_view;
    public YYShowAlertDialog yy_show_alert_dialog;
    public YYInputNumberView yy_input_number_view;
    public YYInputNumberPINView yy_input_number_pin_view;

    public YYViewBase yy_current_view;
    private PowerManager.WakeLock wakeLock = null;
    private static boolean lockAcquired = false;

    public boolean bContactSynchronising = false;

    public boolean bShowWelcomePage = false;
    private static final int[] welcome_res_ids = { R.drawable.welcome_1, R.drawable.welcome_2, R.drawable.welcome_3, R.drawable.welcome_4 };

    private BroadcastReceiver headsetPlugReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( intent.hasExtra( "state" ) ) {
                changeShengDao( true );
            }
        }
    };

    public AlertDialog yy_playing_msg_dlg = null;
    //public AlertDialog yy_record_auto_save_dlg = null;
    public interface onAutoSaveListener {
        public void onAutoSave();
    }
    public onAutoSaveListener yy_auto_save_listener = null;
    private BroadcastReceiver playingMsgEndReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if( yy_auto_save_listener != null ) {
                yy_auto_save_listener.onAutoSave();
                yy_auto_save_listener = null;
            }
            if( yy_playing_msg_dlg != null ) {
                yy_playing_msg_dlg.hide();
                yy_playing_msg_dlg = null;
            }
            changeShengDao( true );
        }
    };

    public AlertDialog yy_record_prompt_dlg = null;
    public int yy_record_schedule_index = -1;
    public boolean bMemoryFullFlag = false;
    public boolean bRecordFlag = false;

    private AlertDialog memory_full_dlg = null;
    private BroadcastReceiver memoryFullReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            bMemoryFullFlag = true;

            if( yy_record_schedule_index != -1 ) {
                yy_schedule.cancelSchedule( yy_record_schedule_index );
                yy_record_schedule_index = -1;
            }
            if( yy_record_prompt_dlg != null ) {
                yy_record_prompt_dlg.hide();
                yy_record_prompt_dlg = null;
            }
            if( yy_playing_msg_dlg != null ) {
                yy_playing_msg_dlg.hide();
                yy_playing_msg_dlg = null;
            }

            //String title = "Voice Prompt\r\nLoudspeaker Delivery";
            String title = "";
            String tips = "Please speak after the tone.\r\nTo end recording, press Save";
            memory_full_dlg = yy_show_alert_dialog.showVoicePromptAlertDialog( title, R.drawable.play_message, tips, new YYShowAlertDialog.onAlertDialogClickHandler() {
                public boolean getIsCancelEnable() { return false; }
                public int getKeybackIsCancel() { return 0; }
                public void onOK() { }
                public void onCancel() { }
                public void onKeyback() {}
            });
            changeShengDaoRecordStart( memory_full_dlg );

            yy_schedule.scheduleOnceTime( 5000, new YYSchedule.onScheduleAction() {
                public void doSomething() {
                    bMemoryFullFlag = false;

                    if( memory_full_dlg != null ) {
                        memory_full_dlg.hide();
                        memory_full_dlg = null;
                    }

                    changeShengDao( true );
                }
            });
        }
    };

    //private BroadcastReceiver autoSaveReceiver = new BroadcastReceiver() {
    //    @Override
    //    public void onReceive(Context context, Intent intent) {
    //        if( yy_auto_save_listener != null ) {
    //            yy_auto_save_listener.onAutoSave();
    //            yy_auto_save_listener = null;
    //        }
    //    }
    //};

    private BroadcastReceiver incomingCallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            yy_playing_msg_dlg = null;

            finish();
        }
    };

    AudioManager localAudioManager = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        main_activity = this;

        PowerManager pm = (PowerManager)getSystemService( Context.POWER_SERVICE );
        //wakeLock = pm.newWakeLock( PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ON_AFTER_RELEASE, CallGuardianActivity.class.getName() );
        wakeLock = pm.newWakeLock( PowerManager.PARTIAL_WAKE_LOCK, CallGuardianActivity.class.getName() );

        localAudioManager = (AudioManager)getSystemService( Context.AUDIO_SERVICE );  


        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();  
            winParams.flags |= WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;  
            win.setAttributes( winParams );

            // create our manager instance after the content view is set
            SystemBarTintManager tintManager = new SystemBarTintManager( this );
            // enable status bar tint
            tintManager.setStatusBarTintEnabled( true );
            //// enable navigation bar tint
            //tintManager.setNavigationBarTintEnabled( true );

            // set a custom tint color for all system bars
            tintManager.setTintColor( Color.parseColor( "#B8392D" ) );
            //// set a custom navigation bar resource
            //tintManager.setNavigationBarTintResource(R.drawable.my_tint);
            //// set a custom status bar drawable
            //tintManager.setStatusBarTintDrawable(MyDrawable);
        }

        yy_common = new YYCommon();
        yy_schedule = new YYSchedule( this );
        yy_command = new YYCommand( this );
        yy_show_alert_dialog = new YYShowAlertDialog( this );
        yy_data_source = new YYDataSource( this );
        yy_input_number_view = new YYInputNumberView();
        yy_input_number_pin_view = new YYInputNumberPINView();

        loadSharedPreferences();
        saveSharedPreferences();

        if( bShowWelcomePage ) {
            openWelcomeView();
        } else {
            openCallContolView();
        }
    }

    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            YYViewBase.onBackClick();
        }

        return false;
    }

    public final static String ANSWER_MACHINE_CHANGE_HEADSET = "andorid.intent.action.answer.machine.change.headset";           // 耳机
    public final static String ANSWER_MACHINE_CHANGE_HANDFREE = "andorid.intent.action.answer.machine.change.handfree";         // 免提
    public final static String ANSWER_MACHINE_CHANGE_CALL = "andorid.intent.action.answer.machine.change.call";                 // private
    public final static String ANSWER_MACHINE_CHANGE_NORMAL = "andorid.intent.action.answer.machine.change.normal";             // 普通
    public void changeShengDao( boolean bHandFree ) {
        if( yy_playing_msg_dlg != null ) {
            Intent intent = new Intent();  
            if( localAudioManager.isWiredHeadsetOn() ) {
                intent.setAction( ANSWER_MACHINE_CHANGE_HEADSET );
            } else {
                if( !bHandFree ) {
                    intent.setAction( ANSWER_MACHINE_CHANGE_CALL );
                } else {
                    intent.setAction( ANSWER_MACHINE_CHANGE_HANDFREE );
                }
            }
            sendBroadcast( intent );

            setVolumeControlStream( AudioManager.STREAM_VOICE_CALL );
            yy_playing_msg_dlg.setVolumeControlStream( AudioManager.STREAM_VOICE_CALL );
        } else {
            Intent intent = new Intent();  
            intent.setAction( ANSWER_MACHINE_CHANGE_NORMAL );
            sendBroadcast( intent );
        }
    }

    public void changeShengDaoRecordStart( AlertDialog ad ) {
        Intent intent = new Intent();  
        if( localAudioManager.isWiredHeadsetOn() ) {
            intent.setAction( ANSWER_MACHINE_CHANGE_HEADSET );
        } else {
            intent.setAction( ANSWER_MACHINE_CHANGE_HANDFREE );
        }
        sendBroadcast( intent );

        setVolumeControlStream( AudioManager.STREAM_VOICE_CALL );
        ad.setVolumeControlStream( AudioManager.STREAM_VOICE_CALL );
    }

	@Override
	protected void onResume() {
        super.onResume();

        //if( localAudioManager != null ) {
        //    changeShengDao( true );
        //}

        acquireWakeLock();
    }

	@Override
	protected void onPause() {
        //if( yy_playing_msg_dlg != null && !bRecordFlag ) {
        //    Intent intent = new Intent();  
        //    intent.setAction( ANSWER_MACHINE_CHANGE_NORMAL );
        //    sendBroadcast( intent );
        //}

        if( yy_current_view.bQuitPause ) {
            yy_schedule.scheduleOnceTime( 20, new YYSchedule.onScheduleAction() {
                public void doSomething() {
                    finish();
                }
            });
        }

        releaseWakeLock();

        super.onPause();
    }

	@Override
	protected void onDestroy()
	{
        bIsDestroy = true;

        unregisterReceiver( headsetPlugReceiver );
        unregisterReceiver( playingMsgEndReceiver );
        unregisterReceiver( incomingCallReceiver );
        //unregisterReceiver( autoSaveReceiver );
        unregisterReceiver( memoryFullReceiver );

        releaseWakeLock();

		// TODO Auto-generated method stub
		super.onDestroy();

        yy_schedule.cancelAllSchedule();
        yy_command.unregisterReceiver();
		//unregisterReceiver( yy_command.commandReceiver );
	}

    public boolean acquireWakeLock() {
        if( lockAcquired )
            return true;

        if( wakeLock != null ) {
            try {
                wakeLock.acquire();
                lockAcquired = true;
            }
            catch( SecurityException e )
            {
                Log.v( "AnswerMachine", "can't acquire wake lock:" + e.toString() );
            }

            return true;
        }

        return false;
    }

    public boolean releaseWakeLock() {
        if( wakeLock != null ) {
            try {
                wakeLock.setReferenceCounted(false);
                wakeLock.release();
                lockAcquired = false;
            }
            catch( SecurityException e )
            {
                Log.v( "AnswerMachine", "can't release wake lock:" + e.toString() );
            }

            return true;
        }

        return false;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素
     */
    public int dip2px( float dpValue ) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public int px2dip( float pxValue ) {
        final float scale = getResources().getDisplayMetrics().density;
        Log.v( "px2dp", "scale : " + scale );
        return (int) (pxValue / scale + 0.5f);
    }

    // 
    private boolean bIsLoading = false;
    private static String LOCAL_PREFER_NAME = "callguardian";
    public void loadSharedPreferences() {
        bIsLoading = true;

        // 
        SharedPreferences share = main_activity.getSharedPreferences( LOCAL_PREFER_NAME, Context.MODE_WORLD_READABLE );

        yy_data_source.setMobileCallsMode( share.getBoolean( "Mobile", true ) ? YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED );
        yy_data_source.setPreminumRateCallsMode( share.getBoolean( "Premium", true ) ? YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED );
        yy_data_source.setInternationalCallsMode( share.getBoolean( "International", true ) ? YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED );
        yy_data_source.setAllDialledCallsMode( share.getBoolean( "AllDialled", true ) ? YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED );
        bContactSynchronising = share.getBoolean( "ContactSynchronising", false );

        bShowWelcomePage = share.getBoolean( "ShowWelcomePage", true );

        bIsLoading = false;
    }

    public void saveSharedPreferences() {
        if( bIsLoading )
            return;

        SharedPreferences share = main_activity.getSharedPreferences( LOCAL_PREFER_NAME, Context.MODE_WORLD_READABLE );
        SharedPreferences.Editor editor = share.edit();

        editor.putBoolean( "Mobile", yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "Premium", yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "International", yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "AllDialled", yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "ContactSynchronising", bContactSynchronising );
        editor.putBoolean( "ShowWelcomePage", bShowWelcomePage );

        editor.commit();
    }

    public void openWelcomeView() {
        setContentView( R.layout.welcome );

        List<View> views = new ArrayList<View>();

        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams( LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT );

        //初始化引导图片列表
        for( int i=0; i < welcome_res_ids.length; ++i ) {
            ImageView iv = new ImageView( this );
            iv.setLayoutParams( mParams );
            iv.setImageResource( welcome_res_ids[i] );
            views.add( iv );
        }

        ViewPager vp = (ViewPager) findViewById(R.id.viewpager);
        //初始化Adapter
        vp.setAdapter( new ViewPagerAdapter( views ) );
        //绑定回调
        vp.setOnPageChangeListener( new OnPageChangeListener() {
            //当滑动状态改变时调用
            @Override
            public void onPageScrollStateChanged(int arg0) {
                // TODO Auto-generated method stub

            }

            //当当前页面被滑动时调用
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub

            }

            //当新的页面被选中时调用
            @Override
            public void onPageSelected(int arg0) {
                //设置底部小点选中状态
                if( arg0 == welcome_res_ids.length - 1 ) {
                    bShowWelcomePage = false;
                    saveSharedPreferences();

                    openCallContolView();
                }
            }
        });
    }

    public void openCallContolView() {
        // 
        call_control_view = new CallControlView();
        call_control_view.setView( false, null );

        IntentFilter filter = new IntentFilter();
        filter.addAction( "android.intent.action.HEADSET_PLUG" );
        registerReceiver( headsetPlugReceiver, filter );  

        IntentFilter filter2 = new IntentFilter();
        filter2.addAction( "com.action.dect.page.voicemsg.play.over" );
        filter2.addAction( "com.action.dect.page.voicemsg.overtime.autosave" );
        filter2.addAction( "com.action.dect.page.voicemsg.delete.play.over" );
        registerReceiver( playingMsgEndReceiver, filter2 );  

        IntentFilter filter3 = new IntentFilter();
        filter3.addAction( "com.action.dect.page.incoming.call" );
        //filter3.addAction( "com.action.dect.call.guardian.handing.result" );
        filter3.addAction( "com.action.dect.settings.base.brst.result" );       // base reset
        registerReceiver( incomingCallReceiver, filter3 );  

        IntentFilter filter4 = new IntentFilter();
        filter4.addAction( "com.action.dect.page.memory.full" );
        registerReceiver( memoryFullReceiver, filter4 );  

        //IntentFilter filter5 = new IntentFilter();
        //filter5.addAction( "com.action.dect.page.voicemsg.overtime.autosave" );
        //registerReceiver( autoSaveReceiver, filter5 );  


        if( bContactSynchronising ) {
            String title = "Error adding contacts to\r\nthe allowed list";
            String tips = "Press OK to return";
            int nDrawableResID = R.drawable.failure;
            int nOKResID = R.drawable.alert_dialog_ok;
            yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, nDrawableResID, tips, nOKResID, new YYShowAlertDialog.onAlertDialogClickHandler() {
                public boolean getIsCancelEnable() { return true; }
                public int getKeybackIsCancel() { return 0; }
                public void onOK() {
                    yy_data_source.initDataSource();
                }
                public void onCancel() { }
                public void onKeyback() { }
            });

            bContactSynchronising = false;
            saveSharedPreferences();
        } else {
            yy_data_source.initDataSource();
        }
    }
}
