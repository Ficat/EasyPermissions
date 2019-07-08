package com.ficat.easypermissions;

import android.support.annotation.NonNull;

import com.ficat.easypermissions.bean.Permission;


public class RequestEachExecutor extends BaseRequestExecutor<RequestEachExecutor.ResultReceiver> {

    RequestEachExecutor(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        super(permissions, fragment);
    }

    public RequestEachExecutor autoRetryWhenUserRefuse(boolean autoRequestAgain, RequestAgainListener listener) {
        mAutoRequestAgain = autoRequestAgain;
        mRequestAgainListener = listener;
        return this;
    }

    @Override
    public void result(ResultReceiver resultReceiver) {
        super.result(resultReceiver);
        for (Permission p : mResults) {
            resultReceiver.onPermissionsRequestResult(p);
        }
    }

    @Override
    void onRequestPermissionsResult(Permission permission) {
        super.onRequestPermissionsResult(permission);
        if (mAutoRequestAgain) {
            if (!permission.granted && permission.shouldShowRequestPermissionRationale) {
                mResults.remove(permission);
                String[] permissionArray = new String[]{permission.name};
                if (mRequestAgainListener != null) {
                    mRequestAgainListener.requestAgain(permissionArray);
                }
                mPermissionsFragment.requestPermissions(permissionArray, this);
            } else {
                notifyResult(permission);
            }
        } else {
            notifyResult(permission);
        }
    }

    private void notifyResult(Permission permission) {
        if (mResultReceiver != null) {
            mResultReceiver.onPermissionsRequestResult(permission);
        }
    }

    public interface ResultReceiver extends BaseRequestExecutor.ResultReceiver {
        void onPermissionsRequestResult(Permission permission);
    }
}
