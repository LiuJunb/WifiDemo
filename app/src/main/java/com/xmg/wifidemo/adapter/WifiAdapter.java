package com.xmg.wifidemo.adapter;


import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.xmg.wifidemo.MainActivity;
import com.xmg.wifidemo.R;

import java.util.List;


/**
 * Description :
 * Author : liujun
 * Email  : liujin2son@163.com
 * Date   : 2016/8/11 0011
 */
public class WifiAdapter extends BaseAdapter {

    private Context context;
    private List<ScanResult> scanResults;

    public WifiAdapter(Context context, List<ScanResult> scanResults) {
        this.context=context;
        this.scanResults=scanResults;
    }

    public List<ScanResult> getScanResults() {
        return scanResults;
    }

    public void setScanResults(List<ScanResult> scanResults) {
        this.scanResults = scanResults;
    }

    @Override
    public int getCount() {
        return  scanResults.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder=null;
        if (convertView == null) {
            viewHolder=new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main, viewGroup, false);
            viewHolder.wifiName= (TextView) convertView.findViewById(R.id.tv_wifiName);
            viewHolder.wifiLeve= (TextView) convertView.findViewById(R.id.tv_wifiLeve);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = scanResults.get(i);
        if(scanResult!=null) {
            viewHolder.wifiName.setText("Wifi名称："+scanResult.SSID);
            viewHolder.wifiLeve.setText("信号强度："+scanResult.level);
        }
        return convertView;
    }


    static  class  ViewHolder{
        TextView wifiName;
        TextView wifiLeve;
    }
}
