/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;


import org.apache.commons.lang.ArrayUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.zip.CRC32;

/**
 *
 * @author Rovers
 */
public class ByteEditor {

    protected byte[] bytes;
    protected static boolean debug;
    public final static ByteOrder DEFAULT_ENDIAN = ByteOrder.LITTLE_ENDIAN;

    public ByteEditor(byte[] bytes) {
        this.bytes = bytes;
    }

    public ByteEditor(int length) {
        this.bytes = new byte[length];
    }

    public ByteEditor debugMode() {
        debug = true;
        return this;
    }

    public static ByteEditor clone(byte[] bytes) {
        byte[] b = new byte[bytes.length];
        System.arraycopy(bytes, 0, b, 0, bytes.length);
        return new ByteEditor(b);
    }

    public static ByteEditor wrap(byte[] bytes) {
        return new ByteEditor(bytes);
    }

    public static byte[] parseInt(int value, int length) throws ByteNotEnoughException {
        return parseInt(value, length, DEFAULT_ENDIAN);
    }

    public static byte[] parseInt(int value, int length, ByteOrder order) throws ByteNotEnoughException {
        if (value >= Math.pow(256, length)) {
            throw new ByteNotEnoughException();
        }
        ByteBuffer bb = ByteBuffer.allocate(4).order(order).putInt(value);
        if (length == 4) {
            return bb.array();
        } else {
            byte[] b = new byte[length];
            if (order == ByteOrder.BIG_ENDIAN) {
                System.arraycopy(bb.array(), Math.max(0, 4 - length), b, Math.max(0, length - 4), Math.min(length, 4));
            } else {
                System.arraycopy(bb.array(), 0, b, 0, Math.min(length, 4));
            }
            return b;
        }
    }

//        byte[] b = new byte[length];
//        if (value >= Math.pow(256, length)) {
//            throw new ByteNotEnoughException();
//        }
//        for (int j = 0; (j < 4) && (j < length); j++) {
//            b[j] = (byte) (value >> 8 * j & 0xFF);
//            System.out.println(j + " byte = " + b[j]);
//            //b[j] = (byte) (value >> 8 * j & 0xFF);
//        }
//        return b;
    public static byte[] parseLong(long value, int length) throws ByteNotEnoughException {
        return parseLong(value, length, DEFAULT_ENDIAN);
    }

    public static byte[] parseLong(long value, int length, ByteOrder order) throws ByteNotEnoughException {
        if (value >= Math.pow(256, length)) {
            throw new ByteNotEnoughException();
        }
        ByteBuffer bb = ByteBuffer.allocate(8).order(order).putLong(value);
        if (length == 8) {
            return bb.array();
        } else {
            byte[] b = new byte[length];
            if (order == ByteOrder.BIG_ENDIAN) {
                System.arraycopy(bb.array(), Math.max(0, 8 - length), b, Math.max(0, length - 8), Math.min(length, 8));
            } else {
                System.arraycopy(bb.array(), 0, b, 0, Math.min(length, 8));
            }
            return b;
        }
    }
//        byte[] b = new byte[length];
//        if (value >= Math.pow(256, length)) {
//            throw new ByteNotEnoughException();
//        }
//        for (int j = 0; (j < 8) && (j < length); j++) {
//            b[j] = (byte) (value >> 8 * j & 0xFF);
//            //b[j] = (byte) (value >> 8 * j & 0xFF);
//        }
//        return b;

    public static byte[] parseHex(String value, int length) throws ByteNotEnoughException {
        return parseHex(value, length, DEFAULT_ENDIAN);
    }

    public static byte[] parseHex(String value, int length, ByteOrder order) throws ByteNotEnoughException {
        byte[] b = hex2Byte(value);
        if (b.length > length) {
            throw new ByteNotEnoughException();
        } else if (b.length < length) {
            byte[] newBytes = new byte[length];
            System.arraycopy(b, 0, newBytes, 0, b.length);
            b = newBytes;
        }
        if (order != ByteOrder.nativeOrder()) {
            ArrayUtils.reverse(b);
        }
        return b;
    }

    public static byte[] parseString(String value, int length, ByteOrder order) throws ByteNotEnoughException, UnsupportedEncodingException {
        byte[] bytes = value.getBytes("UTF-8");
        byte[] b = new byte[length];
        if (order != ByteOrder.nativeOrder()) {
            ArrayUtils.reverse(bytes);;
        }
        if (debug) {
            System.out.println("parse string=" + value + ", need byte length " + bytes.length + ", actual " + length);
        }
        if (bytes.length > length) {
            throw new ByteNotEnoughException();
        }
        System.arraycopy(bytes, 0, b, 0, bytes.length);
        return b;
    }

    public static byte[] parseString(String value, int length) throws ByteNotEnoughException, UnsupportedEncodingException {
        return parseString(value, length, DEFAULT_ENDIAN);
    }

    public byte[] duplicate() {
        byte[] b = new byte[bytes.length];
        System.arraycopy(bytes, 0, b, 0, bytes.length);
        return b;
    }

    public ByteEditor append(byte[] b) {
        byte[] newbytes = new byte[bytes.length + b.length];
        System.arraycopy(bytes, 0, newbytes, 0, bytes.length);
        System.arraycopy(b, 0, newbytes, bytes.length, b.length);
        bytes = newbytes;
        return this;
    }

    public ByteEditor append(int value, int length) throws ByteNotEnoughException {
        append(parseInt(value, length));
        return this;
    }

    public ByteEditor append(int value, int length, ByteOrder order) throws ByteNotEnoughException {
        append(parseInt(value, length, order));
        return this;
    }

    public ByteEditor append(long value, int length) throws ByteNotEnoughException {
        append(parseLong(value, length));
        return this;
    }

    public ByteEditor append(long value, int length, ByteOrder order) throws ByteNotEnoughException {
        append(parseLong(value, length, order));
        return this;
    }

    public ByteEditor append(String value, int length) throws ByteNotEnoughException, UnsupportedEncodingException {
        append(parseString(value, length));
        return this;
    }

    public ByteEditor append(String value, int length, ByteOrder order) throws ByteNotEnoughException, UnsupportedEncodingException {
        append(parseString(value, length, order));
        return this;
    }

    public ByteEditor overwrite(byte[] b, int index) {
        if (b != null && b.length > 0) {
            byte[] newByte = new byte[Math.max(bytes.length, index + b.length)];
            if (bytes != null && bytes.length > 0) {
                System.arraycopy(bytes, 0, newByte, 0, Math.min(index, bytes.length));
            }
            System.arraycopy(b, 0, newByte, index, b.length);
            if (index + b.length < bytes.length) {
                System.arraycopy(bytes, index + b.length, newByte, index + b.length, bytes.length - index - b.length);
            }
            bytes = newByte;
        }
//        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
//        if (index > 0) {
//            buffer.put(Arrays.copyOfRange(bytes, 0, index), 0, index);
//        }
//        System.out.println("buffer.length=" + buffer.capacity() + ", b.length=" + b.length + ", index=" + index);
//        buffer.put(b, index, b.length);
//        if (bytes.length > index + b.length) {
//            buffer.put(Arrays.copyOfRange(bytes, index + b.length, bytes.length), index + b.length, bytes.length - index - b.length);
//        }
//        bytes = buffer.array();
        return this;
    }

    public long readLong(int index, int length) throws ByteNotEnoughException {
        return readLong(index, length, DEFAULT_ENDIAN);
    }

    public long readLong(int index, int length, ByteOrder order) throws ByteNotEnoughException {
        if (bytes.length < index + length) {
            throw new ByteNotEnoughException(index + length, bytes.length);
        }

        byte[] b = null;
        if (length <= 8) {
            b = new ByteEditor(8).overwrite(read(index, length), order == ByteOrder.BIG_ENDIAN ? 8 - length : 0).get();
        } else if (length > 8) {
            b = new byte[8];
            System.arraycopy(read(index + (order == ByteOrder.BIG_ENDIAN ? length - 8 : 0), 8), 0, b, 0, 8);
            //b = Arrays.copyOfRange(bytes, index, index + Math.min(8, length));
        }

        return ByteBuffer.wrap(b).order(order).getLong();
    }

    public int readInt(int index, int length) throws ByteNotEnoughException {
        return readInt(index, length, DEFAULT_ENDIAN);
    }

    public int readInt(int index, int length, ByteOrder order) throws ByteNotEnoughException {
        if (bytes.length < index + length) {
            System.out.println("bytes.length=" + bytes.length + ", need " + index + "+" + length);
            throw new ByteNotEnoughException();
        }

        byte[] b = null;
        if (length <= 4) {
            b = new ByteEditor(4).overwrite(read(index, length), order == ByteOrder.BIG_ENDIAN ? 4 - length : 0).get();
        } else if (length > 4) {
            b = new byte[4];
            System.arraycopy(read(index + (order == ByteOrder.BIG_ENDIAN ? length - 4 : 0), 4), 0, b, 0, 4);
            //b = Arrays.copyOfRange(bytes, index, index + Math.min(4, length));
        }
        return ByteBuffer.wrap(b).order(order).getInt();
    }

    public String readString(int index, int length) throws UnsupportedEncodingException, ByteNotEnoughException {
        return readString(index, length, DEFAULT_ENDIAN);
    }

    public String readString(int index, int length, ByteOrder order) throws UnsupportedEncodingException, ByteNotEnoughException {
        if (bytes.length < index + length) {
            throw new ByteNotEnoughException();
        }
        byte[] b = Arrays.copyOfRange(bytes, index, index + length);
        if (order != ByteOrder.nativeOrder()) {
            ArrayUtils.reverse(b);
        }
        return new String(b, "UTF-8").trim();
    }

    public String readHex(int index, int length) throws ByteNotEnoughException {
        return readHex(index, length, DEFAULT_ENDIAN);
    }

    public String readHex(int index, int length, ByteOrder order) throws ByteNotEnoughException {
        if (bytes.length < index + length) {
            throw new ByteNotEnoughException();
        }
        byte[] b = Arrays.copyOfRange(bytes, index, index + length);
        if (order != ByteOrder.nativeOrder()) {
            ArrayUtils.reverse(b);
        }
        return byte2Hex(b);
    }

    public byte[] read(int index, int length) throws ByteNotEnoughException {
        if (bytes.length < index + length) {
            throw new ByteNotEnoughException();
        }
        return Arrays.copyOfRange(bytes, index, index + length);
    }

    public int length() {
        return bytes.length;
    }

    public byte[] get() {
        return bytes;
    }

    public long getCRC32Value() {
        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return crc32.getValue();
    }

    public static String byte2Hex(byte b) {
        return String.format("%02x", b & 0xff);
    }

    public static String byte2Hex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

    public static byte[] hex2Byte(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public String toString(String format) throws Exception {
        ByteFormatter sf = new ByteFormatter(format);
        return sf.parse(bytes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {
            sb.append(byte2Hex(bytes[i]));
            sb.append(" ");
        }
        return sb.toString();
    }
}
