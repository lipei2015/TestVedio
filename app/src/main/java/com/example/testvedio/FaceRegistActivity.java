package com.example.testvedio;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.*;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Toast;
import com.example.testvedio.util.ResizeAbleSurfaceView;
import com.wrtsz.intercom.master.IFaceApi;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 人脸信息绑定注册
 */
public class FaceRegistActivity extends Activity implements SurfaceHolder.Callback {
    private boolean safeToTakePicture = false;
    private Camera mCamera;
    ResizeAbleSurfaceView mPreview;
    private SurfaceHolder mHolder;
    private int cameraId = Camera.CameraInfo.CAMERA_FACING_BACK;//声明cameraId属性，设备中0为前置摄像头；一般手机0为后置摄像头，1为前置摄像头
//    private int cameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;//声明cameraId属性，设备中0为前置摄像头；一般手机0为后置摄像头，1为前置摄像头

    private int widthPixels;
    private int heightPixels;

    private IFaceApi iFaceApi;
    private String localPath;

    private final int CASE_TAKE_PICTURE = 0;
    private final int CASE_DEAL_PICTURE = 1;
    private final int CASE_COUNT_DOWN = 2;      // 提示框倒计时

    private String pid;
    private String userName;

    private int sWidth;
    private int sHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_regist);

        mPreview = findViewById(R.id.preview);
        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);

        // 重新设置Surface宽高，防止比例跟Camera不一致而变形
//        sWidth = (int) getResources().getDimension(R.dimen.face_regist_surface_width);
//        sHeight = (int) getResources().getDimension(R.dimen.face_regist_surface_height);
//        mPreview.resize(sWidth,sHeight);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;

        localPath = "/sdcard/testRegistPic/";
        createDirectory(localPath);

        startTimer();

        // 实例化远程调用设备SDK服务
        initAidlService();
    }

   /* @SuppressLint("WrongConstant")
    @Override
    protected void initView() {
        pid = getIntent().getStringExtra("pid");
        Log.e("address",pid);

        mHolder = mPreview.getHolder();
        mHolder.addCallback(this);

        // 重新设置Surface宽高，防止比例跟Camera不一致而变形
//        sWidth = (int) getResources().getDimension(R.dimen.face_regist_surface_width);
//        sHeight = (int) getResources().getDimension(R.dimen.face_regist_surface_height);
//        mPreview.resize(sWidth,sHeight);

//        textToSpeechUtil = new TextToSpeechUtil(this);
//        wrtdevManager = (WrtdevManager) getSystemService("wrtsz");

//        preferencesUtils = PreferencesUtils.getInstance(ConstantSys.PREFERENCE_USER_NAME);
//        device_id = preferencesUtils.getString(ConstantSys.PREFERENCE_DEVICE_ID,null);

        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);
        widthPixels = outMetrics.widthPixels;
        heightPixels = outMetrics.heightPixels;

        localPath = "/sdcard/faceRegistPic/";
        FileUtil.createDirectory(localPath);

        startTimer();

        // 实例化远程调用设备SDK服务
        initAidlService();
    }*/

    Timer closeTimer;
    private void cancelCountDownTimer() {
        if (closeTimer != null) {
            closeTimer.cancel();
            closeTimer = null;
        }
    }

    private int countDown = 5;      // 提示框隐藏倒计时，从5秒开始，一秒调用一次
    private void startCountDownTimer() {
        closeTimer = new Timer();
        closeTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Message message = new Message();
                message.what = CASE_COUNT_DOWN;
                countDown -- ;
                message.arg1 = countDown;
                handler.sendMessage(message);
            }
        }, 1000,1000);
    }

    private void initAidlService(){
        // 通过Intent指定服务端的服务名称和所在包，与远程Service进行绑定
        //参数与服务器端的action要一致,即"服务器包名.aidl接口文件名"
//        Intent intent = new Intent("com.wrtsz.intercom.master.IFaceApi");
        Intent intent = new Intent("com.wrtsz.intercom.master.WRT_FACE_SERVICE");

        //Android5.0后无法只通过隐式Intent绑定远程Service
        //需要通过setPackage()方法指定包名
        intent.setPackage("com.wrtsz.intercom.master");

        //绑定服务,传入intent和ServiceConnection对象
        boolean iss = bindService(intent, connection, Context.BIND_AUTO_CREATE);
        Log.e("iss",iss+"---");
    }

    //创建ServiceConnection的匿名类
    private ServiceConnection connection = new ServiceConnection() {
        //重写onServiceConnected()方法和onServiceDisconnected()方法
        //在Activity与Service建立关联和解除关联的时候调用
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("onServiceDisconnected", "aidl远程服务断开成功");
        }

        //在Activity与Service建立关联时调用
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("onServiceConnected", "aidl远程服务连接成功");
            //IFaceApi.Stub.asInterface()方法将传入的IBinder对象传换成了mAIDL_Service对象
            iFaceApi = IFaceApi.Stub.asInterface(service);
            try {
                iFaceApi.unreg("4");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

    //定义照片保存并显示的方法
    private Camera.PictureCallback mpictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
//            safeToTakePicture = true;
            long now = System.currentTimeMillis();
            String path = localPath + now +".png";
            File tempfile = new File(path);//新建一个文件对象tempfile，并保存在某路径中

            try {
                FileOutputStream fos = new FileOutputStream(tempfile);
                fos.write(data);//将照片放入文件中
                fos.close();//关闭文件

                cancelTimer();

                // 拍照之后继续显示预览界面
                setStartPreview (mCamera, mHolder);

                Message msg = new Message();
                msg.what = CASE_DEAL_PICTURE;
                Bundle bundle = new Bundle();
                bundle.putString("path",tempfile.getAbsolutePath());
                msg.setData(bundle);
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private int failCount = 0;  // 人脸注册失败次数
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case CASE_TAKE_PICTURE:
                    // 开始拍照
                    capture(mPreview);
                    break;
                case CASE_DEAL_PICTURE:
                    //TODO 调用设备SDK进行人像注册，若返回人像ID就表示成功，再调用开门记录上传接口，若成功则返回到上一个界面；
                    //TODO 若注册失败发出语音提示"授权失败，请按 * 键重试或 # 键退出"

                    // 处理拍出来的照片
                    String path = msg.getData().getString("path");
//                    Log.e("path",imageToBase64(path));

                    if(iFaceApi != null){
                        try {
//                            String requestResult = iFaceApi.reg(path,null,userName,0);
                            String requestResult = iFaceApi.reg(imageToBase64(path),null,"李先生",0);
                            Log.e("requestResult","返回结果："+requestResult);
                            Toast.makeText(FaceRegistActivity.this,requestResult,Toast.LENGTH_SHORT).show();
                            if(requestResult.contains("error")){
                                try {
                                    Thread.sleep(4000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                startTimer();
                            }
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }else{
                        Log.e("failCount",failCount+"");
                        failCount ++;
                        if(failCount >= 3){
                            // 失败次数大于3时，
                            failCount = 0;
                        }else{
                            startTimer();
                        }
                    }
                    break;
                case CASE_COUNT_DOWN:
                    int time = msg.arg1;
                    /*if(time < 0){
                        cancelCountDownTimer();
                        if(confirmDialog != null && confirmDialog.isShowing()){
                            confirmDialog.cancel();
                            finish();
                        }
                    }else{
                        confirmDialog.setMessage("用户未经登记，不能进行登记采集("+time+"S)");
                        confirmDialog.show();
                    }*/
                    break;
            }
            super.handleMessage(msg);
        }
    };

    Timer takePictureTimer;
    private void cancelTimer() {
        if (takePictureTimer != null) {
            takePictureTimer.cancel();
            takePictureTimer = null;
        }
    }

    private void startTimer() {
        cancelTimer();
        takePictureTimer = new Timer();
        takePictureTimer.schedule(new TimerTask() {

            @Override
            public void run() {
//                Log.e("time", "daochu");
                if(safeToTakePicture) {
                    handler.sendEmptyMessage(CASE_TAKE_PICTURE);
                }
            }
        }, 2500);
    }

    /**
     * 定义“拍照”方法
     * @param view
     */
    public void capture(View view) {
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPictureFormat(ImageFormat.JPEG);//设置照片格式
//        parameters.setPreviewSize(widthPixels, heightPixels);
//        parameters.setPreviewSize(sWidth, sHeight);
//        parameters.setPictureSize(sWidth, sHeight);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        //摄像头聚焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {
                if (success) {
                    mCamera.takePicture(null, null, mpictureCallback);
                }
            }
        });
    }

    /**
     * activity生命周期在onResume是界面应是显示状态
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mCamera == null) {//如果此时摄像头值仍为空
            mCamera = getCamera();//则通过getCamera()方法开启摄像头
//            Camera.Parameters parameters = mCamera.getParameters();
//            parameters.setPictureSize(sWidth,sHeight);
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(sWidth, sHeight);
            parameters.setPictureSize(sWidth, sHeight);
            if (mHolder != null) {
                setStartPreview(mCamera, mHolder);//开启预览界面
            }
        }
    }

    /**
     * activity暂停的时候释放摄像头
     */
    @Override
    public void onPause() {
        super.onPause();
        releaseCamera();
        cancelTimer();
    }

    /**
     * onResume()中提到的开启摄像头的方法
     */
    private Camera getCamera() {
        Camera camera;//声明局部变量camera
        try {
            camera = Camera.open(cameraId);
        }//根据cameraId的设置打开前置摄像头
        catch (Exception e) {
            camera = null;
            e.printStackTrace();
        }
        return camera;
    }

    /**
     * 开启预览界面
     * @param camera
     * @param holder
     */
    private void setStartPreview(Camera camera, SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(holder);
//            camera.setDisplayOrientation(90);//如果没有这行你看到的预览界面就会是水平的
            camera.startPreview();
            safeToTakePicture = true;

//            capture(mPreview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 定义释放摄像头的方法
     */
    private void releaseCamera() {
        if (mCamera != null) {//如果摄像头还未释放，则执行下面代码
            mCamera.stopPreview();//1.首先停止预览
            mCamera.setPreviewCallback(null);//2.预览返回值为null
            mCamera.release(); //3.释放摄像头
            mCamera = null;//4.摄像头对象值为null
        }
    }

    /**
     * 定义新建预览界面的方法
     * @param holder
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview (mCamera, mHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        mCamera.stopPreview();//如果预览界面改变，则首先停止预览界面
        setStartPreview(mCamera, mHolder);//调整再重新打开预览界面
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();//预览界面销毁则释放相机
    }

    @Override
    public void onDestroy() {
//        unbindService(connection);
        super.onDestroy();
    }

    /**
     * 判断文件夹是否存在，不存在就新建
     * @param path
     */
    public static void createDirectory(String path){
        File file = new File(path);
        if(!file.exists()){
            file.mkdir();
        }
    }

    /**
     * 将图片转换成Base64编码的字符串
     */
    public static String imageToBase64(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(path);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }
}
