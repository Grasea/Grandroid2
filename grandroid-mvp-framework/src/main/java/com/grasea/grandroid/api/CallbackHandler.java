package com.grasea.grandroid.api;

import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Alan Ding on 2016/10/12.
 */

public interface CallbackHandler {
    /**
     *
     * @param call
     * @param response
     * @return true: to call invoke. false: do not to call invoke.
     */
    boolean onCallback(Call call, Response response);
}
