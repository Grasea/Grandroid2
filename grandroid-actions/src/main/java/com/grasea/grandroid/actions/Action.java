/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.view.View;

/**
 * 執行動作的指令單元，將操作包裝起來
 * 通常作為callback的用途
 * @author Rovers
 */
public class Action {

    /**
     * 指令名稱
     */
    protected String actionName;
    /**
     * 來源物件(一般為按鈕)
     */
    protected View src;
    /**
     * 需傳輸的參數
     */
    protected Object[] args;

    /**
     * 
     */
    public Action() {
    }

    /**
     * 
     * @param actionName 指令名稱
     */
    public Action(String actionName) {
        this.actionName = actionName;
    }

    /**
     * 
     * @param src 來源物件
     */
    public Action(View src) {
        this.src = src;
    }

    /**
     * 
     * @return 
     */
    public View getSrc() {
        return src;
    }

    /**
     * 
     * @param src
     * @return 回傳Action實體自己
     */
    public Action setSrc(View src) {
        this.src = src;
        return this;
    }

    /**
     * 
     * @return
     */
    public String getActionName() {
        return actionName;
    }

    /**
     * 
     * @param actionName
     * @return 回傳Action實體自己
     */
    public Action setActionName(String actionName) {
        this.actionName = actionName;
        return this;
    }

    /**
     * 
     * @param args 應為物件陣列，設過Args之後，一般會在execute函數裡使用args來取回所傳遞的資料
     * @return 回傳Action實體自己
     */
    public Action setArgs(Object ... args) {
        this.args = args;
        return this;
    }

    /**
     * 
     * @return
     */
    public Object[] getArgs() {
        return args;
    }
    
    /**
     * Action被執行時呼叫的函數
     * 一般應該要覆寫本函數，否則會什麼也不做
     * @return 暫時無意義
     */
    public boolean execute() {

        return true;
    }
}
