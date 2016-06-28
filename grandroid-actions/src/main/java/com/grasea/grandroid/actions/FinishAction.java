/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

/**
 *
 * @author Rovers
 */
public class FinishAction extends Action {

    /**
     * 
     */
    public FinishAction() {
    }

    /**
     * 
     * @param actionName
     */
    public FinishAction(String actionName) {
        super(actionName);
    }

    /**
     * 
     * @return
     */
    @Override
    public boolean execute() {
        int pid = android.os.Process.myPid();
        android.os.Process.killProcess(pid);
        return true;
    }
}
