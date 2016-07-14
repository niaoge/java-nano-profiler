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
package com.helpinput.profaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helpinput.profile.Monitor;

/**
 * The Class AspectProfiler.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: AspectProfiler.java, v 0.1 2016-7-14 17:13:46 niaoge Exp $$
 */
@Aspect
public class AspectProfiler {
    final static Logger logger = LoggerFactory.getLogger(AspectProfiler.class);

    @Pointcut("!within(AspectProfiler)")
    void notSelf() {
    }
    
    @Pointcut("!within(org.aspectj..*)")
    void notAspectJ() {
    }
    
    /**
     * Not profiler.
     */
    @Pointcut("!within(com.helpinput.profile..*)")
    void notProfiler() {
    }

    @Pointcut("execution(* *(..))")
    void allMethods() {
    }

    //@Around("notWithin() && allMethods()")
    @Around("notSelf() && notProfiler() && notAspectJ() && allMethods()")
    public Object exeAround(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Monitor.startMonitor(signature.getMethod());
        Throwable error = null;
        try {
            Object result = joinPoint.proceed();
            return result;
        } catch (Throwable e) {
            error = e;
            throw error;
        } finally {
            Monitor.stopMonitor(error);
        }
    }
}
