package com.helpinput.profaop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.helpinput.profile.Monitor;

@Aspect
public class AspectProfiler {
    final static Logger logger = LoggerFactory.getLogger(AspectProfiler.class);

    @Pointcut("!within(AspectProfiler)")
    void notSelf() {
    }
    
    @Pointcut("!within(org.aspectj..*)")
    void notAspectJ() {
    }
    
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
