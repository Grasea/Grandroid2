/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.grasea.grandroid.database;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 *
 * @author Rovers
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * 
     * @return
     */
    String value() default "app";//value=""
}
