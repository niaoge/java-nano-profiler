/*
 * Copyright 2015-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.helpinput.profile;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * The Class MethodTimer.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: MethodTimer.java, v 0.1 2016-7-14 13:10:22 niaoge Exp $$
 */
public class MethodTimer {

    long              startTime;
    Method            method;
    long              duration;

    MethodTimer       parent;
    List<MethodTimer> children = new ArrayList<MethodTimer>();

    public MethodTimer(Method method, MethodTimer parent) {
        this.parent = parent;
        this.method = method;
        this.startTime = System.nanoTime();

        if (parent != null) {
            parent.children.add(this);
        }
    }

    public long stopTime() {
        duration = System.nanoTime() - startTime;
        return duration;
    }

    public MethodTimer getParent() {
        return parent;
    }

    public long getDuration() {
        return duration;
    }

    public Method getMethod() {
        return method;
    }

    public List<MethodTimer> getChildren() {
        return children;
    }

}
