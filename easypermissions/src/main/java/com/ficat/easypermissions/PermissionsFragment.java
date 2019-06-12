package com.ficat.easypermissions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.PermissionChecker;

import com.ficat.easypermissions.bean.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ficat on 2018/5/12.
 */
public class PermissionsFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 19;

    private Map<String, List<BaseRequestPublisher>> mPermissionsMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    static boolean checkSelfPermission(String permission, Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (getTargetVersion(context) >= Build.VERSION_CODES.M) {
                //targetSdkVersion >= Android M, we can use Context#checkSelfPermission
                return context.checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
            } else {
                // targetSdkVersion < Android M, Context#checkSelfPermission will always
                // return PERMISSION_GRANTED, even if we cancel permissions in Android Setting,
                // so we have to use PermissionChecker
                return PermissionChecker.checkSelfPermission(context, permission) == PermissionChecker.PERMISSION_GRANTED;
            }
        } else {// For Android < Android M, self permissions are always granted.
            return true;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, @NonNull BaseRequestPublisher publisher) {
        List<String> needRequestPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (sdkVersionLowerThan23()) {
                publisher.onRequestPermissionsResult(new Permission(permission, true, false));
                continue;
            }
            if (checkSelfPermission(permission, getActivity())) {
                publisher.onRequestPermissionsResult(new Permission(permission, true, false));
                continue;
            }
            if (isRevoked(permission)) {//check if users don't give the permission in Android Setting
                publisher.onRequestPermissionsResult(new Permission(permission, false, false));
                continue;
            }
            List<BaseRequestPublisher> list = mPermissionsMap.get(permission);
            if (list == null) {
                list = new ArrayList<>();
                mPermissionsMap.put(permission, list);
                needRequestPermissions.add(permission);
            }
            if (list.contains(publisher)) {
                continue;
            }
            list.add(publisher);
        }
        if (!needRequestPermissions.isEmpty()) {
            String[] needRequestPermissionsArray = needRequestPermissions.toArray(new String[needRequestPermissions.size()]);
            requestPermissions(needRequestPermissionsArray, PERMISSION_REQUEST_CODE);
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != PERMISSION_REQUEST_CODE) {
            return;
        }
        boolean[] shouldShowRequestPermissionRationale = new boolean[permissions.length];
        for (int i = 0; i < permissions.length; i++) {
            shouldShowRequestPermissionRationale[i] = shouldShowRequestPermissionRationale(permissions[i]);
        }
        onRequestPermissionsResult(permissions, grantResults, shouldShowRequestPermissionRationale);
    }

    void onRequestPermissionsResult(String permissions[], int[] grantResults, boolean[] shouldShowRequestPermissionRationale) {
        for (int i = 0, size = permissions.length; i < size; i++) {
            List<BaseRequestPublisher> list = mPermissionsMap.get(permissions[i]);
            if (list == null) {
                return;
            }
            mPermissionsMap.remove(permissions[i]);
            boolean granted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
            for (int j = 0; j < list.size(); j++) {
                list.get(j).onRequestPermissionsResult(new Permission(permissions[i], granted, shouldShowRequestPermissionRationale[i]));
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        if (getTargetVersion(getActivity()) >= Build.VERSION_CODES.M) {
            return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
        } else {
            return PermissionChecker.checkSelfPermission(getActivity(), permission) != PermissionChecker.PERMISSION_GRANTED;
        }
    }

    boolean sdkVersionLowerThan23() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }

    private static int getTargetVersion(Context context) {
        int targetSdkVersion = -100;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            targetSdkVersion = info.applicationInfo.targetSdkVersion;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return targetSdkVersion;
    }
}
