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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import com.helpinput.profile.ThreadMethodLinkHolder.ThreadMethodLink;

/**
 * The Class MethodProfiler.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: MethodProfiler.java, v 0.1 2016-7-14 13:10:06 niaoge Exp $$
 */
public class MethodProfiler {

    static Map<MethodList, Map<Method, MethodProfiler>> methodProfilerListMap = new ConcurrentHashMap<MethodList, Map<Method, MethodProfiler>>();

    Method                                              method;
    int                                                 level                 = 0;
    AtomicLong                                          timers                = new AtomicLong(0);
    AtomicLong                                          duration              = new AtomicLong(0);
    AtomicLong                                          max                   = new AtomicLong(0);
    AtomicLong                                          min                   = new AtomicLong(0);
    AtomicLong                                          durationSelf          = new AtomicLong(0);
    AtomicLong                                          maxSelf               = new AtomicLong(0);
    AtomicLong                                          minSelf               = new AtomicLong(0);

    public MethodProfiler(Method method, int level) {
        this.method = method;
        this.level = level;
    }

    public static void cleanHistoryByMethodList(MethodList methodList) {
        methodProfilerListMap.remove(methodList);
    }

    public static void cleanHistoryAll() {
        methodProfilerListMap.clear();
    }

    public static Map<Method, MethodProfiler> getMethodProfilerMap(ThreadMethodLinkHolder threadMethodLinkHolder) {
        MethodList methodList = threadMethodLinkHolder.getMethodOrderList();
        Map<Method, MethodProfiler> methodProfilerMap = methodProfilerListMap.get(methodList);
        if (methodProfilerMap == null) {
            Map<Method, ThreadMethodLink> threadMethodLinkMap = threadMethodLinkHolder.getThreadMethodLinkMap();
            methodProfilerMap = new HashMap<Method, MethodProfiler>(methodList.size());
            for (Method method : methodList) {
                ThreadMethodLink threadMethodLink = threadMethodLinkMap.get(method);
                MethodProfiler methodProfiler = new MethodProfiler(method, threadMethodLink.getLevel());
                methodProfilerMap.put(method, methodProfiler);
            }
            methodProfilerListMap.put(methodList, methodProfilerMap);
        }
        return methodProfilerMap;
    }

    private void safeSetAtomic(AtomicLong atomic, long x, boolean isMax) {
        long expect, update;
        do {
            expect = atomic.get();
            if (isMax) {
                update = Math.max(expect, x);
            } else {
                update = expect == 0 ? x : Math.min(expect, x);
            }
        } while (!atomic.compareAndSet(expect, update));
    }
    


    public void calcProfiler(MethodTimer methodTimer, Map<Method, MethodProfiler> methodProfilerMap) {
        timers.incrementAndGet();
        long durNano = methodTimer.getDuration();
        duration.addAndGet(durNano);
        safeSetAtomic(this.max, durNano, true);
        safeSetAtomic(this.min, durNano, false);

        long chilrenDur = 0;

        List<MethodTimer> childrenMethodTimers = methodTimer.getChildren();
        for (MethodTimer childMethodTimer : childrenMethodTimers) {
            chilrenDur += childMethodTimer.getDuration();
            MethodProfiler childMethodProfiler = methodProfilerMap.get(childMethodTimer.getMethod());
            childMethodProfiler.calcProfiler(childMethodTimer, methodProfilerMap);
        }

        long timerSlfDur = durNano - chilrenDur;

        durationSelf.addAndGet(timerSlfDur);
        safeSetAtomic(this.maxSelf, timerSlfDur, true);
        safeSetAtomic(this.minSelf, timerSlfDur, false);
    }

    public int getLevel() {
        return level;
    }

    public long getTimers() {
        return timers.get();
    }

    public long getDuration() {
        return duration.get();
    }

    public long getMax() {
        return max.get();
    }

    public long getMin() {
        return min.get();
    }

    public long getDurationSelf() {
        return durationSelf.get();
    }

    public long getMaxSelf() {
        return maxSelf.get();
    }

    public long getMinSelf() {
        return minSelf.get();
    }

}
