package com.hy.app.common

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.IBinder
import android.text.TextUtils

import com.hy.app.R
import com.hy.frame.bean.ResultInfo
import com.hy.frame.util.MyLog
import com.hy.frame.util.MyToast
import com.hy.http.IMyHttpListener

//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttClient;
//import org.eclipse.paho.client.mqttv3.MqttException;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;


/**
 * @author HeYan
 * @title
 * @time 2015/11/26 13:13
 */
class AppService : Service(), Runnable, IMyHttpListener {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {}

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        //initPush(intent.getStringExtra(Constant.FLAG));
        return super.onStartCommand(intent, flags, startId)
    }

    private var curName: String? = null

    private fun initPush(name: String) {
        curName = name
        MyLog.d("process: initPush")
        val filter = IntentFilter()
        filter.addAction(ACTION_PUSH_SEND)
        filter.addAction(ACTION_PUSH_LOST)
        filter.addAction(ACTION_PUSH)
        filter.priority = IntentFilter.SYSTEM_LOW_PRIORITY
        registerReceiver(receiver, filter)
        //        if (mqttClient == null) {
        //            try {
        //                String url = getString(R.string.API_MQTT);
        //                mqttClient = new MqttClient(url, name, new MemoryPersistence());
        //                mqttClient.setCallback(new MyMqttCallback(this));
        //            } catch (MqttException e) {
        //                MyLog.INSTANCE.e("Something went wrong!" + e.getMessage());
        //            } catch (Exception e) {
        //                MyLog.INSTANCE.e("2.Something went wrong!" + e.getMessage());
        //            }
        //            connectMqtt();
        //        }
        if (handler == null) {
            handler = android.os.Handler()
            run()
        }
    }

    private fun connectMqtt() {
        //        if (null != mqttClient && !mqttClient.isConnected()) {
        //            try {
        //                //MqttConnectOptions options = new MqttConnectOptions();
        //                //options.setUserName(token);
        //                //options.setPassword(token.toCharArray());
        //                mqttClient.connect();
        //                mqttClient.subscribe(curName);
        //                //mqttClient.subscribe(ComUtil.getToken());
        //            } catch (MqttException e) {
        //                //MyToast.show(getApplicationContext(), "Something went wrong!" + e.getMessage());
        //            }
        //        }
    }

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {
            MyLog.i("Message-------------->接收到消息1")
            val action = intent.action
            if (TextUtils.equals(action, ACTION_PUSH_SEND)) {
                //                String name = intent.getStringExtra(Constant.FLAG);
                //                String msg = intent.getStringExtra(Constant.FLAG2);
                //                sendMsg(name, msg);
            } else if (TextUtils.equals(action, ACTION_PUSH)) {
                //                String content = intent.getStringExtra(Constant.FLAG);
                //                Intent nIntent;
                //                nIntent = new Intent(context, MainActivity.class);
                //                nIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                //                nIntent.putExtra(Constant.FLAG, content);
                //                showNotify(nIntent, getString(R.string.app_name), content);
            } else if (TextUtils.equals(action, ACTION_PUSH_LOST)) {
                MyToast.show(context, "连接丢失")
            }
        }
    }
    private var notifyManager: NotificationManager? = null

    private fun showNotify(intent: Intent, title: String, content: String) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        val mNotification = android.support.v4.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setTicker(title + content)
                .setContentTitle(title)
                .setContentText(content).setContentIntent(pendingIntent)
        val notification = mNotification.build()
        notification.flags = notification.flags or Notification.FLAG_AUTO_CANCEL
        notification.flags = notification.flags or Notification.FLAG_SHOW_LIGHTS
        notification.defaults = notification.defaults or Notification.DEFAULT_ALL
        if (notifyManager == null)
            notifyManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notifyManager!!.cancelAll()
        notifyManager!!.notify(0, notification)
    }

    private val mId: Int = 0

    private fun sendMsg(name: String, msg: String) {
        connectMqtt()
        //        if (FormatUtil.isNoEmpty(name) && FormatUtil.isNoEmpty(msg) && mqttClient != null) {
        //            mId++;
        //            MqttMessage m = new MqttMessage();
        //            m.setId(mId);
        //            m.setQos(1);
        //            m.setPayload(msg.getBytes());
        //            try {
        //                mqttClient.publish(name, m);
        //            } catch (MqttException e) {
        //                e.printStackTrace();
        //            }
        //        }
    }
    //
    //    private void playHint(int type) {
    //        int resId = 0;
    //        switch (type) {
    //            case 4://乘客已下单,请去抢单
    //                resId = R.raw.order_grad;
    //                break;
    //            case 5://车主已抢单,请去确认
    //                resId = R.raw.order_graded;
    //                break;
    //            case 6://乘客已结账
    //                resId = R.raw.order_payed;
    //                break;
    //            case 7://您有订单已取消,请去确认
    //                resId = R.raw.order_cancel;
    //                break;
    //            case 8://乘客已确认,请去接送
    //                resId = R.raw.order_take;
    //                break;
    //            case 10://您的车主审核已通过认证
    //                resId = R.raw.order_authed;
    //                break;
    //        }
    //        if (resId == 0)
    //            return;
    //        try {
    //            MediaPlayer player = MediaPlayer.create(this, resId);
    //            player.start();
    //        } catch (Exception e) {
    //            e.printStackTrace();
    //        }
    //    }

    internal var lifeCount: Int = 0
    internal var handler: android.os.Handler? = null

    override fun run() {
        lifeCount++
        MyLog.e("我还活着:" + lifeCount)
        handler!!.postDelayed(this, (3 * 60 * 1000).toLong())
    }


    override fun onRequestSuccess(result: ResultInfo) {}

    override fun onRequestError(result: ResultInfo) {}

    companion object {
        //private MqttClient mqttClient;
        /**
         * 推送消息
         */
        val ACTION_PUSH = "com.hy.app.PUSH"
        /**
         * 推送连接LOST
         */
        val ACTION_PUSH_LOST = "com.hy.app.PUSH_LOST"

        /**
         * 发送推送消息
         */
        val ACTION_PUSH_SEND = "com.hy.app.SEND"
    }

    //    class MyMqttCallback implements MqttCallback {
    //        private Context context;
    //
    //        public Context getContext() {
    //            return context;
    //        }
    //
    //        public MyMqttCallback(Context context) {
    //            this.context = context;
    //        }
    //
    //        @Override
    //        public void connectionLost(Throwable throwable) {
    //            MyLog.INSTANCE.e("mqttClient: connectionLost" + throwable.toString());
    //            getContext().sendBroadcast(new Intent(ACTION_PUSH_LOST));
    //            //MyToast.show(context,"connectionLost");
    //        }
    //
    //        @Override
    //        public void messageArrived(String s, MqttMessage msg) throws Exception {
    //            MyLog.INSTANCE.d("mqttClient: " + msg.toString());
    //            if (null != msg.getPayload()) {
    //                //MyToast.show(context,"messageArrived");
    //                Intent intent = new Intent(ACTION_PUSH);
    //                intent.putExtra(Constant.FLAG, msg.toString());
    //                getContext().sendOrderedBroadcast(intent, null);
    //            }
    //        }
    //
    //        @Override
    //        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
    //            MyLog.INSTANCE.d("mqttClient: deliveryComplete");
    //            //MyToast.show(context,"deliveryComplete");
    //        }
    //    }
}
