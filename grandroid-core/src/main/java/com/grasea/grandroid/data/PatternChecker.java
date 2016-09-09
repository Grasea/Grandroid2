/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.data;

import java.util.Calendar;
import java.util.regex.Pattern;

/**
 *
 * @author Rovers
 */
public class PatternChecker {

    protected String text;
    protected String fieldName;
    protected boolean valid;
    protected String alert;

    public PatternChecker(String text) {
        this.text = text;
        fieldName = "";
        alert = null;
    }

    public PatternChecker(String text, String fieldName) {
        this.text = text;
        this.fieldName = fieldName;
        alert = null;
    }

    public boolean isValid() {
        return valid;
    }

    public PatternChecker alert(String alert) {
        this.alert = String.format(alert, fieldName);
        return this;
    }

    public PatternChecker noEmpty() throws PatternException {
        if (text==null || text.isEmpty()) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "不可留白");
        }
        return this;
    }

    public PatternChecker length(int length) throws PatternException {
        if (!text.matches("\\w{" + length + "}")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "長度需為" + length + "個字");
        }
        return this;
    }

    public PatternChecker min(int minLength) throws PatternException {
        if (!text.matches("\\w{" + minLength + ",}")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "至少需要" + minLength + "個字");
        }
        return this;
    }

    public PatternChecker max(int maxLength) throws PatternException {
        if (!text.matches("\\w{0," + maxLength + "}")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "不得超過" + maxLength + "個字");
        }
        return this;
    }

    public PatternChecker numerical() throws PatternException {
        if (!text.matches("\\d*")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "含有非數字的字元");
        }
        return this;
    }

    public PatternChecker email() throws PatternException {
        if (!text.matches("[A-Z0-9a-z._%+-]+@[A-Z0-9a-z._%+-]+(\\.[A-Za-z0-9._%+-]{2,})$")) {
//            if (!text.matches("^[_A-Za-z0-9-]+([_A-Za-z0-9-.]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")) {
//        if (!text.matches("^[A-Za-z0-9_%+-.]@[A-Za-z0-9_%+-]+(\\.[A-Za-z0-9._%+-]+)*(\\.[A-Za-z._%+-]{2,})$")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "格式不正確");
        }
        return this;
    }

    public PatternChecker birthdate(String delimiter) throws PatternException {
        date(delimiter);
        if (Integer.valueOf(text.split(delimiter)[0]) < 1912 || Integer.valueOf(text.split(delimiter)[0]) > Calendar.getInstance().get(Calendar.YEAR)) {
            throw new PatternException(fieldName, alert != null ? alert : "年份不正確");
        }
        return this;
    }

    public PatternChecker date(String delimiter) throws PatternException {
        if (!text.matches("^((19|20)\\d\\d)" + delimiter + "(0[1-9]|1[012])" + delimiter + "(0[1-9]|[12][0-9]|3[01])$")) {
            valid = false;
            throw new PatternException(fieldName, alert != null ? alert : "格式不正確");
        }
        return this;
    }

    public static boolean isMatch(String str, String regex) {
        return str.matches(regex);
    }

    public static boolean isDate(String str) {
        return isDate(str, "/");
    }

    public static boolean isDate(String str, String delimiter) {
        return str.matches("^((19|20)\\d\\d)" + delimiter + "(0?[1-9]|1[012])" + delimiter + "(0?[1-9]|[12][0-9]|3[01])$");
    }

    public static boolean isPureNumber(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isPureNumber(String str, int digit) {
        Pattern pattern = Pattern.compile("\\d{" + digit + "}");
        return pattern.matcher(str).matches();
    }

    public static boolean isLowerLetter(String str) {
        Pattern pattern = Pattern.compile("[a-z]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isUpperLetter(String str) {
        Pattern pattern = Pattern.compile("[A-Z]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isLetter(String str) {
        Pattern pattern = Pattern.compile("[a-zA-Z]*");
        return pattern.matcher(str).matches();
    }

    public static boolean isEmail(String str) {
        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
        return pattern.matcher(str).matches();
    }
}
