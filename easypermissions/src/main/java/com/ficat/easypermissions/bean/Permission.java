package com.ficat.easypermissions.bean;

/**
 * Created by ficat on 2018/5/12.
 */
public class Permission {
    public final String name;
    public final boolean granted;
    public final boolean shouldShowRequestPermissionRationale;

    public Permission(String name, boolean granted) {
        this(name, granted, false);
    }

    public Permission(String name, boolean granted, boolean shouldShowRequestPermissionRationale) {
        this.name = name;
        this.granted = granted;
        this.shouldShowRequestPermissionRationale = shouldShowRequestPermissionRationale;
    }

    @Override
    public String toString() {
        return "Permission{" +
                "name='" + name + '\'' +
                ", granted=" + granted +
                ", shouldShowRequestPermissionRationale=" + shouldShowRequestPermissionRationale +
                '}';
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 14 * result + (granted ? 1 : 0);
        result = 18 * result + (shouldShowRequestPermissionRationale ? 1 : 0);
        return result;
    }
}
