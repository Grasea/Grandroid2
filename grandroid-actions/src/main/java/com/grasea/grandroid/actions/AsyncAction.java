/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grasea.grandroid.actions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

/**
 *
 * @author Rovers
 */
public abstract class AsyncAction<T> extends ContextAction {

    protected AsyncTask<Void, Void, T> task;
    protected T result;
    protected boolean multiThreadMode;
    protected boolean running;
    protected boolean cancelable;

    public AsyncAction() {
        this(null, null);
    }

    public AsyncAction(Context context) {
        this(context, null);
    }

    public AsyncAction(final Context context, String actionName) {
        super(context, actionName);
        //dialogType = DIALOG_PROGRESS;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public AsyncAction cancelable() {
        cancelable = true;
        return this;
    }


    public void setResult(T result) {
        this.result = result;
    }

    public void multiple() {
        this.multiThreadMode = true;
    }

    @Override
    public boolean execute() {
        if (!multiThreadMode && running) {
            return false;
        }
        if (!beforeExecution()) {
            return false;
        }
        running = true;

        task = new AsyncTask<Void, Void, T>() {
            @Override
            protected T doInBackground(Void... arg0) {
                AsyncAction.this.execute(context);
                return result;
            }

            @Override
            protected void onPreExecute() {
                result = null;
            }

            @Override
            protected void onPostExecute(T result) {
                if (!isCancelled()) {
                    afterExecution(result);
                }
                running = false;
            }

            @Override
            protected void onCancelled() {
                super.onCancelled();
                running = false;
                onCanceled();
            }
        };

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, (Void[]) null);
        } else {
            task.execute((Void[]) null);
        }
//        task.execute();
        return true;
    }

    public boolean interrupt() {
        return task.cancel(true);
    }

    public boolean beforeExecution() {
        return true;
    }

    public abstract void afterExecution(T result);

    public void onCanceled() {
        Log.e("grandroid", "AsyncAction is canceled");
    }
}
