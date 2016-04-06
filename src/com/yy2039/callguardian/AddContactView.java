package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Toast;

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
public class AddContactView extends YYViewBackList {
    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public static final int ADD_CONTACT_VIEW_TYPE_BLOCK_NUMBER = 1;
    public static final int ADD_CONTACT_VIEW_TYPE_ALLOW_NUMBER = 2;

    private Set<String> blockedNames = new HashSet<String>();
    private Set<String> allowedNames = new HashSet<String>();

    private int total = 0;
    private int nCounter = 0;
    private boolean bSuccess = false;
    private boolean bFail = false;

    public interface onViewHandler {
        String getAttentionText();
        void onAttentionOK( YYDataSource.contactsListItem item_info );
    }

    private onViewHandler v_handler;
    public AddContactView() {
    }

    public String getViewTitle() { return "Add contact"; }
    public void setViewType( int view_type ) {
        switch( view_type ) {
            case ADD_CONTACT_VIEW_TYPE_BLOCK_NUMBER: {
                v_handler = new onViewHandler() {
                    public String getAttentionText() { return "All numbers asscociated with this\r\ncontact will be added to the\r\nBLOCKED list.Are you sure you\r\nwish to continue?"; }
                    public void onAttentionOK( final YYDataSource.contactsListItem item_info ) {
                        final String name = item_info.getName();
                        List<String> number_list = item_info.getNumber();
                        total = number_list.size();

                        nCounter = 0;
                        bSuccess = false;
                        bFail = false;

                        for( int i=0; i < number_list.size(); ++i ) {
                            main_activity.yy_data_source.addBlockNumber( 2, number_list.get( i ), new YYDataSource.onAddNumberSuccefully() {
                                public void onSuccessfully() {
                                    nCounter = nCounter + 1;
                                    bSuccess = true;

                                    // 如果全部返回了，并且没有失败
                                    if( nCounter >= total ) {
                                        if( bFail ) {
                                            //Toast.makeText( main_activity, "Add block number from contacts : failure", Toast.LENGTH_SHORT ).show();
                                        } else {
                                            // 移除这个 item
                                            blockedNames.add( name );

                                            // 弹窗提示成功
                                            String title = "Successfully added to the\r\nBLOCKED list";
                                            String tips = "Press OK to finish";
                                            main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                                public void onOK() {  }
                                                public void onCancel() { }
                                            });

                                            // 更新列表
                                            yy_list_adapter.list_data = getItemListData();

                                            YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                            task.execute();
                                        }
                                    }
                                }
                                public void onFailure() {
                                    nCounter = nCounter + 1;
                                    bFail = true;

                                    // 如果全部返回了，但有失败的
                                    if( nCounter >= total ) {
                                        //Toast.makeText( main_activity, "Add block number from contacts : failure", Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });
                        }
                    }
                };
            }
            break;
            case ADD_CONTACT_VIEW_TYPE_ALLOW_NUMBER: {
                v_handler = new onViewHandler() {
                    public String getAttentionText() { return "All numbers asscociated with this\r\ncontact will be added to the\r\nALLOWED list.Are you sure you\r\nwish to continue?"; }
                    public void onAttentionOK( final YYDataSource.contactsListItem item_info ) {
                        final String name = item_info.getName();
                        List<String> number_list = item_info.getNumber();
                        total = number_list.size();

                        nCounter = 0;
                        bSuccess = false;
                        bFail = false;
                        for( int i=0; i < number_list.size(); ++i ) {
                            main_activity.yy_data_source.addAllowNumber( 2, number_list.get( i ), new YYDataSource.onAddNumberSuccefully() {
                                public void onSuccessfully() {
                                    nCounter = nCounter + 1;
                                    bSuccess = true;

                                    // 如果全部返回了，并且没有失败
                                    if( nCounter >= total ) {
                                        if( bFail ) {
                                            //Toast.makeText( main_activity, "Add allow number from contacts : failure", Toast.LENGTH_SHORT ).show();
                                        } else {
                                            // 移除这个 item
                                            allowedNames.add( name );

                                            // 弹窗提示成功
                                            String title = "Successfully added to the\r\nALLOWED list";
                                            String tips = "Press OK to finish";
                                            main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, R.drawable.successfully, tips, R.drawable.alert_dialog_ok, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                                public void onOK() {  }
                                                public void onCancel() { }
                                            });

                                            // 更新列表
                                            yy_list_adapter.list_data = getItemListData();

                                            YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                            task.execute();
                                        }
                                    }
                                }
                                public void onFailure() {
                                    nCounter = nCounter + 1;
                                    bFail = true;

                                    // 如果全部返回了，但有失败的
                                    if( nCounter >= total ) {
                                        //Toast.makeText( main_activity, "Add allow number from contacts : failure", Toast.LENGTH_SHORT ).show();
                                    }
                                }
                            });
                        }
                    }
                };
            }
            break;
        }
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<YYDataSource.contactsListItem> contacts_list = main_activity.yy_data_source.getContactsList();

        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        for( int i=0; i < contacts_list.size(); ++i ) {
            final YYDataSource.contactsListItem item_info = contacts_list.get( i );

            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
                @Override
                public void item_handle( Object view_obj ) {
                    // TODO:需要先获取blocked list
                    //((ImageView)view_obj).setBackgroundResource( item_info.getMsgType() == 0 ? R.drawable.blocked_1 : R.drawable.blocked_2 );
                }
            });
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( item_info.getName(), "" ) );
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
                                public void onCancel() { v_handler.onAttentionOK( item_info ); }
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

