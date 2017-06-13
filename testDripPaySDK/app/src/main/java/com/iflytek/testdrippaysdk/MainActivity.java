package com.iflytek.testdrippaysdk;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.iflytek.drip.DripPay;
import com.iflytek.drip.DripPayConfig;
import com.iflytek.drip.activity.UNPayEntryBaseActivity;
import com.iflytek.drip.constant.PayConstant;
import com.iflytek.drip.entities.Charge;
import com.iflytek.drip.exception.DripPayException;
import com.iflytek.drip.listener.IPayListener;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

public class MainActivity extends UNPayEntryBaseActivity implements Handler.Callback,Runnable{
    private Button mWxPayBtn;
    private Button mQqPayBtn;
    private Button mAlipayBtn;
    private Button mUinonPay;
    private final String TAG = "MainActivity";
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWxPayBtn = (Button) findViewById(R.id.wxpay);
        mAlipayBtn = (Button) findViewById(R.id.alipay);
        mQqPayBtn = (Button) findViewById(R.id.qqpay);
        mUinonPay = (Button) findViewById(R.id.unionpay);

        mHandler = new Handler(this);

        mWxPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mAlipayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mQqPayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mUinonPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d(TAG, "onClick: ");
                DripPayConfig.Builder builder = new DripPayConfig.Builder();
                builder.setDebugMode(true);
                builder.build();

                new Thread(MainActivity.this).start();
            }
        });
    }

    @Override
    public void run() {
        String tn = null;
        InputStream is;
        try {

            String url = "http://101.231.204.84:8091/sim/getacptn";

            URL myURL = new URL(url);
            URLConnection ucon = myURL.openConnection();
            ucon.setConnectTimeout(120000);
            is = ucon.getInputStream();
            int i = -1;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((i = is.read()) != -1) {
                baos.write(i);
            }

            tn = baos.toString();
            is.close();
            baos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "run: "+tn);
        Message msg = mHandler.obtainMessage();
        msg.obj = tn;
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message message) {
        Log.d(TAG, "handleMessage: "+message.obj);
        if (message.obj == null || ((String) message.obj).length() == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("错误提示");
            builder.setMessage("网络连接失败,请重试!");
            builder.setNegativeButton("确定",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.create().show();
        } else {
            tnNumber = (String) message.obj;
            Charge newUnionPayCharge = new Charge();
            newUnionPayCharge.setAppId("Drippay201791311231");
            newUnionPayCharge.setChannel(PayConstant.PAY_CHANNEL_UNPAY);
            newUnionPayCharge.setSign("C380BEC2BFD727A4B6845133519F3AD6");
            newUnionPayCharge.setNonceStr("ibuaiVcKdpRxkhJA");
            newUnionPayCharge.setChannelResult(new HashMap<String, String>(){{put("tn",tnNumber);}});
            try{
                IPayListener iPayListener =  new IPayListener() {
                    @Override
                    public void onSuccess(Charge charge) {
                        Log.d(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onCancel(Charge charge) {
                        Log.d(TAG, "onCancel: ");
                    }

                    @Override
                    public void onError(Charge charge, String msg) {
                        Log.d(TAG, "onError: ");
                    }
                };
                DripPay.createPayment(MainActivity.this ,newUnionPayCharge,iPayListener );
            }catch(DripPayException e){
                e.printStackTrace();
            }catch (Exception e){
                e.printStackTrace();
            }finally {

            }
        }
        return false;
    }
}

