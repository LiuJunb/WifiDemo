package com.xmg.wifidemo.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.xmg.wifidemo.R;


public abstract class BaseActivity extends AppCompatActivity {
    protected ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder buider;

    public void showProgressDailog() {
        if (progressDialog == null)
            progressDialog = new ProgressDialog(BaseActivity.this);
        progressDialog.setMessage("正在搜索...");
        progressDialog.show();
    }

    public void hideProgressDailog() {
        if (progressDialog != null) {
            if (progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    public void showDailog(String msg, DialogInterface.OnClickListener listenter) {
        alertDialog = new AlertDialog.Builder(BaseActivity.this).create();
        alertDialog.setMessage(msg);
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确认", listenter);
        alertDialog.show();
    }

    public void hideDailog() {
        if (alertDialog != null) {
            if (alertDialog.isShowing()) {
                alertDialog.dismiss();
            }
        }
    }

    public View showInputDailog(String msg, String usename, DialogInterface.OnClickListener listenter) {
        View view = View.inflate(this, R.layout.input_dailog, null);
        TextView viewById = (TextView) view.findViewById(R.id.text_name);
        EditText username = (EditText) view.findViewById(R.id.username);
        username.setText(usename);
        username.setSelection(usename.length());
        viewById.setText(msg);
        alertDialog = new AlertDialog.Builder(BaseActivity.this)
                .setView(view)
                .setPositiveButton("确认", listenter)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        alertDialog.cancel();
                    }
                }).create();
        alertDialog.show();
        return view;
    }

    public void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    public boolean hasNet() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null != connectivity)
        {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected())
            {
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    return true;
                }
            }
        }
        return false;
    }

}
