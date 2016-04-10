package com.yy2039.callguardian;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class OutgoingCallsView extends YYViewBackList {
    public OutgoingCallsView() {
    }

    public String getViewTitle() { return "Outgoing Calls"; }

    public void onResume() {
        YYViewBase.onBackClick();

        main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GDES_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                Intent gdesIntent = new Intent( YYCommand.CALL_GUARDIAN_GDES );
                gdesIntent.putExtra( "data", "00" );
                main_activity.sendBroadcast( gdesIntent );
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : recv " + data );
                if( data == null ) {
                    String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                    //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                }
                else {
                    final String pin_type = data.equals( "01" ) ? "first" : "enter";
                    String title = data.equals( "01" ) ? "Choose your PIN" : "Confirm your PIN";
                    main_activity.yy_input_number_pin_view.showInputNumberView( "Confirm your PIN", "", main_activity.yy_current_view.getViewBackHandler(), pin_type, new YYInputNumberPINView.onYYInputNumberPINHandler() {
                        public void onSuccessful( String number ) {
                            yy_view_self.setView( true, yy_view_self.getViewBackHandler() );

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
            public void onFailure() {
                Log.v( "cconn", "CALL_GUARDIAN_GDES_RESULT : failed " );
                String text = String.format( "%s recv failed", YYCommand.CALL_GUARDIAN_GDES_RESULT );
                //Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
            }
        });
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        // All dialled calls
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "All dialled calls", main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Allowed (default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_all_dialled_calls_mode", YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Barred"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_all_dialled_calls_mode", YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "All dialled calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public boolean getIsCancelEnable() { return true; }
                            public int getKeybackIsCancel() { return 2; }
                            public void onOK() {
                                main_activity.yy_data_source.setAllDialledCallsMode( (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_all_dialled_calls_mode" ) );

                                yy_list_adapter.list_data = getItemListData();

                                YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                task.execute();
                            }
                            public void onCancel() { }
                            public void onKeyback() {}
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

        if( main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ) {
            // Mobile calls
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Mobile calls", main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Allowed (default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_mobile_calls_mode", YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED; }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Barred"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_mobile_calls_mode", YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED; }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Mobile calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public boolean getIsCancelEnable() { return true; }
                                public int getKeybackIsCancel() { return 2; }
                                public void onOK() {
                                    Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_mobile_calls_mode" );
                                    if( nCurSel != null ) {
                                        main_activity.yy_data_source.setMobileCallsMode( nCurSel );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                }
                                public void onCancel() { }
                                public void onKeyback() {}
                            });
                        }
                    });
                }
            });

            ret_list_data.add( map );

            // International calls
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "International calls", main_activity.yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Allowed (default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_international_calls_mode", YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED; }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Barred"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_international_calls_mode", YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED; }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "International calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public boolean getIsCancelEnable() { return true; }
                                public int getKeybackIsCancel() { return 2; }
                                public void onOK() {
                                    Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_international_calls_mode" );
                                    if( nCurSel != null ) {
                                        main_activity.yy_data_source.setInternationalCallsMode( nCurSel );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                }
                                public void onCancel() { }
                                public void onKeyback() {}
                            });
                        }
                    });
                }
            });

            ret_list_data.add( map );

            // Preminum rate calls
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Premium rate calls", main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Allowed (default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_preminum_rate_calls_mode", YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED; }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Barred"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_preminum_rate_calls_mode", YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED; }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Premium rate calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public boolean getIsCancelEnable() { return true; }
                                public int getKeybackIsCancel() { return 2; }
                                public void onOK() {
                                    Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_preminum_rate_calls_mode" );
                                    if( nCurSel != null ) {
                                        main_activity.yy_data_source.setPreminumRateCallsMode( nCurSel );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                }
                                public void onCancel() { }
                                public void onKeyback() {}
                            });
                        }
                    });
                }
            });

            ret_list_data.add( map );
        }

        // Change access PIN
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Change access PIN", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        main_activity.yy_input_number_pin_view.showInputNumberView( "Confirm your PIN", "", yy_view_self.getViewBackHandler(), "first", new YYInputNumberPINView.onYYInputNumberPINHandler() {
                            public void onSuccessful( String number ) {
                                //main_activity.yy_data_source.setPINNumber( number );

                                YYViewBase.onBackClick();

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
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

        return ret_list_data;
    }
}
