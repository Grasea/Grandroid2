package com.grasea.ble.data.parser;

/**
 * Created by Alan Ding on 2016/5/24.
 */
public class BleDataParserHelper {
//    public static BleDataParser<Integer> getIntegerDataParser() {
//        BleDataParser bleDataParser;
//        bleDataParser = new BleDataParser<Integer>() {
//            @Override
//            public Integer parse(final byte[] data) {
//                return data[3] & 0xFF |
//                        (data[2] & 0xFF) << 8 |
//                        (data[1] & 0xFF) << 16 |
//                        (data[0] & 0xFF) << 24;
//            }
//        };
//        return bleDataParser;
//    }

    public static BleDataParser<String> getStringDataParser() {
        BleDataParser bleDataParser;
        bleDataParser = new BleDataParser<String>() {
            @Override
            public String parse(final byte[] data) {
                if (data != null && data.length > 0) {
                    final StringBuilder stringBuilder = new StringBuilder(data.length);
                    for (byte byteChar : data)
                        stringBuilder.append(String.format("%02X", byteChar));
                    return stringBuilder.toString();
                }
                return null;
            }
        };
        return bleDataParser;
    }
}
