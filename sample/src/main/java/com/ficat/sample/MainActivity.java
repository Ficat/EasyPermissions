package com.ficat.sample;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ficat.easypermissions.EasyPermissions;
import com.ficat.easypermissions.Permission;
import com.ficat.easypermissions.RequestSubscriber;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btnCamera, btnLocation;
    EasyPermissions easyPermissions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnCamera = findViewById(R.id.btn_camera);
        btnLocation = findViewById(R.id.btn_location);

        btnCamera.setOnClickListener(this);
        btnLocation.setOnClickListener(this);

        easyPermissions = new EasyPermissions(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_camera:
                easyPermissions.request(Manifest.permission.CAMERA)
                        .subscribe(new RequestSubscriber<Boolean>() {
                            @Override
                            public void onPermissionsRequestResult(Boolean aBoolean) {
                                if (aBoolean) {
                                    Toast.makeText(MainActivity.this, "request CAMERA success!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "request CAMERA fail!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            case R.id.btn_location:
                easyPermissions.requestEach(Manifest.permission.ACCESS_COARSE_LOCATION)
                        .subscribe(new RequestSubscriber<Permission>() {
                            @Override
                            public void onPermissionsRequestResult(Permission permission) {
                                if (permission.granted) {
                                    Toast.makeText(MainActivity.this, "request LOCATION success!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "request LOCATION fail!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }
}
