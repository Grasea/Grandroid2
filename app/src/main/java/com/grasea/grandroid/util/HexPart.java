/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;


/**
 * @author Rovers
 */
public class HexPart extends Part {

    public static final int TYPE = 4;

    public HexPart(int length) {
        super();
        this.length = length;
        this.variable = "?";
    }

    public HexPart(int length, String variable) {
        super();
        this.length = length;
        this.variable = variable;
    }

    @Override
    public Object read(ByteEditor editor, int index) throws ByteNotEnoughException {
        return editor.readHex(index, length, order);
    }

    @Override
    public byte[] encode(Object value) throws Exception {
        return ByteEditor.parseHex((String) value, length, order);
    }

}
