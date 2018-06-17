package com.ficat.easypermissions;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ficat on 2018/5/12.
 */
public class PermissionsFragment extends Fragment {

    private static final int PERMISSION_REQUEST_CODE = 19;

    private Map<String, List<RequestPublisher>> mPermissionsMap = new HashMap<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @TargetApi(Build.VERSION_CODES.M)
    void requestPermissions(@NonNull String[] permissions, @NonNull RequestPublisher publisher) {
        List<String> needRequestPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (isGranted(permission) || sdkVersionLowerThan23()) {
                publisher.onRequestPermissionsResult(new Permission(permission, true, false));
                continue;
            }
            if (isRevoked(permission) && !sdkVersionLowerThan23()) {
                publisher.onRequestPermissionsResult(new Permission(permission, false, false));
                continue;
            }
            List<RequestPublisher> list = mPermissionsMap.get(permission);
            if (list == null) {
                list = new ArrayList<>();
                mPermissionsMap.put(permission, list);
                //the permission needs requesting if the list is null
                needRequestPermissions.add(permission);
            }
            if (list.contains(publisher)) {
                continue;
            }
            list.add(publisher);
        }
        //if needRequestPermissions is empty, needRequestPermissionsArray will be
        //null, which can cause requestPermissions to throw an exception
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
            List<RequestPublisher> list = mPermissionsMap.get(permissions[i]);
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
    boolean isGranted(String permission) {
        return getActivity().checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    boolean isRevoked(String permission) {
        return getActivity().getPackageManager().isPermissionRevokedByPolicy(permission, getActivity().getPackageName());
    }

    boolean sdkVersionLowerThan23() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M;
    }
}
