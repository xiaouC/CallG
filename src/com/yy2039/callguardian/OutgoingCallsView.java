package com.yy2039.callguardian_1;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;

public class OutgoingCallsView extends YYViewBackList {
    public OutgoingCallsView() {
    }

    public String getViewTitle() { return "Outgoing Calls"; }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        // Mobile calls
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Mobile calls", main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Allowed(default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_mobile_calls_mode", YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_ALLOWED; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Barred"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_mobile_calls_mode", YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getMobileCallsMode() == YYCommon.OUTGOING_CALLS_MOBILE_CALLS_MODE_BARRED; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Mobile calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_mobile_calls_mode" );
                                if( nCurSel != null ) {
                                    main_activity.yy_data_source.setMobileCallsMode( nCurSel );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
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
                            public String getRadioText() { return "Allowed(default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_international_calls_mode", YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_ALLOWED; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Barred"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_international_calls_mode", YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getInternationalCallsMode() == YYCommon.OUTGOING_CALLS_INTERNATIONAL_CALLS_MODE_BARRED; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "International calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_international_calls_mode" );
                                if( nCurSel != null ) {
                                    main_activity.yy_data_source.setInternationalCallsMode( nCurSel );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
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

                btn_obj.setText( YYViewBase.transferText( "Preminum rate calls", main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Allowed(default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_preminum_rate_calls_mode", YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_ALLOWED; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Barred"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_preminum_rate_calls_mode", YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getPreminumRateCallsMode() == YYCommon.OUTGOING_CALLS_PREMINUM_RATE_CALLS_MODE_BARRED; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Preminum rate calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_preminum_rate_calls_mode" );
                                if( nCurSel != null ) {
                                    main_activity.yy_data_source.setPreminumRateCallsMode( nCurSel );

                                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                    task.execute();
                                }
                            }
                            public void onCancel() { }
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

        // All dialled calls
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "All dialled calls", main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ? "Allowed" : "Barred" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Allowed(default)"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_all_dialled_calls_mode", YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_ALLOWED; }
                        });
                        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                            public String getRadioText() { return "Barred"; }
                            public void onRadioClick() { yy_view_self.yy_temp_data.put( "outgoing_calls_all_dialled_calls_mode", YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED ); }
                            public boolean isRadioChecked() { return main_activity.yy_data_source.getAllDialledCallsMode() == YYCommon.OUTGOING_CALLS_ALL_DIALLED_CALLS_MODE_BARRED; }
                        });

                        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "All dialled calls", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                main_activity.yy_data_source.setAllDialledCallsMode( (Integer)yy_view_self.yy_temp_data.get( "outgoing_calls_all_dialled_calls_mode" ) );

                                YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                task.execute();
                            }
                            public void onCancel() { }
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

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
                                main_activity.yy_data_source.setPINNumber( number );

                                YYViewBase.onBackClick();

                                main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention_2, new YYShowAlertDialog.onAlertDialogHandler() {
                                    public void onInit( AlertDialog ad, View view ) {
                                        String text1 = "Please remember this Access PIN is used for both remote access and outgoing call control";
                                        TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                        tv.setText( text1 );
                                    }
                                    public void onOK() { }
                                    public void onCancel() { }
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
