package com.ex.administrator.phonetest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    TextView mtnName,mtnName1,mtnName2,mtnName3,mtn,mtn1,mtn2,mtn3,mbattery;
    View mlv;
    private Camera.Size size;
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mtnName = (TextView) findViewById(R.id.tnName);
        mtnName1 = (TextView) findViewById(R.id.tnName1);
        mtnName2 = (TextView) findViewById(R.id.tnName2);
        mtnName3 = (TextView) findViewById(R.id.tnName3);
        mtn= (TextView) findViewById(R.id.tn);
        mtn1= (TextView) findViewById(R.id.tn1);
        mtn2= (TextView) findViewById(R.id.tn2);
        mtn3= (TextView) findViewById(R.id.tn3);


        mlv = findViewById(R.id.lv);


        TelephonyManager tm= (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        //获取IMSI设备识别码
        String subscriberId = tm.getSubscriberId();

        mtnName.setText("IMSI:"+subscriberId);

        String deviceId = tm.getDeviceId();
        mtn.setText("IMEI:"+deviceId);
        //设备版本号
        String ver= Build.VERSION.RELEASE;
        mtnName2.setText("系统版本号 "+ver);
        //CPU类型名称和数量

        mtn2.setText("CPU类型名称 "+getPhoneCpuName()+"      CPU数量"+getPhoneCpuNumber());

        //设备品牌
        String brand=Build.BRAND;
        //设备型号名称
        String model=Build.MODEL ;
        mtnName3.setText("型号"+model+"\n设备"+brand);



        mtn3.setText("是否ROOT:"+root()+"\n屏幕分辨率"+getResolution());

        mtnName1.setText(wifimMAC());
        mtn1.setText(wifimIP());
       BatteryBroadCast bb=new BatteryBroadCast();
        IntentFilter  f= new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(bb,f);


    }
//mac
        public String wifimMAC (){
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            //获取MAC
            WifiInfo wInfo = wifiManager.getConnectionInfo();
            String macAddress = wInfo.getMacAddress();
            return "MAC:"+macAddress;
    }
    //ip
    public String wifimIP (){
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        //获取IP
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        int ipAddress = wInfo.getIpAddress();
        return "IP:"+ipAddress;
    }
    //是否Root
    public boolean root(){
        boolean r=false;
        try {
            r = ((new File("system/bin/su").exists())||(new File("system/bin/su").exists()));
        }catch (Exception e){
            e.printStackTrace();
        }
        return r;
    }

    public class BatteryBroadCast extends BroadcastReceiver{


        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)){
                Bundle bundle=intent.getExtras();
                //最大电量值
                Integer max= (Integer) bundle.get(BatteryManager.EXTRA_SCALE);
                //当前电量值
                Integer newe= (Integer) bundle.get(BatteryManager.EXTRA_LEVEL);
                //当前电池的温度
                Integer temp= (Integer) bundle.get(BatteryManager.EXTRA_TEMPERATURE)/10;
                mbattery= (TextView) findViewById(R.id.battery);
                mbattery.setText("最大电量"+max+"%\n当前电量"+newe+"%\n当前温度"+temp+"℃");


            }

        }
    }
    /**
     * 获取手机分辨率
     */
    public String getResolution() {
        String resolution = "";
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        resolution = metrics.widthPixels + "*" + metrics.heightPixels;
        return resolution;
    }
    /**
     * 获取相机最大尺寸
     */
//    public String getCameraResolution() {
//        String cameraResolution = "";
//        Camera camera = Camera.open();
//        Camera.Parameters parameters = camera.getParameters();
//        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();
//        Camera.Size size = null;
//        for (Camera.Size s : sizes) {
//            if (size == null) {
//                size = s;
//            } else if (size.height * size.width < s.height * s.width) {
//                size = s;
//            }
//        }
//        cameraResolution = (size.width * size.height) / 10000 + "万像素";
//        camera.release();
//        return cameraResolution;
//    }

    //相机的最大分变率
    public String getcameraResolution(){

        android.hardware.Camera camera= android.hardware.Camera.open();
        Camera.Parameters parameters=camera.getParameters();
        List<Camera.Size> sizes=parameters.getSupportedPictureSizes();
        for (Camera.Size s : sizes) {
            if (size==null) {
                size= s;
            }else if(size.height*size.width<s.height*s.width) {
                size = s;

            }
        }
        String cameraResolution = (size.width * size.height) / 10000 + "万像素";
        camera.release();
        return cameraResolution;

//            s. width; //摄像头的宽度
//            s. height;//摄像头的宽度


    }
    /** 设备CPU名称 */
    public String getPhoneCpuName() {
        try {
            FileReader fr = new FileReader("/proc/cpuinfo");
            BufferedReader br = new BufferedReader(fr);
            String text = br.readLine();
            String[] array = text.split(":\\s+", 2);
            return array[1];
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** 设备CPU数量 */
    public int getPhoneCpuNumber() {
        class CpuFilter implements FileFilter {
            public boolean accept(File pathname) {
                if (Pattern.matches("cpu[0-9]", pathname.getName())) {
                    return true;
                }
                return false;
            }
        }
        try {
            File dir = new File("/sys/devices/system/cpu/");
            File[] files = dir.listFiles(new CpuFilter());
            return files.length;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }






}
