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
import java.util.Map;

/**
 * The Class ThreadMethodLinkHolder.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: ThreadMethodLinkHolder.java, v 0.1 2016-7-14 13:12:11 niaoge Exp $$
 */
public class ThreadMethodLinkHolder {
    static ThreadLocal<ThreadMethodLinkHolder> threadMethodProfilerHolderLocal = new ThreadLocal<ThreadMethodLinkHolder>();

    MethodList                                 methodOrderList                 = new MethodList();
    Map<Method, ThreadMethodLink>              threadMethodLinkMap             = new HashMap<Method, ThreadMethodLink>();

    public MethodList getMethodOrderList() {
        return methodOrderList;
    }

    public Map<Method, ThreadMethodLink> getThreadMethodLinkMap() {
        return threadMethodLinkMap;
    }

    public static void clearLocalThreadMethodLinkHolder() {
        threadMethodProfilerHolderLocal.set(null);
    }

    public ThreadMethodLink getCurrentThreadMethodProfiler(Method method) {
        ThreadMethodLink threadMethodProfiler = threadMethodLinkMap.get(method);
        if (threadMethodProfiler == null) {
            methodOrderList.add(method);

            threadMethodProfiler = new ThreadMethodLink(method);
            threadMethodLinkMap.put(method, threadMethodProfiler);
        }
        return threadMethodProfiler;
    }

    public static ThreadMethodLinkHolder getCurrentThreadMethodProfilerHolderFromLocal() {
        ThreadMethodLinkHolder threadMethodLinkHolder = threadMethodProfilerHolderLocal.get();
        if (threadMethodLinkHolder == null) {
            threadMethodLinkHolder = new ThreadMethodLinkHolder();
            threadMethodProfilerHolderLocal.set(threadMethodLinkHolder);
        }
        return threadMethodLinkHolder;
    }

    public ThreadMethodLink getThreadMethodProfiler(Method method) {
        return threadMethodLinkMap.get(method);
    }

    /**
     * The Class ThreadMethodLink.
     *
     * @author niaoge(78493244@qq.com , niaoge@gmail.com)
     * @version $Id: ThreadMethodLinkHolder.java, v 0.1 2016-7-14 13:12:11 niaoge Exp $$
     */
    public class ThreadMethodLink {

        Method           method;

        ThreadMethodLink parent;

        int              level = 0;

        public ThreadMethodLink(Method method) {
            this.method = method;
        }

        public ThreadMethodLink getParent() {
            return parent;
        }

        public void setParent(ThreadMethodLink parent) {
            this.parent = parent;
            if (parent != null) {
                int parentLevel = parent.level;
                level = parentLevel + 1;
            }
        }

        public int getLevel() {
            return level;
        }

    }

}
