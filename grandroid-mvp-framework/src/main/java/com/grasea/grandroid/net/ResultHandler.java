/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.net;

import org.json.JSONObject;

/**
 *
 * @author Rovers
 */
public interface ResultHandler<T> {

    public void onAPIResult(T content);

    public void onAPIError(Throwable t);
}
