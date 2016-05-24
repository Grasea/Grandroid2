package com.grasea.grandroid;

/**
 * Created by Alan Ding on 2016/5/24.
 */

import com.grasea.grandroid.ble.Config;
import com.grasea.grandroid.ble.GrandroidBle;
import com.grasea.grandroid.ble.annotations.AliasName;
import com.grasea.grandroid.ble.annotations.OnBleDataReceive;
import com.grasea.grandroid.ble.data.parser.BleDataParserHelper;

import java.util.Arrays;
import java.util.Calendar;


public class HandShakeProtocol {

    public static final String serviceUUID = "49535343-fe7d-4ae5-8fa9-9fafd205e455";
    @AliasName(name = "Write Channel")
    public static final String wUUID = "49535343-8841-43f4-a8d4-ecbe34729bb3";
    @AliasName(name = "Listen Channel")
    public static final String rUUID = "49535343-1E4D-4BD9-BA61-23C647249616";
    private byte[] handshake = new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0x02, (byte) 0x30, (byte) 0x03};
    private byte[] evenOk = new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0x02, (byte) 0x50, (byte) 0x06, (byte) 0x03};

    private String address;
    private String userCode = "1";

    private boolean hasLoadUserData = false;

    public HandShakeProtocol(String address, String userCode) {
        this.address = address;
        if (userCode.isEmpty()) {
            this.userCode = "1";
        } else {
            this.userCode = userCode;
        }
    }

    public void handshake() {
        Config.logd("send handshake:" + BleDataParserHelper.getStringDataParser().parse(handshake));
        boolean send = GrandroidBle.with(address).findService(serviceUUID).getChannel(wUUID).send(handshake);
        Config.loge("send success:" + send);
    }

    @OnBleDataReceive(serviceUUID = serviceUUID, characteristicUUID = rUUID)
    public void onReceiveData(byte[] data, String from) {
        String byebyeString = "EB9002501B03";
        Config.loge("[" + from + "]data income : " + Arrays.toString(data) + " / " + BleDataParserHelper.getStringDataParser().parse(data));
        ResultParser parser = new ResultParser(data);
        if (byebyeString.equals(BleDataParserHelper.getStringDataParser().parse(data))) {
            Config.loge("接收到裝置離開指令");
        } else if (parser.order.equals("40")) {
            byte[] setTime = createTimeProtocol();
            Config.loge("send time :" + BleDataParserHelper.getStringDataParser().parse(setTime));
            GrandroidBle.with(address).findService(serviceUUID).getChannel(wUUID).send(setTime);
        } else if (parser.order.equals("50")) {
            if (!hasLoadUserData) {
                //先做讀取User資料
                boolean isSuccessSetTime = parser.getHexString(4).equals("06");
                Config.logi("時間設定成功:" + isSuccessSetTime);
                if (isSuccessSetTime) {
                    byte[] loadUserDataProtocol = createLoadUserDataProtocol(userCode);
                    Config.loge("load user[" + userCode + "]data  :" + BleDataParserHelper.getStringDataParser().parse(loadUserDataProtocol));
                    GrandroidBle.with(address).findService(serviceUUID).getChannel(wUUID).send(loadUserDataProtocol);
                    //result :EB 90 02 44 0A 02 C4 07 09 0A F4 01 9F 06 00 7A 02 03
                    hasLoadUserData = true;
                    //確定資料是沒有不同回傳OK
                    GrandroidBle.with(address).findService(serviceUUID).getChannel(wUUID).send(evenOk);
                }
            }
        }
    }

    public byte[] createTimeProtocol() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int date = Calendar.getInstance().get(Calendar.
                DAY_OF_MONTH);
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        int minute = Calendar.getInstance().get(Calendar.MINUTE);
        String yearHex = Integer.toHexString(year);
//        Config.loge("year:" + year + ", month:" + month + ", date:" + date + ", h:" + hour + ", m:" + minute);
        if (yearHex.length() == 3) {
            yearHex = "0" + yearHex;
        }
//        Config.loge("year:" + year + ", yearHex:" + yearHex);

        String monthHex = Integer.toHexString(month);
        String dateHex = Integer.toHexString(date);
        String hourHex = Integer.toHexString(hour);
        String minHex = Integer.toHexString(minute);

        int sumValue = Integer.parseInt(yearHex.substring(2, yearHex.length()), 16) + Integer.parseInt(yearHex.substring(0, 2), 16) + month + date + hour + minute;
        String sumHex = Integer.toHexString(sumValue);
        if (sumHex.length() == 3) {
            sumHex = "0" + sumHex;
        }
//        Config.loge("sumValue:" + sumValue + " , sumHex:" + sumHex);
        return new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0x02, (byte) 0x31, (byte) 0x06,
                byteFromHex(yearHex.substring(2, yearHex.length())),
                byteFromHex(yearHex.substring(0, 2)),
                byteFromHex(monthHex),
                byteFromHex(dateHex),
                byteFromHex(hourHex),
                byteFromHex(minHex),
                byteFromHex(sumHex.substring(2, sumHex.length())),
                byteFromHex(sumHex.substring(0, 2)),
                (byte) 0x03};
    }

    public byte[] createLoadUserDataProtocol(String userCode) {
        return new byte[]{(byte) 0xEB, (byte) 0x90, (byte) 0x02, (byte) 0x33, Byte.parseByte(userCode, 10),
                (byte) 0x03};
    }

    public static byte byteFromHex(String s) throws IllegalArgumentException {
        int i = Integer.parseInt(s, 16);
        if (i < 0 || i > 255)
            throw new IllegalArgumentException("input string " + s + " does not fit into a Byte");
        return (byte) i;
    }

    public class ResultParser {
        public byte[] data;
        private String afterParseString = "";
        private String order;
        private String value;

        public ResultParser(byte[] data) {
            this.data = data;
            afterParseString = BleDataParserHelper.getStringDataParser().parse(data);
            order = afterParseString.substring(6, 8);
            if ("40".equals(order)) {
                value = unHex(afterParseString.substring(10, 30));
//                    value = new String(new byte[]{(byte) 0x46, (byte) 0x57, (byte) 0x50, (byte) 0x2D, (byte) 0x35, (byte) 0x31, (byte) 0x30, (byte) 0x2D, (byte) 0x42, (byte) 0x54}, "UTF-8");
                Config.logi("value:" + value);
            }
        }

        public String getHexString(int index) {
            try {
                return afterParseString.substring(index * 2, index * 2 + 2);
            } catch (Exception e) {
                return "";
            }
        }

        public String unHex(String arg) {

            String str = "";
            for (int i = 0; i < arg.length(); i += 2) {
                String s = arg.substring(i, (i + 2));
                int decimal = Integer.parseInt(s, 16);
                str = str + (char) decimal;
            }
            return str;
        }
    }
}
