package com.grasea.grandroid.ble.annotations;


import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.GrandroidBle;
import com.grasea.grandroid.ble.data.BleDataDivider;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by Alan Ding on 2016/5/16.
 */
public class MethodBinder implements Parsable, BleDataDivider {
    public class MethodManager {
        private String serviceUUID;
        private HashMap<String, Method> methodHashMap;

        public MethodManager(String serviceUUID) {
            this.serviceUUID = serviceUUID;
            methodHashMap = new HashMap<>();
        }

        public HashMap<String, Method> getMethodHashMap() {
            return methodHashMap;
        }

        public String getServiceUUID() {
            return serviceUUID;
        }
    }

    private Object clazz;
    private String deviceAddress;
    private HashMap<String, MethodManager> dataMethodHashMap;
//    private HashMap<String, Method> connectionMethodHashMap;

    public static HashMap<String, MethodBinder> binderMap;

    private MethodBinder(String address) {
        this.deviceAddress = address;
        dataMethodHashMap = new HashMap<>();
//        connectionMethodHashMap = new HashMap<>();
    }

    public static void bind(String deviceAddress, Object object) {
        MethodBinder binder;
        if (binderMap == null) {
            binderMap = new HashMap<>();
        }
        if (binderMap.containsKey(deviceAddress)) {
            binder = binderMap.get(deviceAddress);
        } else {
            binder = new MethodBinder(deviceAddress);
            binderMap.put(deviceAddress, binder);
        }
        try {
            Config.logi("[" + deviceAddress + "]" + " Bind Method");
            binder.startBindClass(object);
        } catch (Exception e) {
            Config.loge(e);
        }
    }

    private void doUnbind() {
        dataMethodHashMap.clear();
    }

    public static void unbindAll() {
        if (getBinders() != null && !getBinders().isEmpty()) {
            Iterator<MethodBinder> iterator = getBinders().values().iterator();
            while (iterator.hasNext()) {
                MethodBinder binder = iterator.next();
                binder.doUnbind();
                iterator.remove();
            }
        }
    }

    public static void unbind(String deviceAddress) {
        MethodBinder binder = getBinder(deviceAddress);
        if (getBinders() != null && binder != null) {
            binder.doUnbind();
            getBinders().remove(deviceAddress);
        }
    }

    public static boolean isBinded(String deviceAddress) {
        return binderMap.containsKey(deviceAddress);
    }

    public static MethodBinder getBinder(String deviceAddress) {
        if (binderMap == null && binderMap.get(deviceAddress) == null) {
            new UnbindException("Need to call MethodBinder.bind('deviceAddress') first.");
        }
        return binderMap.get(deviceAddress);
    }

    public static HashMap<String, MethodBinder> getBinders() {
        return binderMap;
    }

    @Override
    public void startBindClass(final Object object) throws Exception {
        dataMethodHashMap.clear();
//        connectionMethodHashMap.clear();
        //
        clazz = object;
        Method[] methods = object.getClass().getMethods();
        Config.logi("Find " + methods.length + " of Methods.");
        for (Method method : methods) {
            boolean annotationPresent = method.isAnnotationPresent(OnBleDataReceive.class);
            Config.logi("method name:" + method.getName() + " is OnBleDataReceive Annotation ? " + annotationPresent);

            if (annotationPresent) {
                OnBleDataReceive bleDataReceive = method.getAnnotation(OnBleDataReceive.class);
                String serviceUUID = bleDataReceive.serviceUUID().toLowerCase();
                String channelUUID = bleDataReceive.characteristicUUID().toLowerCase();
                String[] channelUUIDs = bleDataReceive.characteristicUUIDs();

                if ((channelUUIDs != null && channelUUIDs.length == 0 && channelUUID.isEmpty()) || serviceUUID.isEmpty()) {
                    throw new Exception("Service/channel's UUID are empty.");
                }
                method.setAccessible(true);
                Type[] parameters = method.getParameterTypes();
                if (parameters == null || parameters.length < 1) {
                    throw new Exception("Method doesn't has one of 'byte[]' parameter.");
                }
                boolean isSingleChannelUUID = channelUUIDs.length == 0 && !channelUUID.isEmpty();
                if (!hasParameterType(method, byte[].class)) {
                    throw new Exception("[" + object.getClass().getName() + "] Method " + method.getName() + " need at least one 'byte[]' parameter.");
                }
                Config.logd("Is SingleChannelUUID? " + isSingleChannelUUID);
                if (!dataMethodHashMap.containsKey(serviceUUID)) {
                    MethodManager methodManager = new MethodManager(serviceUUID);
                    if (isSingleChannelUUID) {
                        methodManager.getMethodHashMap().put(channelUUID, method);
                        boolean startSuccess = startListenNotificationChannel(serviceUUID, channelUUID);
                        Config.logi("Bind success UUID:" + channelUUID + ", Method Name:" + method.getName() + ", isSuccess:" + startSuccess);
                    } else {
                        for (String cUUId : channelUUIDs) {
                            methodManager.getMethodHashMap().put(cUUId.toLowerCase(), method);
                            boolean startSuccess = startListenNotificationChannel(serviceUUID, cUUId);
                            Config.logi("Bind success UUID:" + cUUId.toLowerCase() + ", Method Name:" + method.getName() + ", isSuccess:" + startSuccess);
                        }
                    }
                    dataMethodHashMap.put(serviceUUID, methodManager);

                } else {
                    if (isSingleChannelUUID) {
                        dataMethodHashMap.get(serviceUUID).getMethodHashMap().put(channelUUID, method);
                        boolean startSuccess = startListenNotificationChannel(serviceUUID, channelUUID);
                        Config.logi("Start Listen UUID:" + channelUUID + ", Method Name:" + method.getName() + ", isSuccess:" + startSuccess);
                    } else {
                        for (String cUUId : channelUUIDs) {
                            dataMethodHashMap.get(serviceUUID).getMethodHashMap().put(cUUId, method);
                            boolean startSuccess = startListenNotificationChannel(serviceUUID, cUUId);
                            Config.logi("Bind success UUID:" + channelUUID + ", Method Name:" + method.getName() + ", isSuccess:" + startSuccess);
                        }
                    }
                }
            }
        }
    }

    private boolean startListenNotificationChannel(String serviceUUID, String channelUUID) {
        try {
            return GrandroidBle.with(deviceAddress).findService(serviceUUID).getChannel(channelUUID).startListenBleData();
        } catch (Exception e) {
            Config.loge("Start Listen Notification Channel in Binder has failed" + e.toString());
        }
        return false;
    }

    public static boolean hasParameterType(Method method, Class clazz) {
        Type[] parameters = method.getParameterTypes();
        boolean hasType = false;
        for (Type type : parameters) {
            if (type.equals(clazz)) {
                hasType = true;
                break;
            }
        }
        return hasType;
    }

    public static boolean hasParameterType(Method method, Class clazz, int position) {
        Type[] parameters = method.getParameterTypes();
        if (parameters.length > position) {
            return parameters[position].equals(clazz);
        }
        return false;
    }

    public static int getParameterCount(Method method) {
        return method.getParameterTypes().length;
    }

    @Override
    public void onBLEDataReceive(String serviceUUID, String channelUUID, byte[] data) {
        Method method = dataMethodHashMap.get(serviceUUID.toLowerCase()).getMethodHashMap().get(channelUUID.toLowerCase());
        if (method == null) {
            return;
        }
        try {
            if (hasParameterType(method, String.class, 1)) {
                method.invoke(clazz, data, channelUUID);
            } else {
                method.invoke(clazz, data);
            }
        } catch (IllegalAccessException e) {
            Config.loge(e);
        } catch (InvocationTargetException e) {
            Config.loge(e);
        }
    }


    /**
     * Created by Alan Ding on 2016/5/16.
     */
    public static class UnbindException extends Exception {
        public UnbindException(String detailMessage) {
            super(detailMessage);
        }
    }
}
