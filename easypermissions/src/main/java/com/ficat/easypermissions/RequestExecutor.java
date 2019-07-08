package com.ficat.easypermissions;

import android.support.annotation.NonNull;

import com.ficat.easypermissions.bean.Permission;

import java.util.ArrayList;
import java.util.List;

public class RequestExecutor extends BaseRequestExecutor<RequestExecutor.ResultReceiver> {

    RequestExecutor(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        super(permissions, fragment);
    }

    public RequestExecutor autoRetryWhenUserRefuse(boolean autoRequestAgain, RequestAgainListener listener) {
        mAutoRequestAgain = autoRequestAgain;
        mRequestAgainListener = listener;
        return this;
    }

    @Override
    public void result(ResultReceiver resultReceiver) {
        super.result(resultReceiver);
        if (hasAllResult()) {
            notifyResult(isAllGranted());
        }
    }

    @Override
    void onRequestPermissionsResult(Permission permission) {
        super.onRequestPermissionsResult(permission);
        if (!hasAllResult()) {
            return;
        }
        if (mAutoRequestAgain) {
            List<Permission> results = getResults(false);
            List<Permission> needRequestPermissions = getResults(true);
            if (needRequestPermissions.size() > 0) {
                mResults.clear();
                mResults.addAll(results);
                String[] needRequestArray = new String[needRequestPermissions.size()];
                for (int i = 0; i < needRequestPermissions.size(); i++) {
                    needRequestArray[i] = needRequestPermissions.get(i).name;
                }
                if (mRequestAgainListener != null) {
                    mRequestAgainListener.requestAgain(needRequestArray);
                }
                //只请求用户未勾选不再提示的权限，否则将出现一直请求的情况
                mPermissionsFragment.requestPermissions(needRequestArray, this);
            } else {
                notifyResult(isAllGranted());
            }
        } else {
            notifyResult(isAllGranted());
        }
    }

    private boolean isAllGranted() {
        for (Permission p : mResults) {
            if (!p.granted) {
                return false;
            }
        }
        return true;
    }

    private List<Permission> getResults(boolean needAndCanRequestAgain) {
        List<Permission> list = new ArrayList<>();
        for (Permission p : mResults) {
            if (needAndCanRequestAgain) {
                if (!p.granted && p.shouldShowRequestPermissionRationale) {
                    list.add(p);
                }
            } else {
                if (p.granted || !p.shouldShowRequestPermissionRationale) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    private void notifyResult(boolean grantAll) {
        if (mResultReceiver != null) {
            mResultReceiver.onPermissionsRequestResult(grantAll, mResults);
        }
    }

    public interface ResultReceiver extends BaseRequestExecutor.ResultReceiver {
        void onPermissionsRequestResult(boolean grantAll, List<Permission> results);
    }

}
