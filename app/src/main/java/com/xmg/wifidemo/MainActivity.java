package com.xmg.wifidemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.xmg.wifidemo.adapter.WifiAdapter;
import com.xmg.wifidemo.base.BaseActivity;
import com.xmg.wifidemo.utils.ImageUtils;
import com.xmg.wifidemo.utils.WifiHelper;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements View.OnClickListener {

    @Bind(R.id.btn_start)
    Button btnStart;
    @Bind(R.id.btn_stop)
    Button btnStop;
    @Bind(R.id.ed_name)
    EditText edName;
    @Bind(R.id.ed_password)
    EditText edPassword;
    @Bind(R.id.btn_connect)
    Button btnConnect;
    @Bind(R.id.btn_search)
    Button btnSearch;
    @Bind(R.id.ls_wifi)
    ListView lsWifi;
    @Bind(R.id.img_logo)
    ImageView imgLogo;
    @Bind(R.id.btn_text_net)
    Button btnTextNet;
    @Bind(R.id.btn_unconnect)
    Button btnUnconnect;
    /**wifi管理器*/
    private WifiHelper wifiManager;
    private WifiAdapter wifiAdapter;
    private WifiStateReceiver wifiReceiver;
    private String pic_url="http://avatar.csdn.net/A/2/3/1_u012987546.jpg";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        /**隐藏软件盘*/
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE |
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        initDate();
        setListener();

    }

    private void initDate() {
         wifiManager = new WifiHelper(this);

        /**注册wifi状态改变的监听器*/
        //WIFI状态接收器
        wifiReceiver=new WifiStateReceiver(this);
        IntentFilter filter=new IntentFilter();
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
//        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(wifiReceiver,filter);
    }

    private void setListener() {
        btnStart.setOnClickListener(this);
        btnStop.setOnClickListener(this);
        btnConnect.setOnClickListener(this);
        btnSearch.setOnClickListener(this);
        btnTextNet.setOnClickListener(this);
        btnUnconnect.setOnClickListener(this);

        /**点击进行连接*/
        lsWifi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public View view1;

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                if (wifiAdapter != null) {
                    List<ScanResult> scanResults = wifiAdapter.getScanResults();
                    final ScanResult scanResult = scanResults.get(i);
                    view1 = showInputDailog("连接" + scanResult.SSID + " wifi",scanResult.SSID, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            EditText password = (EditText) view1.findViewById(R.id.password);
//                            Toast.makeText(MainActivity.this,password.getText(),Toast.LENGTH_SHORT).show();
                            if(wifiManager==null){
                                wifiManager=new WifiHelper(MainActivity.this);
                            }
                            wifiManager.addNetworkWPA(scanResult.SSID ,password.getText().toString().trim());
                        }
                    });

                }
            }
        });

        /**长按*/
        lsWifi.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {

                return false;
            }
        });
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case  R.id.btn_start:
                wifiManager.openWifi();
                break;
            case  R.id.btn_stop:
                wifiManager.closeWifi();
                break;
            case  R.id.btn_connect:
                String ssid = edName.getText().toString().trim();
                String password = edPassword.getText().toString().trim();
                if(TextUtils.isEmpty(ssid)){
                    showToast("wifi名不能为空");
                    return;
                }else if(TextUtils.isEmpty(password)){
                    showToast("wifi密码不能为空");
                    return;
                }
                Log.d("MainActivity","ssid="+ssid+" password="+password);
                /**连接这个wifi*/
                wifiManager.addNetworkWPA(ssid,password);
                break;
            case R.id.btn_unconnect:
                /**断开当前连接的wifi*/
                if(wifiManager==null){
                    wifiManager=new WifiHelper(MainActivity.this);
                }
                wifiManager.disconnectWifi(wifiManager.getNetworkId());
                break;
            case  R.id.btn_search:
                /**搜索附近的wifi*/
                List<ScanResult> scanResults = wifiManager.startScanWifi();
                /**把附近的wifi列出来*/
                if(wifiAdapter==null) {
                    wifiAdapter = new WifiAdapter(this, scanResults);
                    lsWifi.setAdapter(wifiAdapter);
                }else {
                    wifiAdapter.setScanResults(scanResults);
                    wifiAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_text_net:

                /**测试网络*/
                if(hasNet()){
                    ImageUtils.disPlayImageNoCache(MainActivity.this, pic_url,imgLogo, new RequestListener() {
                        @Override
                        public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                            Log.d("MainActivity","======图片加载失败");
                            Toast.makeText(MainActivity.this,"图片加载失败",Toast.LENGTH_SHORT).show();

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                            Log.d("MainActivity","======图片加载成功");
                            return false;
                        }
                    });
                }else{
                    Toast.makeText(MainActivity.this,"没有连上网络",Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        unregisterReceiver(wifiReceiver);
    }


    /**
     *  Wifi开关,信号,状态改变监听
     */
    public class WifiStateReceiver extends BroadcastReceiver {
        private static final String TAG = "WifiStateReceiver";
        Context context;

        public WifiStateReceiver(Context context) {
            this.context = context;

        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)) {

            } else if (intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                /**网络状态改变*/
//                Log.d(TAG, "网络状态改变");
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (info.getState().equals(NetworkInfo.State.CONNECTED)) {
                    Log.d(TAG, "网络连接成功");
                    Toast.makeText(MainActivity.this,"wifi连接成功",Toast.LENGTH_SHORT).show();
                } else if (info.getState().equals(NetworkInfo.State.DISCONNECTED)) {
                    Log.d(TAG, "网络连接失败");
                    Toast.makeText(MainActivity.this,"wifi连接失败",Toast.LENGTH_SHORT).show();
                }
            } else if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                /**Wifi状态改变*/
//                Log.d(TAG, "Wifi状态改变");
                int wifistate = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_DISABLED);
                if (wifistate == WifiManager.WIFI_STATE_ENABLED) {/**wifi可用*/
                    Log.d(TAG, "wifi可用");
                    Toast.makeText(MainActivity.this,"wifi打开成功",Toast.LENGTH_SHORT).show();

                } else if (wifistate == WifiManager.WIFI_STATE_DISABLED) {/**wifi不可用*/
                    Log.d(TAG, "wifi不可用");
                    Toast.makeText(MainActivity.this,"wifi关闭成功",Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    }
