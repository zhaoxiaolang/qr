package com.microstorm.qrcode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by jinzlin on 17/5/19.
 * 广播管理类
 */

public class BroadcastManager {

    private Context mContext;
    private static BroadcastManager instance;
    private Map<String, BroadcastReceiver> receiverMap;

    private BroadcastManager(Context context) {
        this.mContext = context.getApplicationContext();
        receiverMap = new HashMap<String, BroadcastReceiver>();
    }

    public static BroadcastManager getInstance(Context context) {
        if (instance == null) {
            instance = new BroadcastManager(context);
        }
        return instance;
    }

    /**
     * 添加广播
     *
     * @param
     */
    public void addAction(String[] action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            for (int i = 0; i < action.length; i++) {
                filter.addAction(action[i]);
                receiverMap.put(action[i], receiver);
            }
            mContext.registerReceiver(receiver, filter);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * 添加广播
     *
     * @param
     */
    public void addAction(String action, BroadcastReceiver receiver) {
        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(action);
            receiverMap.put(action, receiver);
            mContext.registerReceiver(receiver, filter);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送参数为 String 的数据广播
     *
     * @param action
     * @param s
     */
    public void sendBroadcast(String action, String s) {
        Intent intent = new Intent();
        intent.setAction(action);
        intent.putExtra("data", s);
        mContext.sendBroadcast(intent);
    }

    /**
     * 销毁广播
     *
     * @param action
     */
    public void destroy(String... action) {
        try {
            if (receiverMap != null) {
                boolean flag = false ;
//                Iterator<Map.Entry<String, BroadcastReceiver>> it = receiverMap.entrySet().iterator();
//                while(it.hasNext()){
//                    Map.Entry<String, BroadcastReceiver> entry = it.next();
//                    if(entry.getKey() == 2)
//                        it.remove();//使用迭代器的remove()方法删除元素
//                }

                for (int i = 0; i < action.length; i++) {
                    if (receiverMap.get(action[i]) != null) {
                        BroadcastReceiver receiver = receiverMap.remove(action[i]);
                        if (receiver != null) {
                            if (!flag) {
                                mContext.unregisterReceiver(receiver);
                            }
                            flag = true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
