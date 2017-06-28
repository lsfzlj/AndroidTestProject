package com.iflytek.testhandler;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;


public class MainActivity extends Activity {
    private TextView mTextView;
    private HandlerTest1 mHandlerTest1;
    private HandlerTest2 mHandlerTest2;
    private Handler handler1;
    private Handler handler2;

    private Handler handler0;

    private HandlerThread myHandlerThread;
    private Handler handler3;

    private int counter=0;
    private String TAG = "threadinfo";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init4();
    }

    public class myThread extends Thread{
        @Override
        public void run() {
            super.run();
            Looper.prepare();
            handler0 = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG, "handleMessage: in myThread"+msg+"   "+Thread.currentThread().getId());
                }
            };
            Message mss = new Message();
            mss.obj = "test in handler";
            Log.d(TAG, "run: "+Thread.currentThread().getId());
            handler0.sendMessage(mss);
            Looper.loop();
        }
    }

    private void init2(){
        Log.d(TAG, "init1: "+Thread.currentThread().getId());
        new MyThread1().start();
        new MyThread2().start();
    }

    private void init3() {
        mTextView = (TextView) findViewById(R.id.text);
        Log.d(TAG, "init:   "+Thread.currentThread().getId());
        //1 子线程发送消息给本身
        new Thread() {
            public void run() {
                long temp = Thread.currentThread().getId();
                Log.d(TAG, "run: "+temp);
                Looper.prepare();
                mHandlerTest1=new HandlerTest1();
                Message message = new Message();
                message.obj = "子线程发送的消息Hi~Hi";
                mHandlerTest1.sendMessage(message);
                Looper.loop();
            }
        }.start();
    }

    private void init4(){
        //创建一个线程,线程名字：handler-thread
        myHandlerThread = new HandlerThread( "handler-thread") ;
        //开启一个线程
        myHandlerThread.start();
        //在这个线程中创建一个handler对象
        Log.d(TAG, "init4: "+Thread.currentThread().getId());
        handler3 = new Handler( myHandlerThread.getLooper() ){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //这个方法是运行在 handler-thread 线程中的 ，可以执行耗时操作
                Log.d(TAG , "消息： " + msg.what + "  线程： " + Thread.currentThread().getId()) ;
            }
        };

        //在主线程给handler发送消息
        handler3.sendEmptyMessage( 1 ) ;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "new runnable "+Thread.currentThread().getId());
                //在子线程给handler发送数据
                handler3.sendEmptyMessage( 2 ) ;
            }
        }).start() ;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myHandlerThread.quit();
    }

    private class HandlerTest1 extends Handler {
        private HandlerTest1(Looper looper) {
            super(looper);
        }
        private HandlerTest1(){
            super();
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            System.out.println("子线程收到:" + msg.obj);

            Log.d(TAG, "handleMessage: in handlerTest1 : "+ Thread.currentThread().getId());
            //2  收到消息后可再发消息到主线程
            mHandlerTest2=new HandlerTest2(getMainLooper());
            Message message = new Message();
            message.obj = "O(∩_∩)O";
            mHandlerTest2.sendMessage(message);
        }
    }

    private class HandlerTest2 extends Handler {

        private HandlerTest2(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            mTextView.setText("在主线程中,收到子线程发来消息:" + msg.obj);
            Log.d(TAG, "handleMessage: +handlerTest2" + Thread.currentThread().getId());
            //3  收到消息后再发消息到子线程
            if (counter==0) {
                Message message = new Message();
                message.obj = "主线程发送的消息Xi~Xi";
                mHandlerTest1.sendMessage(message);
                counter++;
            }

        }
    }

    class MyThread1 extends Thread {

        @Override
        public void run() {
            super.run();

            Looper.prepare();

            handler1 = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG,"threadName--" + Thread.currentThread().getId() + "messageWhat-"+ msg.what );
                }
            };

            try {
                sleep( 3000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler2.sendEmptyMessage( 2 ) ;
            Log.d(TAG, "run: Mythread1 :"+Thread.currentThread().getId());
            Looper.loop();
        }
    }

    class MyThread2 extends Thread {
        @Override
        public void run() {
            super.run();
            Looper.prepare();

            handler2 = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG,"threadName--" + Thread.currentThread().getId() + "messageWhat-"+ msg.what );
                }
            };

            try {
                sleep( 4000 );
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            handler1.sendEmptyMessage( 5 ) ;
            Log.d(TAG, "run: Mythread2 :"+Thread.currentThread().getId());
            Looper.loop();
        }
    }
}