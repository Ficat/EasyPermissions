# EasyPermissions
**声明**：本库参考了rxpermissions（[https://github.com/tbruyelle/RxPermissions](https://github.com/tbruyelle/RxPermissions "go to rxpermissions"))<br>
本库旨在简化安卓6.0及以上版本运行时权限申请流程，特点如下：<br>
    1.链式操作<br>
    2.相较于rxpermissions，无需依赖rxjava<br>
    3.若请求的权限未在manifest中注册，将抛出明确的异常<br>
    4.请求之间互不影响，即便每次请求的是相同的权限

## Gradle依赖

```gradle
dependencies {
    implementation 'com.github.Ficat:EasyPermissions:v1.0.0'
}
```
## 使用
1.创建easypermissions对象

```java
EasyPermissions easyPermissions = new EasyPermissions(activity);
```

2.请求权限

```java
//request方式，请求的所有权限被用户授权后返回true，否则返回false  
easyPermissions.request(Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE)
               .subscribe(new RequestSubscriber<Boolean>() {
                   @Override
                   public void onPermissionsRequestResult(Boolean granted) {
                       if (granted) {
                           //摄像头、拨打电话都被用户授权
                       } else {
                           //摄像头、拨打电话任意一项被拒绝或两者都被拒绝
                       }
                   }
               });
//requestEach方式
easyPermissions.requestEach(Manifest.permission.CAMERA,Manifest.permission.CALL_PHONE)
               .subscribe(new RequestSubscriber<Permission>() {
                   @Override
                   public void onPermissionsRequestResult(Permission permission) {
                       //权限名
                       String name = permission.name;
                       if (permission.granted) {
                           //权限被授予
                       } else {
                           //权限被拒绝
                       }
                   }
               });
```



