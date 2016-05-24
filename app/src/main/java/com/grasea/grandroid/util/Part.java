/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;

import java.nio.ByteOrder;

/**
 *
 * @author Rovers
 */
public abstract class Part {

    String variable;
    int length;
    ByteOrder order;

    public Part() {
        order = ByteEditor.DEFAULT_ENDIAN;
    }

    public Part bigEndian() {
        this.order = ByteOrder.BIG_ENDIAN;
        return this;
    }

    public Part littleEndian() {
        this.order = ByteOrder.LITTLE_ENDIAN;
        return this;
    }

    public int length() {
        return length;
    }

    public String getVariable() {
        return variable;
    }

    public abstract Object read(ByteEditor editor, int index) throws Exception;

    public abstract byte[] encode(Object value) throws Exception;

}
