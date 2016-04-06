package com.yy2039.callguardian;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.app.AlertDialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.AbsoluteSizeSpan;
import android.graphics.Color;
import android.text.method.LinkMovementMethod;
import android.content.Intent;
import android.widget.Toast;
import android.util.Log;

public class BTCallGuardianView extends YYViewBack {
    private AnnounceMessageView announce_message_view;
    private BlockNumbersView block_numbers_view;
    private AllowNumbersView allow_numbers_view;
    private boolean bIsInitSwitchBtnState;

    public BTCallGuardianView() {
        view_layout_res_id = R.layout.title_back_listview_1;

        announce_message_view = new AnnounceMessageView();
        block_numbers_view = new BlockNumbersView();
        allow_numbers_view = new AllowNumbersView();
    }

    public void setView( boolean bIsPush, onViewBackHandler handler ) {
        super.setView( bIsPush, handler );

        bIsInitSwitchBtnState = true;

        // 
        Switch btn_obj = (Switch)main_activity.findViewById( R.id.button_state );
        btn_obj.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged( CompoundButton buttonView, boolean isChecked ) {
                if( !bIsInitSwitchBtnState ) {
                    main_activity.yy_data_source.setBTCallGuardianModeOn( isChecked );
                }

                updateState();
            }
        });

        fillListView();

        btn_obj.setChecked( main_activity.yy_data_source.getBTCallGuardianModeOn() );

        bIsInitSwitchBtnState = false;

        main_activity.yy_schedule.scheduleOnceTime( 20, new YYSchedule.onScheduleAction() {
            public void doSomething() {
                updateState();
            }
        });
    }

    public String getViewTitle() {
        return "BT Call Guardian";
    }

    public interface onSelectEvent {
        Integer getParam();
        void setParam( Integer nType );
    }

    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

        if( main_activity.yy_data_source.getBTCallGuardianModeOn() )
        {
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // BT Call Guardian Mode
            initItemData( ret_data, R.drawable.bt_call_guardian, getUpdateTextHandler_BTCallGuardianMode(), new View.OnClickListener() {
                public void onClick( View v ) {
                    List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                    item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                        public String getRadioText() { return "Announce (default)"; }
                        public void onRadioClick() { yy_view_self.yy_temp_data.put( "bt_call_guardian_mode", YYCommon.BT_CALL_GUARDIAN_MODE_ANNOUNCE ); }
                        public boolean isRadioChecked() { return main_activity.yy_data_source.getBTCallGuardianMode() == YYCommon.BT_CALL_GUARDIAN_MODE_ANNOUNCE; }
                    });
                    item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                        public String getRadioText() { return "International"; }
                        public void onRadioClick() { yy_view_self.yy_temp_data.put( "bt_call_guardian_mode", YYCommon.BT_CALL_GUARDIAN_MODE_INTERNATIONAL ); }
                        public boolean isRadioChecked() { return main_activity.yy_data_source.getBTCallGuardianMode() == YYCommon.BT_CALL_GUARDIAN_MODE_INTERNATIONAL; }
                    });
                    item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                        public String getRadioText() { return "Answerphone"; }
                        public void onRadioClick() { yy_view_self.yy_temp_data.put( "bt_call_guardian_mode", YYCommon.BT_CALL_GUARDIAN_MODE_ANSWERPHONE ); }
                        public boolean isRadioChecked() { return main_activity.yy_data_source.getBTCallGuardianMode() == YYCommon.BT_CALL_GUARDIAN_MODE_ANSWERPHONE; }
                    });
                    item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                        public String getRadioText() { return "Custom"; }
                        public void onRadioClick() { yy_view_self.yy_temp_data.put( "bt_call_guardian_mode", YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM ); }
                        public boolean isRadioChecked() { return main_activity.yy_data_source.getBTCallGuardianMode() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM; }
                    });

                    main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "BT Call Guardian Mode", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                        public void onOK() {
                            Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( "bt_call_guardian_mode" );
                            if( nCurSel != null ) {
                                main_activity.yy_data_source.setBTCallGuardianMode( nCurSel );

                                yy_view_self.yy_list_adapter.list_data = yy_view_self.getItemListData();

                                YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                task.execute();
                            }
                        }
                        public void onCancel() { }
                    });
                }
            });

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if( main_activity.yy_data_source.getBTCallGuardianMode() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM )
            {
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_BlockedNumbers(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Blocked numbers", "custom_blocked_numbers", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_BlockedNumber(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_BlockedNumber( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_AllowedNumbers(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Allowed numbers", "custom_allowed_numbers", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_AllowedNumber(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_AllowedNumber( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_International(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "International", "custom_international", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_International(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_International( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_Withheld(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Withheld", "custom_withheld", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_Withheld(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_Withheld( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_Payphones(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Payphones", "custom_payphones", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_Payphones(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_Payphones( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_MobileNumbers(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Mobile Numbers", "custom_mobile_numbers", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_MobileNumbers(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_MobileNumbers( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_Unavailable(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "Unavailable", "custom_unavailable", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_Unavailable(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_Unavailable( nType ); }
                        });
                    }
                });
                initItemData( ret_data, R.drawable.default_icon, getUpdateTextHandler_Custom_AllOtherNumbers(), new View.OnClickListener() {
                    public void onClick( View v ) {
                        openSelectCustom( "All other numbers", "custom_all_other_numbers", new onSelectEvent() {
                            public Integer getParam() { return main_activity.yy_data_source.getBTCallGuardianMode_Custom_AllOtherNumbers(); }
                            public void setParam( Integer nType ) { main_activity.yy_data_source.setBTCallGuardianMode_Custom_AllOtherNumbers( nType ); }
                        });
                    }
                });
            }

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Announce Message
            initItemData( ret_data, R.drawable.announce_message, getUpdateTextHandler_AnnounceMessage(), new View.OnClickListener() {
                public void onClick( View v ) {
                    //main_activity.yy_command.connectAnswerMachine( new YYCommand.onConnLisenter() {
                    //    public void onSuccessfully() { announce_message_view.setView( true, yy_view_self.getViewBackHandler() ); }
                    //    public void onFailure() {}
                    //});
                    announce_message_view.setView( true, yy_view_self.getViewBackHandler() );
                }
            });

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Block Numbers
            initItemData( ret_data, R.drawable.block_numbers, getUpdateTextHandler_BlockNumbers(), new View.OnClickListener() {
                public void onClick( View v ) {
                    block_numbers_view.setView( true, yy_view_self.getViewBackHandler() );
                }
            });

            //////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Allow Numbers
            initItemData( ret_data, R.drawable.allow_numbers, getUpdateTextHandler_AllowNumbers(), new View.OnClickListener() {
                public void onClick( View v ) {
                    allow_numbers_view.setView( true, yy_view_self.getViewBackHandler() );
                }
            });
        }

        return ret_data;
    }

    public void initItemData( List<Map<Integer,YYListAdapter.onYYListItemHandler>> item_list_data, final int image_id, final onUpdateTextHandler update_text_handler, final View.OnClickListener click_listener ) {
        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
        map.put( R.id.item_image, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                ((ImageView)view_obj).setBackgroundResource( image_id );
            }
        });
        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
            @Override
            public void item_handle( Object view_obj ) {
                Button btn_obj = (Button)view_obj;

                btn_obj.setText( update_text_handler.getText() );
                btn_obj.setOnClickListener( click_listener );
            }
        });

        item_list_data.add( map );
    }
    public void openSelectCustom( String title, final String data_type, final onSelectEvent se ) {
        List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
            public String getRadioText() { return "Allow"; }
            public void onRadioClick() { yy_view_self.yy_temp_data.put( data_type, YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW ); }
            public boolean isRadioChecked() { return se.getParam() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW; }
        });
        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
            public String getRadioText() { return "Announce"; }
            public void onRadioClick() { yy_view_self.yy_temp_data.put( data_type, YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANNOUNCE ); }
            public boolean isRadioChecked() { return se.getParam() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANNOUNCE; }
        });
        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
            public String getRadioText() { return "Block (default)"; }
            public void onRadioClick() { yy_view_self.yy_temp_data.put( data_type, YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_BLOCK ); }
            public boolean isRadioChecked() { return se.getParam() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_BLOCK; }
        });
        item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
            public String getRadioText() { return "Answerphone"; }
            public void onRadioClick() { yy_view_self.yy_temp_data.put( data_type, YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANSWERPHONE ); }
            public boolean isRadioChecked() { return se.getParam() == YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANSWERPHONE; }
        });

        main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( title, item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
            public void onOK() {
                Integer nCurSel = (Integer)yy_view_self.yy_temp_data.get( data_type );
                if( nCurSel != null ) {
                    se.setParam( nCurSel );

                    YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                    task.execute();
                }
            }
            public void onCancel() { }
        });
    }

    public void updateState() {
        // "on" or "off"
        TextView tv_state = (TextView)main_activity.findViewById( R.id.state_text );
        tv_state.setText( main_activity.yy_data_source.getBTCallGuardianModeOn() ? "on" : "off" );

        // tips
        TextView tv_tips = (TextView)main_activity.findViewById( R.id.tips_text );
        if( main_activity.yy_data_source.getBTCallGuardianModeOn() ) {
            tv_tips.setText( "" );
        } else {
            String text1 = "BT Call Guardian";
            String text2 = "You will need Caller Display to use\r\nBT Call Guardian and other Call control\r\nfeatures. Please contact your telephone\r\nservice provider for more information.";

            String text = text1 + "\r\n" + text2;

            SpannableString msp = new SpannableString( text );

            msp.setSpan( new StyleSpan( android.graphics.Typeface.BOLD ), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            msp.setSpan( new AbsoluteSizeSpan( 25 ), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            msp.setSpan( new ForegroundColorSpan( Color.BLACK ), 0, text1.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

            msp.setSpan( new StyleSpan( android.graphics.Typeface.NORMAL ), text1.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            msp.setSpan( new AbsoluteSizeSpan( 20 ), text1.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );
            msp.setSpan( new ForegroundColorSpan( Color.GRAY ), text1.length(), text.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE );

            tv_tips.setText( msp );
        }

        // list view
        yy_list_adapter.list_data = getItemListData();

        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
        task.execute();
    }

    public onUpdateTextHandler getUpdateTextHandler_BTCallGuardianMode() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "BT Call Guardian Mode";
                String text2 = "Announce";
                switch( main_activity.yy_data_source.getBTCallGuardianMode() )
                {
                    case YYCommon.BT_CALL_GUARDIAN_MODE_ANNOUNCE:
                        text2 = "Announce";
                        break;
                    case YYCommon.BT_CALL_GUARDIAN_MODE_INTERNATIONAL:
                        text2 = "International";
                        break;
                    case YYCommon.BT_CALL_GUARDIAN_MODE_ANSWERPHONE:
                        text2 = "Answerphone";
                        break;
                    case YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM:
                        text2 = "Custom";
                        break;
                }

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public String getCustomTypeText( Integer nType ) {
        String ret_text = "";
        switch( nType ) {
            case YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ALLOW:
                ret_text = "Allow";
                break;
            case YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANNOUNCE:
                ret_text = "Announce";
                break;
            case YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_BLOCK:
                ret_text = "Block";
                break;
            case YYCommon.BT_CALL_GUARDIAN_MODE_CUSTOM_ANSWERPHONE:
                ret_text = "Answerphone";
                break;
        }

        return ret_text;
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_BlockedNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Blocked numbers";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_BlockedNumber() );
                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_AllowedNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Allowed numbers";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_AllowedNumber() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_International() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "International";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_International() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_Withheld() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Withheld";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_Withheld() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_Payphones() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Payphones";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_Payphones() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_MobileNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Mobile numbers";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_MobileNumbers() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_Unavailable() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Unavailable";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_Unavailable() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_Custom_AllOtherNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "All other numbers";
                String text2 = getCustomTypeText( main_activity.yy_data_source.getBTCallGuardianMode_Custom_AllOtherNumbers() );

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_AnnounceMessage() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Announce message";
                String text2 = main_activity.yy_data_source.getIsUseDefaultMessage() ? "Default" : "";

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_BlockNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Block Numbers";
                String text2 = "";

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    public onUpdateTextHandler getUpdateTextHandler_AllowNumbers() {
        return new onUpdateTextHandler() {
            public SpannableString getText() {
                String text1 = "Allow Numbers";
                String text2 = "";

                return YYViewBase.transferText( text1, text2 );
            }
        };
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class AnnounceMessageView extends YYViewBackList {
        public AnnounceMessageView() {
        }

        public String getViewTitle() {
            return "Announce Message";
        }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            // record name
            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( getRecordNameText() );
                    btn_obj.setOnClickListener( new View.OnClickListener() { public void onClick( View v ) { showRecordNameAlertDialog(); } } );
                }
            });

            ret_list_data.add( map );

            // play message
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( getPlayMessageText() );
                    btn_obj.setOnClickListener( new View.OnClickListener() { public void onClick( View v ) { showPlayMessageAlertDialog(); } } );
                }
            });

            ret_list_data.add( map );

            // use default message
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( getUseDefaultMessageText() );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            List<YYShowAlertDialog.onAlertDialogRadioItemHandler> item_list_data = new ArrayList<YYShowAlertDialog.onAlertDialogRadioItemHandler>();

                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "On (default)"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "use_default_message", true ); }
                                public boolean isRadioChecked() { return main_activity.yy_data_source.getIsUseDefaultMessage(); }
                            });
                            item_list_data.add( new YYShowAlertDialog.onAlertDialogRadioItemHandler() {
                                public String getRadioText() { return "Off"; }
                                public void onRadioClick() { yy_view_self.yy_temp_data.put( "use_default_message", false ); }
                                public boolean isRadioChecked() { return !main_activity.yy_data_source.getIsUseDefaultMessage(); }
                            });

                            main_activity.yy_show_alert_dialog.showRadioGroupAlertDialog( "Use default message", item_list_data, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onOK() {
                                    Boolean use_default_msg = (Boolean)yy_view_self.yy_temp_data.get( "use_default_message" );
                                    if( use_default_msg != null ) {
                                        main_activity.yy_data_source.setIsUseDefaultMessage( use_default_msg );

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

            return ret_list_data;
        }

        public SpannableString getRecordNameText() {
            String text1 = "Record name";
            String text2 = "";

            return YYViewBase.transferText( text1, text2 );
        }
        public SpannableString getPlayMessageText() {
            String text1 = "Play message";
            String text2 = "";

            return YYViewBase.transferText( text1, text2 );
        }
        public SpannableString getUseDefaultMessageText() {
            String text1 = "Use default message";
            String text2 = main_activity.yy_data_source.getIsUseDefaultMessage() ? "on" : "off";

            return YYViewBase.transferText( text1, text2 );
        }

        public void showRecordNameAlertDialog() {
            main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                    tempIntent.putExtra( "operation", "2" );       // 2 : change , 3 : announce message
                    tempIntent.putExtra( "type", "3" );       // 0 : play , 3 : announce message
                    main_activity.sendBroadcast( tempIntent );
                }
                public void onRecv( String data ) {
                    Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT : " + data );
                    if( data != null && data.equals( "SUCCESS" ) ) {
                        String title = "Record name";
                        String tips = "Recording name";
                        main_activity.yy_playing_msg_dlg = main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.record_name, tips, R.drawable.alert_save, R.drawable.alert_delete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                main_activity.yy_playing_msg_dlg = null;
                                main_activity.yy_auto_save_listener = null;
                                main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                                    public void onSend() {
                                        Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                                        tempIntent.putExtra( "operation", "4" );        // 4 : stop change
                                        tempIntent.putExtra( "type", "3" );             // 3 : announce message
                                        main_activity.sendBroadcast( tempIntent );
                                    }
                                    public void onRecv( String data ) {
                                        main_activity.yy_data_source.initIsUseDefaultMessage( false );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();

                                        showPlayMessageAlertDialog();
                                    }
                                    public void onFailure() {
                                        showPlayMessageAlertDialog();
                                    }
                                });
                            }
                            public void onCancel() {
                                main_activity.yy_playing_msg_dlg = null;
                                main_activity.yy_auto_save_listener = null;
                                main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                                    public void onSend() {
                                        Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                                        tempIntent.putExtra( "operation", "1" );        // 1 : delete
                                        tempIntent.putExtra( "type", "3" );             // 3 : announce message
                                        main_activity.sendBroadcast( tempIntent );
                                    }
                                    public void onRecv( String data ) {
                                        Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT : " + data );
                                        if( data != null && data.equals( "SUCCESS" ) ) {
                                            main_activity.yy_data_source.initIsUseDefaultMessage( true );

                                            YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                            task.execute();
                                        }
                                        else {
                                            Toast.makeText( main_activity, "delete announce message failed", Toast.LENGTH_LONG ).show();
                                        }
                                    }
                                    public void onFailure() {
                                        Toast.makeText( main_activity, "delete announce message failed", Toast.LENGTH_LONG ).show();
                                    }
                                });
                            }
                        });
                        main_activity.yy_auto_save_listener = new CallGuardianActivity.onAutoSaveListener() {
                            public void onAutoSave() {
                                if( main_activity.yy_playing_msg_dlg != null ) {
                                    main_activity.yy_schedule.scheduleOnceTime( 100, new YYSchedule.onScheduleAction() {
                                        public void doSomething() {
                                            showPlayMessageAlertDialog();
                                        }
                                    });
                                }
                            }
                        };
                    }
                    else {
                        Toast.makeText( main_activity, "record announce message failed", Toast.LENGTH_LONG ).show();
                    }
                }
                public void onFailure() {
                    Toast.makeText( main_activity, "record announce message failed", Toast.LENGTH_LONG ).show();
                }
            });
        }

        public void showPlayMessageAlertDialog() {
            main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                    tempIntent.putExtra( "operation", "0" );       // 0 : play , 3 : announce message
                    tempIntent.putExtra( "type", "3" );       // 0 : play , 3 : announce message
                    main_activity.sendBroadcast( tempIntent );
                }
                public void onRecv( String data ) {
                    Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT : " + data );
                    if( data != null && data.equals( "SUCCESS" ) ) {
                        String title = "Play message";
                        String tips = "Playing announce message";
                        main_activity.yy_playing_msg_dlg = main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.play_message, tips, R.drawable.alert_dialog_ok, R.drawable.alert_delete, new YYShowAlertDialog.onAlertDialogClickHandler() {
                            public void onOK() {
                                main_activity.yy_playing_msg_dlg = null;
                                main_activity.changeShengDao( true );
                                main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                                    public void onSend() {
                                        Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                                        tempIntent.putExtra( "operation", "3" );        // 3 : stop play
                                        tempIntent.putExtra( "type", "3" );             // 3 : announce message
                                        main_activity.sendBroadcast( tempIntent );
                                    }
                                    public void onRecv( String data ) {
                                        main_activity.yy_data_source.initIsUseDefaultMessage( false );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                    public void onFailure() {
                                        main_activity.yy_data_source.initIsUseDefaultMessage( false );

                                        YYListAdapter.updateListViewTask task = new YYListAdapter.updateListViewTask();
                                        task.execute();
                                    }
                                });
                            }
                            public void onCancel() {
                                main_activity.yy_playing_msg_dlg = null;
                                main_activity.changeShengDao( true );
                                main_activity.yy_command.executeAnswerMachineCommand( YYCommand.ANSWER_MACHINE_COOM_RESULT, new YYCommand.onCommandListener() {
                                    public void onSend() {
                                        Intent tempIntent = new Intent( YYCommand.ANSWER_MACHINE_COOM );
                                        tempIntent.putExtra( "operation", "1" );       // 1 : delete , 3 : announce message
                                        tempIntent.putExtra( "type", "3" );       // 1 : play , 3 : announce message
                                        main_activity.sendBroadcast( tempIntent );
                                    }
                                    public void onRecv( String data ) {
                                        Log.v( "cconn", "ANSWER_MACHINE_COOM_RESULT : " + data );
                                        if( data != null && data.equals( "SUCCESS" ) ) {
                                            //showRecordNameAlertDialog();
                                        }
                                        else {
                                            Toast.makeText( main_activity, "delete announce message failed", Toast.LENGTH_LONG ).show();
                                        }
                                    }
                                    public void onFailure() {
                                        Toast.makeText( main_activity, "delete announce message failed", Toast.LENGTH_LONG ).show();
                                    }
                                });
                            }// End public void onCancel()
                        });
                        main_activity.changeShengDao( false );
                    }
                    else {
                        Toast.makeText( main_activity, "play announce message failed", Toast.LENGTH_LONG ).show();
                    }
                }
                public void onFailure() {
                    Toast.makeText( main_activity, "play announce message failed", Toast.LENGTH_LONG ).show();
                }
            });
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class BlockNumbersView extends YYViewBackList {
        private AddNumberView add_number_view;
        private AddContactView add_contact_view;
        private BlockkNumbers_AreaCodesView area_codes_view;
        public BlockNumbersView() {
            add_number_view = new AddNumberView();
            add_number_view.setViewType( AddNumberView.ADD_NUMBER_VIEW_TYPE_BLOCK_NUMBER );

            add_contact_view = new AddContactView();
            add_contact_view.setViewType( AddContactView.ADD_CONTACT_VIEW_TYPE_BLOCK_NUMBER );

            area_codes_view = new BlockkNumbers_AreaCodesView();
        }

        public String getViewTitle() { return "Block Numbers"; }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            // Add number
            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Add number", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) { add_number_view.setView( true, yy_view_self.getViewBackHandler() ); }
                    });
                }
            });

            ret_list_data.add( map );

            // Add contact
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Add contact", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            //if( main_activity.yy_data_source.initContactsList() ) {
                                add_contact_view.setView( true, yy_view_self.getViewBackHandler() );
                            //}
                        }
                    });
                }
            });

            ret_list_data.add( map );

            // Area codes
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Area codes", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) { area_codes_view.setView( true, yy_view_self.getViewBackHandler() ); }
                    });
                }
            });

            ret_list_data.add( map );

            // Delete all
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Delete all", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                public void onInit( AlertDialog ad, View view ) {
                                    String text1 = "Are you sure that you want to delete all the blocked numbers stored on your telephone system?";
                                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                    tv.setText( text1 );

                                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                }
                                public void onOK() {
                                }
                                public void onCancel() {
                                    main_activity.yy_data_source.onMedaProcess( YYCommand.DELETE_ALL_BLOCK_NUMBER, null, null, new YYDataSource.onMedaListener() {
                                        public void onSuccessfully() {
                                        }
                                        public void onFailure( int err_code ) {
                                            Toast.makeText( main_activity, "delete all block number failed", Toast.LENGTH_LONG ).show();
                                        }
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

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
        public class BlockkNumbers_AreaCodesView extends YYViewBackList {
            private AreaCodesFullView area_codes_full_view;
            private ViewAreaCodesView view_area_codes_view;
            public BlockkNumbers_AreaCodesView() {
                area_codes_full_view = new AreaCodesFullView();
                view_area_codes_view = new ViewAreaCodesView();
            }

            public String getViewTitle() { return "Area codes"; }

            public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
                List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

                // Add area code
                Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                    public void item_handle( Object view_obj ) {
                        Button btn_obj = (Button)view_obj;

                        btn_obj.setText( YYViewBase.transferText( "Add area code", "" ) );
                        btn_obj.setOnClickListener( new View.OnClickListener() {
                            public void onClick( View v ) {
                                main_activity.yy_input_number_view.showInputNumberView( "Add area code", "", 2, 8, yy_view_self.getViewBackHandler(), new YYInputNumberView.onYYInputNumberHandler() {
                                    public void onSave( String number ) {
                                        final String new_area_code = number;
                                        main_activity.yy_data_source.onMedaProcess( YYCommand.ADD_NEW_AREA_CODE, number, null, new YYDataSource.onMedaListener() {
                                            public void onSuccessfully() {
                                                String title = "Successfully added to the\r\n BLOCKED list";
                                                String tips = "Press OK to finish";
                                                int nDrawableResID = R.drawable.successfully;
                                                int nOKResID = R.drawable.alert_dialog_ok;
                                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, nDrawableResID, tips, nOKResID, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                                    public void onOK() { }
                                                    public void onCancel() { }
                                                });
                                            }
                                            public void onFailure( int err_code ) {
                                                if( err_code == 2 ) {
                                                    String text = String.format( "Area code %s has already been saved", new_area_code );
                                                    Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                                } else if ( err_code == 3 ) {
                                                    //String text = String.format( "Already blocked by area code %s", new_area_code );
                                                    String text = String.format( "%s already blocked by other area code", new_area_code );
                                                    Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                                } else if ( err_code == 4 ) {
                                                    main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                                        public void onInit( AlertDialog ad, View view ) {
                                                            TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                                            tv.setText( "The area codes list is full.Please\r\npress OK to select an area code in\r\nthe list to replace or CANCEL to go\r\nback." );

                                                            // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                                            ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                                            btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                                            ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                                            btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                                        }
                                                        public void onOK() { }
                                                        public void onCancel() {
                                                            area_codes_full_view.add_area_code = new_area_code;
                                                            area_codes_full_view.setView( true, main_activity.yy_input_number_view.getViewBackHandler() );
                                                        }
                                                    });
                                                } else {
                                                    Toast.makeText( main_activity, "add area code failed", Toast.LENGTH_LONG ).show();
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

                ret_list_data.add( map );

                // View area codes
                map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                    public void item_handle( Object view_obj ) {
                        Button btn_obj = (Button)view_obj;

                        btn_obj.setText( YYViewBase.transferText( "View area codes", "" ) );
                        btn_obj.setOnClickListener( new View.OnClickListener() {
                            public void onClick( View v ) { view_area_codes_view.setView( true, yy_view_self.getViewBackHandler() ); }
                        });
                    }
                });

                ret_list_data.add( map );

                return ret_list_data;
            }

            public class AreaCodesFullView extends YYViewBackList {
                public List<String> area_codes_list = new ArrayList<String>();
                public String add_area_code;
                public String getViewTitle() { return "Add area code"; }

                public void setView( boolean bIsPush, onViewBackHandler handler ) {
                    super.setView( bIsPush, handler );

                    // 请求 area code list
                    main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GACN_RESULT, new YYCommand.onCommandListener() {
                        public void onSend() {
                            main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_GACN ) );
                        }
                        public void onRecv( String data ) {
                            if( data == null ) {
                                String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GACN_RESULT );
                                Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                            }
                            else {
                                String[] results = data.split( "," );

                                area_codes_list.clear();
                                for( int i=0; i < results.length; ++i ) {
                                    area_codes_list.add( results[i] );
                                }

                                fillListView();

                                area_codes_list.clear();
                            }
                        }
                        public void onFailure() {
                            Toast.makeText( main_activity, "get area code list failed", Toast.LENGTH_LONG ).show();
                        }
                    });
                }

                public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
                    List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

                    for( int i=0; i < area_codes_list.size(); ++i ) {
                        final String area_code = area_codes_list.get( i );

                        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                            public void item_handle( Object view_obj ) {
                                Button btn_obj = (Button)view_obj;

                                btn_obj.setText( YYViewBase.transferText( area_code, "" ) );
                                btn_obj.setOnClickListener( new View.OnClickListener() {
                                    public void onClick( View v ) {
                                        main_activity.yy_data_source.onMedaProcess( YYCommand.EDIT_ONE_AREA_CODE, add_area_code, area_code, new YYDataSource.onMedaListener() {
                                            public void onSuccessfully() {
                                                YYViewBase.onBackClick();

                                                String title = "Successfully added to the\r\n BLOCKED list";
                                                String tips = "Press OK to finish";
                                                int nDrawableResID = R.drawable.successfully;
                                                int nOKResID = R.drawable.alert_dialog_ok;
                                                main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, nDrawableResID, tips, nOKResID, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                                    public void onOK() { }
                                                    public void onCancel() { }
                                                });
                                            }
                                            public void onFailure( int err_code ) {
                                                Toast.makeText( main_activity, "edit area code failed", Toast.LENGTH_LONG ).show();
                                            }
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

            public class ViewAreaCodesView extends YYViewBackList {
                public List<String> area_codes_list = new ArrayList<String>();
                public AreaCodeItemView area_code_item_view;
                public ViewAreaCodesView() {
                    area_code_item_view = new AreaCodeItemView();
                }

                public String getViewTitle() { return "View area codes"; }

                public void setView( boolean bIsPush, onViewBackHandler handler ) {
                    super.setView( bIsPush, handler );

                    // 请求 area code list
                    main_activity.yy_schedule.scheduleOnceTime( 100, new YYSchedule.onScheduleAction() {
                        public void doSomething() {
                            main_activity.yy_command.executeSettingsBaseCommand( YYCommand.CALL_GUARDIAN_GACN_RESULT, new YYCommand.onCommandListener() {
                                public void onSend() {
                                    main_activity.sendBroadcast( new Intent( YYCommand.CALL_GUARDIAN_GACN ) );
                                }
                                public void onRecv( String data ) {
                                    if( data == null ) {
                                        String text = String.format( "%s recv : null", YYCommand.CALL_GUARDIAN_GACN_RESULT );
                                        Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                    }
                                    else {
                                        String[] results = data.split( "," );

                                        area_codes_list.clear();
                                        for( int i=0; i < results.length; ++i ) {
                                            area_codes_list.add( results[i] );
                                        }

                                        fillListView();

                                        area_codes_list.clear();
                                    }
                                }
                                public void onFailure() {
                                    Toast.makeText( main_activity, "get area code list failed", Toast.LENGTH_LONG ).show();
                                }
                            });
                        }
                    });
                }
                public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
                    List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

                    for( int i=0; i < area_codes_list.size(); ++i ) {
                        final String area_code = area_codes_list.get( i );

                        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                            public void item_handle( Object view_obj ) {
                                Button btn_obj = (Button)view_obj;

                                btn_obj.setText( YYViewBase.transferText( area_code, "" ) );
                                btn_obj.setOnClickListener( new View.OnClickListener() {
                                    public void onClick( View v ) {
                                        area_code_item_view.yy_temp_data.put( "area_code", area_code );
                                        area_code_item_view.setView( true, yy_view_self.getViewBackHandler() );
                                    }
                                });
                            }
                        });

                        ret_list_data.add( map );
                    }

                    return ret_list_data;
                }

                public class AreaCodeItemView extends YYViewBackList {
                    public String getViewTitle() { return "View area codes"; }

                    public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
                        List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

                        // Edit area code
                        Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                            public void item_handle( Object view_obj ) {
                                Button btn_obj = (Button)view_obj;

                                btn_obj.setText( YYViewBase.transferText( "Edit area code", "" ) );
                                btn_obj.setOnClickListener( new View.OnClickListener() {
                                    public void onClick( View v ) {
                                        final String area_code = (String)yy_view_self.yy_temp_data.get( "area_code" );
                                        main_activity.yy_input_number_view.showInputNumberView( "Edit area code", area_code, -1, 8, yy_view_self.getViewBackHandler(), new YYInputNumberView.onYYInputNumberHandler() {
                                            public void onSave( String number ) {
                                                final String new_area_code = number;
                                                main_activity.yy_data_source.onMedaProcess( YYCommand.EDIT_ONE_AREA_CODE, number, area_code, new YYDataSource.onMedaListener() {
                                                    public void onSuccessfully() {
                                                        String title = "Successfully added to the\r\n BLOCKED list";
                                                        String tips = "Press OK to finish";
                                                        int nDrawableResID = R.drawable.successfully;
                                                        int nOKResID = R.drawable.alert_dialog_ok;
                                                        main_activity.yy_show_alert_dialog.showSuccessfullImageTipsAlertDialog( title, nDrawableResID, tips, nOKResID, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                                            public void onOK() {
                                                                YYViewBase.onBackClick();
                                                                YYViewBase.onBackClick();
                                                            }
                                                            public void onCancel() { }
                                                        });
                                                    }
                                                    public void onFailure( int err_code ) {
                                                        if( err_code == 2 ) {
                                                            String text = String.format( "Area code %s has already been saved", new_area_code );
                                                            Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                                        } else if ( err_code == 3 ) {
                                                            //String text = String.format( "Already blocked by area code %s", new_area_code );
                                                            String text = String.format( "%s already blocked by other area code", new_area_code );
                                                            Toast.makeText( main_activity, text, Toast.LENGTH_LONG ).show();
                                                        } else if ( err_code == 4 ) {
                                                            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                                                public void onInit( AlertDialog ad, View view ) {
                                                                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                                                    tv.setText( "The area codes list is full.Please\r\npress OK to select an area code in\r\nthe list to replace or CANCEL to go\r\nback." );

                                                                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                                                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                                                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                                                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                                                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                                                }
                                                                public void onOK() { }
                                                                public void onCancel() {
                                                                    area_codes_full_view.add_area_code = new_area_code;
                                                                    area_codes_full_view.setView( true, main_activity.yy_input_number_view.getViewBackHandler() );
                                                                }
                                                            });
                                                        } else {
                                                            Toast.makeText( main_activity, "add area code failed", Toast.LENGTH_LONG ).show();
                                                        }
                                                    }
                                                });
                                            }
                                        });
                                    }
                                });
                            }
                        });

                        ret_list_data.add( map );

                        // Delete area code
                        map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
                        map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                            public void item_handle( Object view_obj ) {
                                Button btn_obj = (Button)view_obj;

                                btn_obj.setText( YYViewBase.transferText( "Delete area code", "" ) );
                                btn_obj.setOnClickListener( new View.OnClickListener() {
                                    public void onClick( View v ) {
                                        final String area_code = (String)yy_view_self.yy_temp_data.get( "area_code" );
                                        main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                            public void onInit( AlertDialog ad, View view ) {
                                                String text1 = String.format( "Are you sure that you want to delete the area code %s from your list?", area_code );
                                                TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                                tv.setText( text1 );

                                                // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                                ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                                btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                                ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                                btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                            }
                                            public void onOK() { }
                                            public void onCancel() {
                                                main_activity.yy_data_source.onMedaProcess( YYCommand.DELETE_ONE_AREA_CODE, null, area_code, new YYDataSource.onMedaListener() {
                                                    public void onSuccessfully() {
                                                        YYViewBase.onBackClick();
                                                    }
                                                    public void onFailure( int err_code ) {
                                                        Toast.makeText( main_activity, "delete area code failed", Toast.LENGTH_LONG ).show();
                                                    }
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
                }   // end public class AreaCodeItemView extends YYViewBackList
            }
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class AllowNumbersView extends YYViewBackList {
        private AddNumberView add_number_view;
        private AddContactView add_contact_view;
        public AllowNumbersView() {
            add_number_view = new AddNumberView();
            add_number_view.setViewType( AddNumberView.ADD_NUMBER_VIEW_TYPE_ALLOW_NUMBER );

            add_contact_view = new AddContactView();
            add_contact_view.setViewType( AddContactView.ADD_CONTACT_VIEW_TYPE_ALLOW_NUMBER );
        }

        public String getViewTitle() {
            return "Allow Numbers";
        }

        public List<Map<Integer,YYListAdapter.onYYListItemHandler>> getItemListData() {
            List<Map<Integer,YYListAdapter.onYYListItemHandler>> ret_list_data = new ArrayList<Map<Integer,YYListAdapter.onYYListItemHandler>>();

            // Add number
            Map<Integer,YYListAdapter.onYYListItemHandler> map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Add number", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) { add_number_view.setView( true, yy_view_self.getViewBackHandler() ); }
                    });
                }
            });

            ret_list_data.add( map );

            // Add contact
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Add contact", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            //if( main_activity.yy_data_source.initContactsList() ) {
                                add_contact_view.setView( true, yy_view_self.getViewBackHandler() );
                            //}
                        }
                    });
                }
            });

            ret_list_data.add( map );

            // Delete all
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Delete all", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                public void onInit( AlertDialog ad, View view ) {
                                    String text1 = "Are you sure that you want to delete all the allowed numbers stored on your telephone system?";
                                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                    tv.setText( text1 );

                                    // 又是 OK 当 CANCEL 用，CANCEL 当 OK 用
                                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_cancel ) );

                                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                }
                                public void onOK() { }
                                public void onCancel() {
                                    main_activity.yy_data_source.onMedaProcess( YYCommand.DELETE_ALL_ALLOW_NUMBER, null, null, new YYDataSource.onMedaListener() {
                                        public void onSuccessfully() {
                                        }
                                        public void onFailure( int err_code ) {
                                            Toast.makeText( main_activity, "delete all allow number failed", Toast.LENGTH_LONG ).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                }
            });

            ret_list_data.add( map );

            // Synchronise contacts to base
            map = new HashMap<Integer,YYListAdapter.onYYListItemHandler>();
            map.put( R.id.item_button, new YYListAdapter.onYYListItemHandler() {
                public void item_handle( Object view_obj ) {
                    Button btn_obj = (Button)view_obj;

                    btn_obj.setText( YYViewBase.transferText( "Synchronise contacts to base", "" ) );
                    btn_obj.setOnClickListener( new View.OnClickListener() {
                        public void onClick( View v ) {
                            String title = "Contact sync with base?";
                            String tips = "This could take up to ten minutes";
                            main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.sync_base, tips, R.drawable.alert_yes, R.drawable.alert_no, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                public void onCancel() { }
                                public void onOK() {
                                    String title = "Contacts synchronising";
                                    String tips = "Busy\r\nSynchronisation in progress";
                                    main_activity.yy_show_alert_dialog.showImageTipsAlertDialog( title, R.drawable.synchronising, tips, 0, R.drawable.alert_cancel, new YYShowAlertDialog.onAlertDialogClickHandler() {
                                        public void onOK() { }
                                        public void onCancel() {
                                            main_activity.yy_show_alert_dialog.showAlertDialog( R.layout.alert_attention, new YYShowAlertDialog.onAlertDialogHandler() {
                                                public void onInit( AlertDialog ad, View view ) {
                                                    String text1 = "If you cancel this synchronisation new.your contacts will need to announce their names when they call.Are you sure?";
                                                    TextView tv = (TextView)view.findViewById( R.id.attention_text );
                                                    tv.setText( text1 );

                                                    ImageButton btn_ok = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_OK );
                                                    btn_ok.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_back ) );

                                                    ImageButton btn_cancel = (ImageButton)view.findViewById( R.id.ALERT_DIALOG_CANCEL );
                                                    btn_cancel.setImageDrawable( main_activity.getResources().getDrawable( R.drawable.alert_attention_ok ) );
                                                }
                                                public void onOK() { }
                                                public void onCancel() {
                                                    main_activity.yy_show_alert_dialog.bShowWaiting = true;
                                                    main_activity.yy_command.unregisterReceiver();
                                                    main_activity.yy_command.cur_command_info = null;
                                                    main_activity.yy_command.request_command_list.clear();
                                                }
                                            });
                                        }
                                    });

                                    main_activity.bContactSynchronising = true;

                                    main_activity.yy_show_alert_dialog.bShowWaiting = false;
                                    main_activity.yy_data_source.syncContactToBase( new YYDataSource.syncLisenter() {
                                        public void onSuccessfully() {
                                            main_activity.bContactSynchronising = false;

                                            main_activity.yy_show_alert_dialog.hideAlertDialog();
                                            main_activity.yy_show_alert_dialog.bShowWaiting = true;

                                            main_activity.yy_data_source.syncContactSuccessfully();
                                        }
                                        public void onFailure() {
                                            main_activity.bContactSynchronising = false;

                                            main_activity.yy_show_alert_dialog.hideAlertDialog();
                                            main_activity.yy_show_alert_dialog.bShowWaiting = true;

                                            main_activity.yy_data_source.syncContactFailure();
                                        }
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

}
