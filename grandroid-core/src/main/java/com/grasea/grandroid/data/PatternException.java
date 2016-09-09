/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.data;

public class PatternException extends Exception {

    protected String field;

    public PatternException() {
    }

    public PatternException(String field, String detailMessage) {
        super(detailMessage);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
