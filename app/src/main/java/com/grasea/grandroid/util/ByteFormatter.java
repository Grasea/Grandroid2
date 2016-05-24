/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.util;

import java.util.ArrayList;

/**
 *
 * @author Rovers
 */
public class ByteFormatter {

    protected String format;
    protected ArrayList<Part> parts;

    public ByteFormatter(String format) {

        this.format = format;
        parts = new ArrayList<Part>();
        String[] s = format.split(",");
        for (int i = 0; i < s.length; i++) {
            if (s[i].charAt(0) == 's') {
                parts.add(new StringPart(Integer.parseInt(s[i].substring(1))));
            } else if (s[i].charAt(0) == 'i') {
                parts.add(new IntPart(Integer.parseInt(s[i].substring(1))));
            } else if (s[i].charAt(0) == 'l') {
                parts.add(new LongPart(Integer.parseInt(s[i].substring(1))));
            } else if (s[i].charAt(0) == 'h') {
                parts.add(new HexPart(Integer.parseInt(s[i].substring(1))));
            }
        }
    }

    public String parse(byte[] bytes) throws Exception {
        StringBuilder sb = new StringBuilder();
        ByteEditor be = new ByteEditor(bytes);
        int pos = 0;
        for (Part p : parts) {
            if (sb.length() > 0) {
                sb.append(" ");
            }
            sb.append(p.read(be, pos));
            pos += p.length();
        }
        return sb.toString();
    }

}
