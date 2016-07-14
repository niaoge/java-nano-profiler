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
import java.util.Stack;

/**
 * The Class MethodTimerStack.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: MethodTimerStack.java, v 0.1 2016-7-14 13:10:29 niaoge Exp $$
 */
public class MethodTimerStack extends Stack<MethodTimer> {

    static ThreadLocal<MethodTimerStack> methodTimerStackLocal = new ThreadLocal<MethodTimerStack>();
    private static final long            serialVersionUID      = 1L;

    public static MethodTimer pushMethodTimer(Method method) {
        MethodTimerStack methodTimerStack = getMethodTimerStackFromLocal();
        return methodTimerStack.createAndPush(method);
    }

    public static MethodTimer popMethodTimer() {
        MethodTimerStack methodTimerStack = getMethodTimerStackFromLocal();
        MethodTimer methodTimer = methodTimerStack.pop();
        methodTimer.stopTime();
        return methodTimer;
    }

    private static MethodTimerStack getMethodTimerStackFromLocal() {
        MethodTimerStack methodTimerStack = methodTimerStackLocal.get();
        if (methodTimerStack == null) {
            methodTimerStack = new MethodTimerStack();
            methodTimerStackLocal.set(methodTimerStack);
        }
        return methodTimerStack;
    }

    public MethodTimer createAndPush(Method method) {
        MethodTimer parent = null;
        if (!this.isEmpty()) {
            parent = this.peek();
        }

        MethodTimer methodTimer = new MethodTimer(method, parent);
        return push(methodTimer);
    }

}
