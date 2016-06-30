# Grandroid2

Grandroid2是一個開源的Android Library. 基於 [Grandroid](https://github.com/Grasea/Grandroid) 重新整合，更合理的拆分成多個Dependency，新版將更輕量化.

Grandroid2 is a open source library for Android. More stronger than [Grandroid](https://github.com/Grasea/Grandroid).

##功能 Features
**Grandroid2 包含Modules:**

 1. **Core**: 開發上常用之核心物件.

 2. **MVP-Framework(使用Annotation技術)**: 利用Annotation技術Presenter及View之間的setup更加快速及簡便.

 3. **Bluetooth LE Library(使用Annotation技術)**: 利用Annotation技術，快速實現BLE Device搜尋、裝置連接、監聽資料以及發送資料.

 4. **ORM Database**: 簡單易用的ORM Database library.

## 如何使用 Usage
Step1. Add Repository in build.gradle:

```
      repositories {
    	        maven { url 'https://dl.bintray.com/ch8154/maven' }
        }
```

Step2. Add dependency in build.gradle what library you want.


###Core引用了
1. com.android.support:appcompat-v7:23.3.0
2. com.android.support:recyclerview-v7:23.4.0

```
    dependencies {
	    compile 'com.grasea:grandroid-core:1.0.1'
    }
```
###MVP-Framework引用了
1. com.grasea:grandroid-core:1.0.1
2. com.android.support:appcompat-v7:23.3.0
3. com.mcxiaoke.volley:library:1.0.+

```
    dependencies {
	    compile 'com.grasea:grandroid-mvp-framework:1.0.1-beta6'
    }
```
###Bluetooth LE Library引用了
1. com.android.support:appcompat-v7:23.3.0

```
    dependencies {
	    compile 'com.grasea:grandroid-ble:1.0.2'
    }
```
**Init at Activity or Application onCreate().**
```
   GrandroidBle.init(this, new GrandroidConnectionListener() {
              @Override
              public void onGattDiscovered(BleDevice controller) {
                  super.onGattDiscovered(controller);
              }
  
              @Override
              public void onDeviceReady(@Nullable BleDevice controller) {
                  //On connected device.
                  ArrayList<GattServiceChannelHandler> serviceHandlers = controller.getServiceHandlers();
                  address = controller.getAddress();
                  handShakeProtocol = new HandShakeProtocol(address, userCode);
                  GrandroidBle.getInstance().getDeviceScanner().stopScan();
                  GrandroidBle.getInstance().bindName(handShakeProtocol);
                  GrandroidBle.getInstance().bind(address, protocolObject);
              }
  
              @Override
              public void onDeviceDisconnected(@Nullable BleDevice controller) {
                  GrandroidBle.getInstance().getDeviceScanner().startScan();
              }
  
              @Override
              public void onFailed(int errorCode) {
              }
          });
```


**Scan device**
```
   GrandroidBle.getInstance().getDeviceScanner()
   .setRetry(10)
   .filterName("Device Name")
   .setScanResultHandler(new AutoConnectScanResultHandler(60000) {
           @Override
           public void onDeviceFailed(int errorCode) {
                   Config.logi("onDeviceScan Filed:" + errorCode);
           }
   }).startScan();
   
   //Then do something after scan at GrandroidConnectionListener if GrandroidBle found device you want.
```
**Listen notify characteristic**
```
    public calss BleProtocol{

      public static final String serviceUUID = "49535343-fe7d-4ae5-8fa9-9fafd205e455";
      public static final String rUUID = "49535343-1E4D-4BD9-BA61-23C647249616";
      public static final String wUUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
      public BleProtocol(String deviceAddress) {
          GrandroidBle.getInstance().bind(deviceAddress, this);
      }

      @OnBleDataReceive(serviceUUID = serviceUUID, characteristicUUID = rUUID)
       public void onReceiveData(byte[] data, String fromWhatCharacteristicUUID) {
          //Single characteristic.
       }
    
      @OnBleDataReceive(serviceUUID = serviceUUID, characteristicUUIDs = {rUUID,wUUID})
      public void onReceiveData(byte[] data, String fromWhatCharacteristicUUID) {
          //Mutiple characteristics.
      }
     }
```
**Send data.**
```
    try{
        GrandroidBle.with(deviceAddress).findService(serviceUUID).getChannel(wUUID).send(loadUserDataProtocol);
    }catch(NullPointerException e){
    
    }
    
```
or
```
    try{
        GrandroidBle.with(deviceAddress).send(serviceUUID,wUUID,loadUserDataProtocol);
    }catch(NullPointerException e){
    
    }
    
```
###ORM Database引用了
1. com.grasea:grandroid-core:1.0.1

```
    dependencies {
	    compile 'com.grasea:grandroid-database:2.0.0'
    }
```