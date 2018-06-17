package com.ficat.easypermissions;

/**
 * Created by ficat on 2018-05-12.
 */

public interface RequestSubscriber<T> {
    void onPermissionsRequestResult(T t);
}
