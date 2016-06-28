/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;

/**
 * 繼承自Action，當欲執行的程式碼需要用到context時，應改用ContextAction 本類別設計為Abstract
 * Class，主要是為了保證使用時覆寫正確的函數
 *
 * @author Rovers
 */
public abstract class ContextAction extends Action {

    /**
     *
     */
    protected Context context;
    protected Action nextAction;

    /**
     *
     * @param context
     */
    public ContextAction(Context context) {
        this.context = context;
    }

    /**
     *
     * @param context
     * @param actionName
     */
    public ContextAction(Context context, String actionName) {
        super(actionName);
        this.context = context;
    }

    /**
     * 一般不需覆寫這個函數
     *
     * @return
     */
    @Override
    public boolean execute() {
        boolean result = this.execute(context);
        if (nextAction != null && result) {
            result = result && nextAction.execute();
        }
        return result;
    }

    public ContextAction concat(Action nextAction) {
        this.nextAction = nextAction;
        return this;
    }

    /**
     * 應覆寫此函數，從此函數中取得Context物件
     *
     * @param context
     * @return 暫時無意義
     */
    public abstract boolean execute(Context context);
}
