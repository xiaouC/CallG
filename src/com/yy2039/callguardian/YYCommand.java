package com.yy2039.callguardian_1;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;
import android.util.Log;

public class YYCommand {
    public CallGuardianActivity main_activity;

	// Result Final
	public final static String RESULT_LINK = "LINK";
	public final static String RESULT_NOLINK = "NOLINK";
	public final static String RESULT_SUCCESS = "SUCCESS";
	public final static String RESULT_FAIL = "FAIL";
	public final static String RESULT_EQUAL = "EQUAL";
	public final static String RESULT_NOEQUAL = "NOEQUAL";

    // base 连接/断开
	public final static String SETTINGS_BTCL = "andorid.intent.action.settings.btcl";// base连接
	public final static String SETTINGS_BASE_BTCL_RESULT = "com.action.dect.settings.base.btcl.result";

	public final static String SETTINGS_BTCR = "andorid.intent.action.settings.btcr";// base断开
	public final static String SETTINGS_BASE_BTCR_RESULT = "com.action.dect.settings.base.btcr.result";

    // 黑白名单
	public final static String CALL_GUARDIAN_BANB = "andorid.intent.action.call.guardian.banb";
	public final static String CALL_GUARDIAN_BANB_RESULT = "com.action.dect.call.guardian.banb.result";

    // 获取 BT Call Guardian 设置
	public final static String CALL_GUARDIAN_GCCS = "andorid.intent.action.call.guardian.gccs";
	public final static String CALL_GUARDIAN_GCCS_RESULT = "com.action.dect.call.guardian.gccs.result";

    // 更新 BT Call Guardian 设置
	public final static String CALL_GUARDIAN_SCCS = "andorid.intent.action.call.guardian.sccs";
	public final static String CALL_GUARDIAN_SCCS_RESULT = "com.action.dect.call.guardian.sccs.result";

    // calls list 连接/断开
	public final static String CALL_LIST_BTCL = "andorid.intent.action.call.list.btcl";
	public final static String CALL_LIST_BTCL_RESULT = "com.action.dect.call.list.btcl.result";

	public final static String CALL_LIST_BTCR = "andorid.intent.action.call.list.btcr";
	public final static String CALL_LIST_BTCR_RESULT = "com.action.dect.call.list.btcr.result";

    public final static String CALL_LIST_GTCL = "andorid.intent.action.call.list.gtcl";
    public final static String CALL_LIST_GTCL_RESULT = "com.action.dect.call.list.gtcl.result";

    // <type>,<new number and old number>
	// <type> : 0 add new area code. 1 edit one area code. 2 delete one area code. 3 delete all area code. 4 delete all block number. 5 delete all allow number.
	// <new number and old number> : 8 byte number. for example:	AT+MDEA=0,33AAAAAAAAAAAAAA
    public final static String CALL_GUARDIAN_MDEA="andorid.intent.action.call.guardian.mdea";
    public final static String CALL_GUARDIAN_MDEA_RESULT = "com.action.dect.call.guardian.mdea.result";

	public final static String CALL_GUARDIAN_GACN="andorid.intent.action.call.guardian.gacn";
	public final static String CALL_GUARDIAN_GACN_RESULT = "com.action.dect.call.guardian.gacn.result";

    public final static int ADD_NEW_AREA_CODE = 0;
    public final static int EDIT_ONE_AREA_CODE = 1;
    public final static int DELETE_ONE_AREA_CODE = 2;
    public final static int DELETE_ALL_AREA_CODE = 3;
    public final static int DELETE_ALL_BLOCK_NUMBER = 4;
    public final static int DELETE_ALL_ALLOW_NUMBER = 5;

    // 

    public final static String ANSWER_MACHINE_BTCL = "andorid.intent.action.answer.machine.btcl";
    public final static String ANSWER_MACHINE_BTCL_RESULT = "com.action.dect.answer.machine.btcl.result";

	public final static String ANSWER_MACHINE_BTCR = "andorid.intent.action.answer.machine.btcr";
	public final static String ANSWER_MACHINE_BTCR_RESULT = "com.action.dect.answer.machine.btcr.result";

    public final static String ANSWER_MACHINE_COOM = "andorid.intent.action.answer.machine.coom";
    public final static String ANSWER_MACHINE_COOM_RESULT = "com.action.dect.answer.machine.coom.result";

    // 比较 pin 是否正确
    public final static String CALL_GUARDIAN_CMPC = "android.intent.action.call.guardian.cmpc";
    public final static String CALL_GUARDIAN_CMPC_RESULT = "com.action.dect.call.guardian.cmpc.result";

    // new pin
    public final static String CALL_GUARDIAN_SCCP = "android.intent.action.call.guardian.sccp";
    public final static String CALL_GUARDIAN_SCCP_RESULT = "com.action.dect.call.guardian.sccp.result";

    private boolean settings_base_link = false;
    private boolean call_list_link = false;
    private boolean answer_machine_link = false;

    // 
    public YYCommand( CallGuardianActivity activity ) {
        main_activity = activity;

        init();
    }

    public interface onCommandListener {
        void onSend();
        void onRecv( String data );
        void onFailure();
    }

    public class CommandInfo {
        public String command_name;
        public onCommandListener command_listener;
        public CommandInfo( String cmd, onCommandListener cmdListener ) {
            command_name = cmd;
            command_listener = cmdListener;
        }
    }

    public interface onRecvActionListener {
        void onExecute( String data );
    }
    public Map<String,onRecvActionListener> action_list = new HashMap<String,onRecvActionListener>();

	public BroadcastReceiver commandReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			String data = intent.getExtras().getString("data");

            Log.v( "cconn", "BroadcastReceiver ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
            Log.v( "cconn", "action : " + action );
            Log.v( "cconn", "data : " + data );
            Log.v( "cconn", "BroadcastReceiver ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
            Log.v( "prot", "BroadcastReceiver ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );
            Log.v( "prot", "action : " + action );
            Log.v( "prot", "data : " + data );
            Log.v( "prot", "BroadcastReceiver ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" );

            if( cur_command_info != null ) {
                if( cur_command_info.command_name.equals( action ) ) {
                    onRecvActionListener ral = action_list.get( action );
                    ral.onExecute( data );
                }
            }
            else {
                onRecvActionListener ral = action_list.get( action );
                ral.onExecute( data );
            }
        }
    };

    public List<CommandInfo> request_command_list = new ArrayList<CommandInfo>();
    public void executeCommand( String cmd, onCommandListener listener ) {
        CommandInfo cmd_info = new CommandInfo( cmd, listener );
        request_command_list.add( cmd_info );

        realExecuteCommand();
    }

    public CommandInfo cur_command_info;
    public void realExecuteCommand() {
        if( cur_command_info != null ) {
            return;
        }

        if( request_command_list.size() > 0 ) {
            cur_command_info = request_command_list.get( 0 );
            request_command_list.remove( 0 );

            cur_command_info.command_listener.onSend();
        }
    }

    public void addDefaultProcessAction( String action_name, final String disconnection_name ) {
        action_list.put( action_name, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( disconnection_name != null ) {
                    main_activity.sendBroadcast( new Intent( disconnection_name ) );
                }

                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }
            }
        });
    }

    public void init() {
        // 建立连接
        action_list.put( SETTINGS_BASE_BTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
				if( data.equals( RESULT_LINK ) ) {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onRecv( data );
                    }
                }
                else {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onFailure();
                    }
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 断开连接，当断开之后，马上执行下一个请求
        action_list.put( SETTINGS_BASE_BTCR_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                Log.v( "cconn", "recv SETTINGS_BASE_BTCR_RESULT" );
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // BT Call Guardian 获取设置
        action_list.put( CALL_GUARDIAN_GCCS_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // BT Call Guardian 更新设置
        action_list.put( CALL_GUARDIAN_SCCS_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 设置 白/黑 名单
        action_list.put( CALL_GUARDIAN_BANB_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // calls list 连接
        action_list.put( CALL_LIST_BTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
				if( data.equals( RESULT_LINK ) ) {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onRecv( data );
                    }
                }
                else {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onFailure();
                    }
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 断开 calls list 连接
        action_list.put( CALL_LIST_BTCR_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 获取 calls list
        action_list.put( CALL_LIST_GTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                Log.v( "cconn", "CALL_LIST_GTCL_RESULT : 1111111111111111111111111" + data );
                if( cur_command_info != null ) {
                    Log.v( "cconn", "CALL_LIST_GTCL_RESULT : 222222222222222222222222222222222222" );
                    cur_command_info.command_listener.onRecv( data );
                }
                Log.v( "cconn", "CALL_LIST_GTCL_RESULT : 3333333333333333333333333333333333333333" );

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 添加、编辑、删除 area code; 删除所有的area code、删除所有的 block number、删除所有的 allow number
        action_list.put( CALL_GUARDIAN_MDEA_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 获取 area code list
        action_list.put( CALL_GUARDIAN_GACN_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 比较 pin
        action_list.put( CALL_GUARDIAN_CMPC_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 修改 pin
        action_list.put( CALL_GUARDIAN_SCCP_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });

        action_list.put( ANSWER_MACHINE_BTCL_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
				if( data.equals( RESULT_LINK ) ) {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onRecv( data );
                    }
                }
                else {
                    if( cur_command_info != null ) {
                        cur_command_info.command_listener.onFailure();
                    }
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        // 断开连接，当断开之后，马上执行下一个请求
        action_list.put( ANSWER_MACHINE_BTCR_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });
        action_list.put( ANSWER_MACHINE_COOM_RESULT, new onRecvActionListener() {
            public void onExecute( String data ) {
                if( cur_command_info != null ) {
                    cur_command_info.command_listener.onRecv( data );
                }

                cur_command_info = null;
                realExecuteCommand();
            }
        });

        // 注册
        IntentFilter filter = new IntentFilter();
        for( Map.Entry<String,onRecvActionListener> entry : action_list.entrySet() ) {
            String action_name = entry.getKey();
            filter.addAction( action_name );
        }
		main_activity.registerReceiver( commandReceiver, filter );
    }

    public interface onConnLisenter {
        void onSuccessfully();
        void onFailure();
    }

    public void executeSettingsBaseCommand( final String cmd_result, final onCommandListener cmd_listener ) {
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        disconnectAllLink( new onConnLisenter() {
            public void onSuccessfully() {
                connectSettingsBase( new onConnLisenter() {
                    public void onSuccessfully() {
                        executeCommand( cmd_result, new onCommandListener() {
                            public void onSend() { cmd_listener.onSend(); }
                            public void onRecv( String data ) {
                                Log.v( "cconn", "recv data : " + data );
                                final String recv_data = data;
                                // 处理完后，马上断开
                                disconnectSettingsBase( new onConnLisenter() {
                                    public void onSuccessfully() {
                                        cmd_listener.onRecv( recv_data );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                    public void onFailure() {
                                        cmd_listener.onRecv( recv_data );
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                });
                            }
                            public void onFailure() {
                                // 处理完后，马上断开
                                disconnectSettingsBase( new onConnLisenter() {
                                    public void onSuccessfully() {
                                        cmd_listener.onFailure();
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                    public void onFailure() {
                                        cmd_listener.onFailure();
                                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                    }
                                });
                            }
                        });
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "settings base link failed!", Toast.LENGTH_LONG ).show();
                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                    }
                });
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                Toast.makeText( main_activity, "disconnect link failed!", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void executeCallListCommand( final String cmd_result, final onCommandListener cmd_listener ) {
        //cmd_listener.onFailure();
        Log.v( "cconn", "execute call list cmd begin 11 " );
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        disconnectAllLink( new onConnLisenter() {
            public void onSuccessfully() {
                connectCallList( new onConnLisenter() {
                    public void onSuccessfully() {
                        Log.v( "cconn", "execute call list cmd begin 22" );
                        executeCommand( cmd_result, new onCommandListener() {
                            public void onSend() { cmd_listener.onSend(); }
                            public void onRecv( String data ) {
                                Log.v( "cconn", "execute call list cmd success" );
                                cmd_listener.onRecv( data );
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //final String recv_data = data;
                                //disconnectCallList( new onConnLisenter() {
                                //    public void onSuccessfully() {
                                //        cmd_listener.onRecv( recv_data );
                                //        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //    }
                                //    public void onFailure() {
                                //        cmd_listener.onRecv( recv_data );
                                //        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //    }
                                //});
                                call_list_link = false;
                            }
                            public void onFailure() {
                                Log.v( "cconn", "execute call list cmd failed" );
                                cmd_listener.onFailure();
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //disconnectCallList( new onConnLisenter() {
                                //    public void onSuccessfully() {
                                //        cmd_listener.onFailure();
                                //        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //    }
                                //    public void onFailure() {
                                //        cmd_listener.onFailure();
                                //        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                                //    }
                                //});
                                call_list_link = false;
                            }
                        });
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "call list link failed!", Toast.LENGTH_LONG ).show();
                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                    }
                });
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                Toast.makeText( main_activity, "disconnect link failed!", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void executeAnswerMachineCommand( final String cmd_result, final onCommandListener cmd_listener ) {
        main_activity.yy_show_alert_dialog.showWaitingAlertDialog();
        disconnectAllLink( new onConnLisenter() {
            public void onSuccessfully() {
                connectAnswerMachine( new onConnLisenter() {
                    public void onSuccessfully() {
                        executeCommand( cmd_result, new onCommandListener() {
                            public void onSend() { cmd_listener.onSend(); }
                            public void onRecv( String data ) {
                                cmd_listener.onRecv( data );
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                            }
                            public void onFailure() {
                                cmd_listener.onFailure();
                                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                            }
                        });
                    }
                    public void onFailure() {
                        Toast.makeText( main_activity, "answer machine link failed!", Toast.LENGTH_LONG ).show();
                        main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                    }
                });
            }
            public void onFailure() {
                main_activity.yy_show_alert_dialog.hideWaitingAlertDialog();
                Toast.makeText( main_activity, "disconnect link failed!", Toast.LENGTH_LONG ).show();
            }
        });
    }

    public void connectSettingsBase( final onConnLisenter conn_lisenter ) {
        Log.v( "cconn", "connect settings base begin" );
        executeCommand( YYCommand.SETTINGS_BASE_BTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( SETTINGS_BTCL ) );
                Log.v( "cconn", "connect settings base send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "connect settings base success" );

                settings_base_link = true;
                conn_lisenter.onSuccessfully();
            }
            public void onFailure() {
                Log.v( "cconn", "connect settings base failed" );

                conn_lisenter.onFailure();
            }
        });
    }

    public void connectCallList( final onConnLisenter conn_lisenter ) {
        Log.v( "cconn", "connect call list begin" );
        executeCommand( YYCommand.CALL_LIST_BTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( CALL_LIST_BTCL ) );
                Log.v( "cconn", "connect call list send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "connect call list success" );

                call_list_link = true;
                conn_lisenter.onSuccessfully();
            }
            public void onFailure() {
                Log.v( "cconn", "connect call list failed" );

                conn_lisenter.onFailure();
            }
        });
    }

    public void connectAnswerMachine( final onConnLisenter conn_lisenter ) {
        Log.v( "cconn", "connect answer machine begin" );
        executeCommand( YYCommand.ANSWER_MACHINE_BTCL_RESULT, new YYCommand.onCommandListener() {
            public void onSend() {
                main_activity.sendBroadcast( new Intent( ANSWER_MACHINE_BTCL ) );
                Log.v( "cconn", "connect answer machine send" );
            }
            public void onRecv( String data ) {
                Log.v( "cconn", "connect answer machine success" );

                answer_machine_link = true;
                conn_lisenter.onSuccessfully();
            }
            public void onFailure() {
                Log.v( "cconn", "connect answer machine failed" );

                conn_lisenter.onFailure();
            }
        });
    }

    public void disconnectAllLink( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnectAllLink begin" );
        disconnectSettingsBase( new onConnLisenter() {
            public void onSuccessfully() {
                disconnectCallList( new onConnLisenter() {
                    public void onSuccessfully() {
                        disconnectAnswerMachine( new onConnLisenter() {
                            public void onSuccessfully() {
                                disconnect_lisenter.onSuccessfully();
                            }
                            public void onFailure() { }
                        });
                    }
                    public void onFailure() { }
                });
            }
            public void onFailure() { }
        });
    }

    public void disconnectSettingsBase( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnect settings base" );
        if( settings_base_link ) {
            executeCommand( YYCommand.SETTINGS_BASE_BTCR_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Log.v( "cconn", "disconnect settings base begin" );
                    main_activity.sendBroadcast( new Intent( SETTINGS_BTCR ) );
                }
                public void onRecv( String data ) {
                    Log.v( "cconn", "disconnect settings base success" );
                    settings_base_link = false;
                    disconnect_lisenter.onSuccessfully();
                }
                public void onFailure() {
                    Log.v( "cconn", "disconnect settings base failed" );
                    Toast.makeText( main_activity, "disconnect settings base link failed!", Toast.LENGTH_LONG ).show();
                }
            });
        }
        else {
            disconnect_lisenter.onSuccessfully();
        }
    }

    public void disconnectCallList( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnect call list" );
        if( call_list_link ) {
            executeCommand( YYCommand.CALL_LIST_BTCR_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Log.v( "cconn", "disconnect call list begin" );
                    main_activity.sendBroadcast( new Intent( CALL_LIST_BTCR ) );
                }
                public void onRecv( String data ) {
                    Log.v( "cconn", "disconnect call list success" );
                    call_list_link = false;
                    disconnect_lisenter.onSuccessfully();
                }
                public void onFailure() {
                    Log.v( "cconn", "disconnect call list failed" );
                    Toast.makeText( main_activity, "disconnect call list link failed!", Toast.LENGTH_LONG ).show();
                }
            });
        }
        else {
            disconnect_lisenter.onSuccessfully();
        }
    }

    public void disconnectAnswerMachine( final onConnLisenter disconnect_lisenter ) {
        Log.v( "cconn", "disconnect answer machine" );
        if( answer_machine_link ) {
            executeCommand( YYCommand.ANSWER_MACHINE_BTCR_RESULT, new YYCommand.onCommandListener() {
                public void onSend() {
                    Log.v( "cconn", "disconnect answer machine begin" );
                    main_activity.sendBroadcast( new Intent( ANSWER_MACHINE_BTCR ) );
                }
                public void onRecv( String data ) {
                    Log.v( "cconn", "disconnect answer machine success" );
                    answer_machine_link = false;
                    disconnect_lisenter.onSuccessfully();
                }
                public void onFailure() {
                    Log.v( "cconn", "disconnect answer machine failed" );
                    Toast.makeText( main_activity, "disconnect answer machine link failed!", Toast.LENGTH_LONG ).show();
                }
            });
        }
        else {
            disconnect_lisenter.onSuccessfully();
        }
    }
}
