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
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helpinput.profile.ThreadMethodLinkHolder.ThreadMethodLink;

/**
 * The Class Monitor.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: Monitor.java, v 0.1 2016-7-14 13:10:35 niaoge Exp $$
 */
public class Monitor {
    final static Logger logger = LoggerFactory.getLogger(Monitor.class);

    public static void startMonitor(Method method) {
        MethodTimer methodTimer = MethodTimerStack.pushMethodTimer(method);

        ThreadMethodLinkHolder threadMethodLinkHolder = ThreadMethodLinkHolder.getCurrentThreadMethodProfilerHolderFromLocal();
        ThreadMethodLink threadMethodLink = threadMethodLinkHolder.getCurrentThreadMethodProfiler(method);

        if (threadMethodLink.getParent() == null) {
            MethodTimer parenMethodTimer = methodTimer.getParent();
            if (parenMethodTimer != null) {
                Method parentMethod = parenMethodTimer.getMethod();
                ThreadMethodLink parenThreadMethodProfiler = threadMethodLinkHolder.getThreadMethodProfiler(parentMethod);
                threadMethodLink.setParent(parenThreadMethodProfiler);
            }
        }
    }

    public static void stopMonitor(Throwable error) {
        MethodTimer methodTimer = MethodTimerStack.popMethodTimer();
        //stack is empty
        if (methodTimer.getParent() == null) {

            MethodTimer rootMethodTimer = methodTimer;
            ThreadMethodLinkHolder threadMethodLinkHolder = ThreadMethodLinkHolder
                .getCurrentThreadMethodProfilerHolderFromLocal();
            //清理threadLocal
            ThreadMethodLinkHolder.clearLocalThreadMethodLinkHolder();

            Map<Method, MethodProfiler> methodProfilerMap = MethodProfiler.getMethodProfilerMap(threadMethodLinkHolder);

            Method method = rootMethodTimer.getMethod();
            MethodProfiler methodProfiler = methodProfilerMap.get(method);
            methodProfiler.calcProfiler(rootMethodTimer, methodProfilerMap);
            ProcessExecutor.processMethodProfilerMap(threadMethodLinkHolder.getMethodOrderList(), methodProfilerMap);
        }
    }

}
