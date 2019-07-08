package com.ficat.sample;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ficat.easypermissions.BaseRequestExecutor;
import com.ficat.easypermissions.EasyPermissions;
import com.ficat.easypermissions.RequestEachExecutor;
import com.ficat.easypermissions.RequestExecutor;
import com.ficat.easypermissions.bean.Permission;

import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnCamera, btnLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        btnCamera = findViewById(R.id.btn_camera);
        btnLocation = findViewById(R.id.btn_location);
        btnCamera.setOnClickListener(this);
        btnLocation.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                EasyPermissions
                        .with(this)
                        .requestEach(Manifest.permission.CAMERA)
                        .result(new RequestEachExecutor.ResultReceiver() {
                            @Override
                            public void onPermissionsRequestResult(Permission permission) {
                                if (permission.granted) {
                                    Toast.makeText(MainActivity.this, "request CAMERA success!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "request CAMERA fail!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.btn_location:
                EasyPermissions
                        .with(this)
                        .request(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .autoRetryWhenUserRefuse(true, new BaseRequestExecutor.RequestAgainListener() {
                            @Override
                            public void requestAgain(String[] needAndCanRequestAgainPermissions) {
                                for (String s : needAndCanRequestAgainPermissions) {
                                    Log.e("TAG", "request again permission = " + s);
                                }
                            }
                        })
                        .result(new RequestExecutor.ResultReceiver() {
                            @Override
                            public void onPermissionsRequestResult(boolean grantAll, List<Permission> results) {
                                if (grantAll) {
                                    Toast.makeText(MainActivity.this, "request permissions success!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "request permissions fail!", Toast.LENGTH_SHORT).show();
                                }
                                for (Permission p : results) {
                                    Log.e("TAG", "name=" + p.name + "   granted=" + p.granted + "   shouldShowRequestPermissionRationale=" + p.shouldShowRequestPermissionRationale);
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }
}
