package com.ficat.easypermissions;

import android.support.annotation.NonNull;

import com.ficat.easypermissions.bean.Permission;


public class RequestEachPublisher extends BaseRequestPublisher<RequestEachPublisher.Subscriber> {

    RequestEachPublisher(@NonNull String[] permissions, @NonNull PermissionsFragment fragment) {
        super(permissions, fragment);
    }

    public RequestEachPublisher autoRetryWhenUserRefuse(boolean autoRequestAgain, RequestAgainListener listener) {
        mAutoRequestAgain = autoRequestAgain;
        mRequestAgainListener = listener;
        return this;
    }

    @Override
    public void subscribe(Subscriber subscriber) {
        super.subscribe(subscriber);
        for (Permission p : mResults) {
            subscriber.onPermissionsRequestResult(p);
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
                publish(permission);
            }
        } else {
            publish(permission);
        }
    }

    /**
     * Publish the request results to all of subscribers
     */
    private void publish(Permission permission) {
        for (Subscriber s : mSubscribers) {
            if (s != null) {
                s.onPermissionsRequestResult(permission);
            }
        }
    }

    public interface Subscriber extends BaseRequestPublisher.Subscriber {
        void onPermissionsRequestResult(Permission permission);
    }
}
