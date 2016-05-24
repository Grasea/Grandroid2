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
public class StringPart extends Part {

    public static final int TYPE = 3;

    public StringPart(int length) {
        super();
        this.length = length;
        this.variable = "?";
    }

    public StringPart(int length, String variable) {
        super();
        this.length = length;
        this.variable = variable;
    }

    @Override
    public Object read(ByteEditor editor, int index) throws Exception {
        return editor.readString(index, length, order);
    }

    @Override
    public byte[] encode(Object value) throws Exception {
        return ByteEditor.parseString((String) value, length, order);
    }
}
