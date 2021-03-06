package com.yy2039.callguardian;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import android.view.View;
import android.app.AlertDialog;

import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ImageView;
import android.content.Intent;
import android.widget.Toast;
import android.widget.ImageButton;
import android.util.Log;

public class CallControlView extends YYViewBase {
    private BTCallGuardianView bt_call_guardian;
    private DoNotDisturbView do_not_disturb;
    private OutgoingCallsView outgoing_calls_view;
    public CallControlView() {
        view_layout_res_id = R.layout.title_listview;

        bt_call_guardian = new BTCallGuardianView();
        do_not_disturb = new DoNotDisturbView();
        outgoing_calls_view = new OutgoingCallsView();
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        // 
        fillListView();
    }

    public String getViewTitle() {
        return "Call Control";
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // BT Call Control
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( R.drawable.bt_call_guardian );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                String text1 = "BT Call Control";
                String text2 = main_activity.yy_data_source.getBTCallGuardianModeOn() ? "On" : "Off";

                btn_obj.setText( YYViewBase.transferText( text1, text2 ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        bt_call_guardian.resetLastPosition();
                        bt_call_guardian.setView( true, yy_view_self.getViewBackHandler() );
                    }
                });
            }
        });
        ret_data.add( map );

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //// Do Not Disturb
        //map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        //map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
        //    @Override
        //    public void item_handle( Object view_obj ) {
        //        ((ImageView)view_obj).setBackgroundResource( R.drawable.do_not_disturb );
        //    }
        //});
        //map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
        //    @Override
        //    public void item_handle( Object view_obj ) {
        //        Button btn_obj = (Button)view_obj;

        //        btn_obj.setText( "Do Not Disturb" );
        //        btn_obj.setOnClickListener( new View.OnClickListener() {
        //            @Override
        //            public void onClick( View v ) { do_not_disturb.setView( true, yy_view_self.getViewBackHandler() ); }
        //        });
        //    }
        //});
        //ret_data.add( map );

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Outgoing Calls
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( R.drawable.outgoing_calls );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( "Outgoing Calls" );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    @Override
                    public void onClick( View v ) {
                        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GDES_RESULT, new YYCommand.onCommandListener() {
                            public void onSend() {
                                Intent gdesIntent = new Intent( YYCommand.CALL_GUARDIAN_GDES );
                                gdesIntent.putExtra( "data", "00" );
                                main_activity.sendBroadcast( gdesIntent );
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : send" );
                            }
                            public void onRecv( final String data ) {
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv " + data );
                                if( data == null ) {
                                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                }
                                else {
                                    final String pin_type = data.equals( "01" ) ? "first" : "enter";
                                    final String title = data.equals( "01" ) ? "Choose your PIN" : "Confirm your PIN";
                                    if( pin_type.equals( "enter" ) ) {
                                        showInputView( title, pin_type );
                                    } else {
                                        main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                            public void onInit( AlertDialog ad, View view ) {
                                                TextView tv_tips = (TextView)view.findViewById( R.id.attention_text );
                                                tv_tips.setText( "        You need to set your Access PIN before using call control. The Access PIN is used for both remote access and call control." );

                                                ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                                btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_back ) );

                                                ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                                btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                            }
                                            public boolean getIsCancelEnable() { return true; }
                                            public int getKeybackIsCancel() { return 2; }
                                            public void onOK() {
                                            }
                                            public void onCancel() {
                                                showInputView( title, pin_type );
                                            }
                                            public void onKeyback() {}
                                        });
                                    }
                                }
                            }
                            public void onFailure() {
                                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : failed " );
                                String text = String.format( "%s recv failed", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                                //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                            }
                        });
                    }
                });
            }
        });
        ret_data.add( map );

        return ret_data;
    }

    public void updateView() {
        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
        task.execute();
    }

    public void showInputView( final String title, final String pin_type ) {
        main_activity.yy_input_number_pin_view.showInputNumberView( title, "", yy_view_self.getViewBackHandler(), pin_type, new YYInputNumberPINView.onYYInputNumberPINHandler() {
            public void onSuccessful( String number ) {
                outgoing_calls_view.setView( false, yy_view_self.getViewBackHandler() );

                if( pin_type.equals( "first" ) ) {
                    main_activity.yy_data_source.setIsFirstTimeUse( false );

                    main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention_2, new YYShowAlertDialog.onAlertDialogHandler() {
                        public void onInit( AlertDialog ad, View view ) {
                            String text1 = "Please remember this Access PIN is used for both remote access and outgoing call control";
                            TextView tv = (TextView)view.findViewById( R.id.attention_text );
                            tv.setText( text1 );
                        }
                        public boolean getIsCancelEnable() { return false; }
                        public int getKeybackIsCancel() { return 0; }
                        public void onOK() { }
                        public void onCancel() { }
                        public void onKeyback() {}
                    });
                }
            }
        });

    }
}
