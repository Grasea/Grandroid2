/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;

/**
 *
 * @author Rovers
 */
public class ByteNotEnoughException extends Exception {

    public ByteNotEnoughException(int need, int actualSize) {
        super("handle byte occur error, need " + need + " bytes, but only " + actualSize + " length");
    }

    public ByteNotEnoughException() {
        super("handle byte occur error");
    }

    public ByteNotEnoughException(String message) {
        super(message);
    }

}
