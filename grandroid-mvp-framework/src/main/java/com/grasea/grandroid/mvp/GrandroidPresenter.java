package com.grasea.grandroid.mvp;

/**
 * Created by Rovers on 2016/5/7.
 */
public class GrandroidPresenter<C> {
    protected C contract;

    public void setContract(C contract) {
        this.contract = contract;
    }

    public C getContract() {
        return (C) contract;
    }


}
