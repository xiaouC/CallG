package com.yy2039.callguardian;

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

public class CallGuardianActivity extends FragmentActivity
{
    public static CallGuardianActivity main_activity;

    public YYCommon yy_common;
    public YYDataSource yy_data_source;
    public YYCommand yy_command;
    public CallControlView call_control_view;
    public YYShowAlertDialog yy_show_alert_dialog;
    public YYInputNumberView yy_input_number_view;
    public YYInputNumberPINView yy_input_number_pin_view;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        main_activity = this;

        yy_common = new YYCommon();
        yy_command = new YYCommand( this );
        yy_show_alert_dialog = new YYShowAlertDialog( this );
        yy_data_source = new YYDataSource( this );
        yy_input_number_view = new YYInputNumberView();
        yy_input_number_pin_view = new YYInputNumberPINView();

        // 
        call_control_view = new CallControlView();
        call_control_view.setView( false, null );

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

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics( dm );
        int widthPixels= dm.widthPixels;
        int heightPixels= dm.heightPixels;
        float density = dm.density;
        int screenWidth = (int)( widthPixels * density );
        int screenHeight = (int)( heightPixels * density );

        Log.v( "cocos", "screenWidth : " + screenWidth );
        Log.v( "cocos", "screenHeight : " + screenHeight );
        Log.v( "cocos", "density : " + density );

        Drawable drawable_1 = main_activity.getResources().getDrawable( R.drawable.item_button_long );
        Log.v( "drawable", "long width : " + drawable_1.getIntrinsicWidth() );
        Log.v( "drawable", "long height : " + drawable_1.getIntrinsicHeight() );

        Drawable drawable_2 = main_activity.getResources().getDrawable( R.drawable.item_button_short );
        Log.v( "drawable", "short width : " + drawable_2.getIntrinsicWidth() );
        Log.v( "drawable", "short height : " + drawable_2.getIntrinsicHeight() );

        loadSharedPreferences();
        saveSharedPreferences();
    }

    public boolean onKeyDown( int keyCode, KeyEvent event )
    {
        if( keyCode == KeyEvent.KEYCODE_BACK )
        {
            YYViewBase.onBackClick();
        }

        return false;
    }

	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();

        yy_command.unregisterReceiver();
		//unregisterReceiver( yy_command.commandReceiver );
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
        yy_data_source.setPreminumRateCallsMode( share.getBoolean( "Preminum", true ) ? YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED );
        yy_data_source.setInternationalCallsMode( share.getBoolean( "International", true ) ? YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED );
        yy_data_source.setAllDialledCallsMode( share.getBoolean( "AllDialled", true ) ? YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED : YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED );

        bIsLoading = false;
    }

    public void saveSharedPreferences() {
        if( bIsLoading )
            return;

        SharedPreferences share = main_activity.getSharedPreferences( LOCAL_PREFER_NAME, Context.MODE_WORLD_READABLE );
        SharedPreferences.Editor editor = share.edit();

        editor.putBoolean( "Mobile", yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "Preminum", yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "International", yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED ? true : false );
        editor.putBoolean( "AllDialled", yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ? true : false );

        editor.commit();
    }
}
