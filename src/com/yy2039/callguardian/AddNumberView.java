package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ImageView;
import android.view.View;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.content.Intent;
import android.widget.Toast;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class AddNumberView extends YYViewBackList {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int ADD_NUMBER_VIEW_TYPE_BLOCK_NUMBER = 1;
    public static final int ADD_NUMBER_VIEW_TYPE_ALLOW_NUMBER = 2;

    public interface onViewHandler {
        void onEnterManuallySave( String number );
        String getAttentionText();
        void onFromCallsListOK( String number );
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private FromCallsListView from_calls_list_view;
    private onViewHandler v_handler;
    public AddNumberView() {
        from_calls_list_view = new FromCallsListView();
    }

    public String getViewTitle() { return "Add number"; }
    public void setViewType( int view_type ) {
        switch( view_type ) {
            case ADD_NUMBER_VIEW_TYPE_BLOCK_NUMBER: {
                v_handler = new onViewHandler() {
                    public void onEnterManuallySave( String number ) {
                        main_activity.yy_data_source.addBlockNumber( 2, number, new YYDataSource.onAddNumberSuccefully() {
                            public void onSuccessfully() {
                                String title = "Successfully added to the BLOCKED list";
                                String tips = "Press OK to finish";
                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                    public void onOK() { }
                                    public void onCancel() { }
                                });
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "add block number failed", Toast.LENGTH_SHORT ).show();
                            }
                        });
                    }
                    public String getAttentionText() { return "All future calls from this number will be BLOCKED.Are you sure you wish to continue?"; }
                    public void onFromCallsListOK( String number ) {
                        main_activity.yy_data_source.addBlockNumber( 2, number, new YYDataSource.onAddNumberSuccefully() {
                            public void onSuccessfully() {
                                // 移除这个 item

                                // 弹窗提示成功
                                String title = "Successfully added to the BLOCKED list";
                                String tips = "Press OK to finish";
                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                    public void onOK() {  }
                                    public void onCancel() { }
                                });

                                // 更新列表
                                from_calls_list_view.yy_list_adapter.list_data = from_calls_list_view.getItemListData();

                                YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                task.execute();
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "add block number failed", Toast.LENGTH_SHORT ).show();
                            }
                        });
                    }
                };
            }
            break;
            case ADD_NUMBER_VIEW_TYPE_ALLOW_NUMBER: {
                v_handler = new onViewHandler() {
                    public void onEnterManuallySave( String number ) {
                        main_activity.yy_data_source.addAllowNumber( 2, number, new YYDataSource.onAddNumberSuccefully() {
                            public void onSuccessfully() {
                                String title = "Successfully added to the ALLOWED list";
                                String tips = "Press OK to finish";
                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                    public void onOK() { }
                                    public void onCancel() { }
                                });
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "add allow number failed", Toast.LENGTH_SHORT ).show();
                            }
                        });
                    }
                    public String getAttentionText() { return "All future calls from this number will be ALLOWED.Are you sure you wish to continue?"; }
                    public void onFromCallsListOK( String number ) {
                        main_activity.yy_data_source.addAllowNumber( 2, number, new YYDataSource.onAddNumberSuccefully() {
                            public void onSuccessfully() {
                                // 移除这个 item

                                // 弹窗提示成功
                                String title = "Successfully added to the ALLOWED list";
                                String tips = "Press OK to finish";
                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                    public void onOK() {  }
                                    public void onCancel() { }
                                });

                                // 更新列表
                                from_calls_list_view.yy_list_adapter.list_data = from_calls_list_view.getItemListData();

                                YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                task.execute();
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "add allow number failed", Toast.LENGTH_SHORT ).show();
                            }
                        });
                    }
                };
            }
            break;
        }
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        // Enter manually
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "Enter manually", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        main_activity.yy_input_number_view.showInputNumberView( "Add number", "", -1, 110, yy_view_self.getViewBackHandler(), new YYInputNumberView.onYYInputNumberHandler() {
                            public void onSave( String number ) { v_handler.onEnterManuallySave( number ); }
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

        // From calls list
        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( YYViewBase.transferText( "From calls list", "" ) );
                btn_obj.setOnClickListener( new View.OnClickListener() {
                    public void onClick( View v ) {
                        main_activity.yy_data_source.getCallsList( new YYDataSource.onCallsListListener() {
                            public void onSuccessfully() {
                                from_calls_list_view.setView( true, yy_view_self.getViewBackHandler() );
                            }
                            public void onFailure() {
                                Toast.makeText( main_activity, "get calls list failed", Toast.LENGTH_LONG ).show();
                            }
                        });
                    }
                });
            }
        });

        ret_list_data.add( map );

        return ret_list_data;
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class FromCallsListView extends YYViewBackList {
        public FromCallsListView() {
        }
        public String getViewTitle() { return "Add number"; }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<YYDataSource.callsListItem> calls_list = main_activity.yy_data_source.cur_calls_list;

            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            for( int i=0; i < calls_list.size(); ++i ) {
                final YYDataSource.callsListItem item_info = calls_list.get( i );

                Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                    public void item_handle( Object view_obj ) {
                        final Button btn_obj = (Button)view_obj;

                        String name = item_info.getNumber();
                        if( name.equals( "" ) ) {
                            name = item_info.getName();
                        }
                        String callTime = item_info.getCallTime();
                        int state = item_info.getState();

                        // 
                        String html = "";
                        if( state == YYCommon.CALLS_LIST_STATE_4 ) {
                            html = String.format( "<img src='call_state_4'/>%s<br/><img src='call_state_3'/>%s", name, callTime );
                        }
                        else {
                            String state_id = String.format( "call_state_%d", state );
                            html = String.format( "%s<br/><img src='%s'/>%s", name, state_id, callTime );
                        }
                        Log.v( "prot", "++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++=html : " + html );

                        // 
                        btn_obj.setText( YYViewBase.transferText( html ) );
                        btn_obj.setMovementMethod( LinkMovementMethod.getInstance() );

                        btn_obj.setOnClickListener( new View.OnClickListener() {
                            public void onClick( View v ) {
                                main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                    public void onInit( AlertDialog ad, View view ) {
                                        String text1 = v_handler.getAttentionText();
                                        TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                        tv.setText( text1 );

                                        // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                        ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                        btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                        ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                        btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                    }
                                    public void onOK() { }
                                    public void onCancel() { v_handler.onFromCallsListOK( item_info.getNumber() ); }
                                });
                            }
                        });
                    }
                });

                ret_list_data.add( map );
            }

            return ret_list_data;
        }
    }
}

