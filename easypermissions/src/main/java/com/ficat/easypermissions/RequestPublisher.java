package com.ficat.easypermissions;

import android.support.annotation.NonNull;

import com.ficat.easypermissions.bean.Permission;

import java.util.ArrayList;
import java.util.List;

public class RequestPublisher extends BaseRequestPublisher<RequestPublisher.Subscriber> {

    RequestPublisher(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        super(permissions, fragment);
    }

    public RequestPublisher autoRetryWhenUserRefuse(boolean autoRequestAgain, RequestAgainListener listener) {
        mAutoRequestAgain = autoRequestAgain;
        mRequestAgainListener = listener;
        return this;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        super.subscribe(subscriber);
        if (hasAllResult()) {
            publish(isAllGranted());
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
                publish(isAllGranted());
            }
        } else {
            publish(isAllGranted());
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
                if ((!p.granted && !p.shouldShowRequestPermissionRationale) || p.granted) {
                    list.add(p);
                }
            }
        }
        return list;
    }

    /**
     * Publish the request results to all of subscribers
     */
    private void publish(boolean grantAll) {
        for (Subscriber s : mSubscribers) {
            if (s != null) {
                s.onPermissionsRequestResult(grantAll, mResults);
            }
        }
    }

    public interface Subscriber extends BaseRequestPublisher.Subscriber {
        void onPermissionsRequestResult(boolean grantAll, List<Permission> results);
    }

}
