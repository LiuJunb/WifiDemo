package com.xmg.wifidemo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.util.Log;

import java.util.List;

/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/8/12 0012
 */
public class WifiHelper {

    public static final String TAG="WifiHelper";
    //管理wifi
    private android.net.wifi.WifiManager mWifiManager;
    // WifiInfo对象
    private WifiInfo mWifiInfo;
    // 扫描出的
    private List<ScanResult> mWifiList;
    // 网络连接列表
    private List<WifiConfiguration> mWifiConfigurations;
    // 定义一个WifiLock
    private android.net.wifi.WifiManager.WifiLock mWifiLock;
    //管理网络连接
    private ConnectivityManager connectManager;
    //网络连接
    private NetworkInfo netInfo;
    //动态主机配置协议信息的对象，获得IP等网关信息
    private DhcpInfo dhcpInfo;

    /**
     * 构造器
     * @param context
     */
    public WifiHelper(Context context) {
        //取得WifiManager对象
        mWifiManager = (android.net.wifi.WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
         //获取管理网络连接对象
//        connectManager = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
          //获取网络连接对象
//        netInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
          //获取动态主机配置协议信息的对象
//        dhcpInfo = mWifiManager.getDhcpInfo();
    }

    /**再次获取wifi信息*/
    public void reSetWifiInfo(){
        // 取得WifiInfo对象
        mWifiInfo = mWifiManager.getConnectionInfo();
    }

    /** 打开WIFI*/
    public void openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
    }

    /**关闭WIFI*/
    public void closeWifi() {
        if (mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(false);
        }
    }

    /**
     * 检查当前WIFI状态
     * 1.WifiManager.WIFI_STATE_DISABLING: Wi-Fi已被禁用
     * 2.WifiManager.WIFI_STATE_DISABLING: 无线网络目前正在禁用
     * 3.WifiManager.WIFI_STATE_ENABLED: Wi-Fi已启用
     * 4.WifiManager.WIFI_STATE_ENABLING:目前正在支持Wi-Fi
     * 5.WifiManager.WIFI_STATE_UNKNOWN:无线网络处于未知状态
      */
    public int getWifiState() {
        return mWifiManager.getWifiState();
    }

    /**
     * 返回最新的接入点(wifi)的结果
     * ScanResult包含：
     * ScanResult.level ： wifi信号强度，值越大信号越强
     * scanResult.frequency：wifi的频率
     * scanResult.SSID: wifi名称
     * scanResult.BSSID：wifi的BSSID
     * ......
     * @return
     */
    public List<ScanResult>  startScanWifi() {
        mWifiManager.startScan();
        // 得到扫描结果
        mWifiList = mWifiManager.getScanResults();
        return mWifiList;
    }

    /**返回请求者配置的所有网络的列表。*/
    public List<WifiConfiguration> getConfiguredNetworks(){
        // 得到配置好的网络连接
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();
        return mWifiConfigurations;
    }
    /** 得到MAC地址*/
    public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }

    /**得到接入点的BSSID*/
    public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }

    /**得到IP地址*/
    public int getIPAddress() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
    }
    /**得到连接的ID*/
    public int getNetworkId() {
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
    }

    /**得到WifiInfo的所有信息包*/
    public String getWifiInfo() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
    }

    /**添加一个网络并连接*/
    public void addNetworkWPA(String wifiName ,String wifiPassword) {
        WifiConfiguration wifiConfiguration = CreateWifiInfo(wifiName, wifiPassword, 3);
        int wcgID = mWifiManager.addNetwork(wifiConfiguration);
        boolean b =  mWifiManager.enableNetwork(wcgID, true);
        Log.d(TAG,"wcgID="+wcgID);
        Log.d(TAG,"b="+b);
    }

    /**断开指定ID的网络*/
    public void disconnectWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    /**允许之前配置的网络与关联*/
    public void enableNetwork(int netId){
        mWifiManager.enableNetwork(netId,false);
    }

    /**创建一个wifi配置信息*/
    private WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type)
    {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);
        if(tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        /**连接不需要密码的wifi*/
        if(Type == 1) //WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "\"\"";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        /**连接wep格式加密wifi*/
        if(Type == 2) //WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0]= "\""+Password+"\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        /**连接WPA格式加密wifi（就是我们平时使用的加密方法）*/
        if(Type == 3) //WIFICIPHER_WPA
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            //config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String str){
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs){
            if (existingConfig.SSID.equals(str.trim())){
                return existingConfig;
            }
        }
        return null;
    }

}
