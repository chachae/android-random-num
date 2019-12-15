package com.example.beling;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.pm.FeatureInfo;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private CameraManager manager;
    private Camera camera = null;
    private Camera.Parameters mParameters;
    Button btn;
    Button sos;
    private boolean rs;//记录关还是开
    private boolean sosrs;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        sos = (Button) findViewById(R.id.sos);
        rs=false;
        sosrs=false;
        manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] camerList = manager.getCameraIdList();
            for (String str : camerList) {
            }
        } catch (CameraAccessException e) {
            Log.e("error", e.getMessage());
        }
//监听器

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rs=!rs;
                if(rs==false){
                    Switch(false);
                    btn.setBackgroundResource(R.drawable.open);
                }else {
                    Switch(true);
                    btn.setBackgroundResource(R.drawable.close);
                }
            }
        });
            sos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sosrs=!sosrs;

                }
            });

    }

    //手电筒控制
    private void Switch(final boolean rs) {
        if (rs) { // 关闭手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (camera != null) {
                    camera.stopPreview();
                    camera.release();
                    camera = null;
                }
            }
        } else { // 打开手电筒
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    manager.setTorchMode("0", true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final PackageManager pm = getPackageManager();
                final FeatureInfo[] features = pm.getSystemAvailableFeatures();
                for (final FeatureInfo f : features) {
                    if (PackageManager.FEATURE_CAMERA_FLASH.equals(f.name)) { // 判断设备是否支持闪光灯
                        if (null == camera) {
                            camera = Camera.open();
                        }
                        final Camera.Parameters parameters = camera.getParameters();
                        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(parameters);
                        camera.startPreview();
                    }
                }
            }
        }
    }
    // 检测当前设备是否配置闪光灯
    boolean checkFlashlight(View view) {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
            Toast.makeText(this, "当前设备没有闪光灯", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    // 打开闪光灯
    void openFlashlight() {

        try {
            camera = Camera.open();
            int textureId = 0;
            camera.setPreviewTexture(new SurfaceTexture(textureId));
            camera.startPreview();

            mParameters = camera.getParameters();

            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
            camera.setParameters(mParameters);

        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    // 关闭闪光灯
    void closeFlashlight() {

        if (camera != null) {
            mParameters = camera.getParameters();
            mParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
            camera.setParameters(mParameters);
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

}
