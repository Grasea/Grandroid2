package com.grasea.grandroid.mvp.model;

/**
 * Created by Rovers on 2016/7/19.
 */
public enum DefaultValue {

    FALSE, TRUE, ZERO, ONE, EMPTY_STRING;

    public String getStringValue() {
        switch (this) {
            case FALSE:
                return "0";
            case TRUE:
                return "1";
            case ZERO:
                return "0";
            case ONE:
                return "1";
            case EMPTY_STRING:
                return "";
            default:
                return "";
        }
    }

    public Integer getIntValue() {
        switch (this) {
            case FALSE:
                return 0;
            case TRUE:
                return 1;
            case ZERO:
                return 0;
            case ONE:
                return 1;
            case EMPTY_STRING:
                return 0;
            default:
                return 0;
        }
    }

    public Long getLongValue() {
        switch (this) {
            case FALSE:
                return 0L;
            case TRUE:
                return 1L;
            case ZERO:
                return 0L;
            case ONE:
                return 1L;
            case EMPTY_STRING:
                return 0L;
            default:
                return 0L;
        }
    }

    public Double getDoubleValue() {
        return getIntValue().doubleValue();
    }

    public Float getFloatValue() {
        return getIntValue().floatValue();
    }

    public Boolean getBooleanValue() {
        switch (this) {
            case FALSE:
                return false;
            case TRUE:
                return true;
            case ZERO:
                return false;
            case ONE:
                return true;
            case EMPTY_STRING:
                return false;
            default:
                return false;
        }
    }
}
