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

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ProcessExecutor.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: ProcessExecutor.java, v 0.1 2016-7-14 13:11:53 niaoge Exp $$
 */
public class ProcessExecutor {

    final static Logger    logger          = LoggerFactory.getLogger(ProcessExecutor.class);

    private static long    nanoToMs        = 1000000;

    static ExecutorService executorService = null;

    public static void processMethodProfilerMap(MethodList methodList, Map<Method, MethodProfiler> methodProfilerMap) {
        if (methodList.isEmpty()) {
            return;
        }

        boolean doProcess = false;
        MethodProfiler mp1 = methodProfilerMap.get(methodList.get(0));

        long firstInvokeTimes = mp1.getTimers();
        if (firstInvokeTimes == 1) {
            doProcess = true;
        }

        if (!doProcess) {
            if ((firstInvokeTimes % ProfileProperties.processTimers) == 0) {
                doProcess = true;
            }
        }

        if (!doProcess) {
            return;
        }

        if (ProfileProperties.clearnAfterProcess && firstInvokeTimes != 1) {
            MethodProfiler.cleanHistoryByMethodList(methodList);
        }

        if (!(logger.isInfoEnabled() || ProfileProperties.processToFile)) {
            return;
        }

        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        LogOrFileProcessor LogOrFileProcessor = new LogOrFileProcessor(methodList, methodProfilerMap);

        executorService.submit(LogOrFileProcessor);
    }

    /**
     * The Class LogOrFileProcessor.
     *
     * @author niaoge(78493244@qq.com , niaoge@gmail.com)
     * @version $Id: ProcessExecutor.java, v 0.1 2016-7-14 13:11:53 niaoge Exp $$
     */
    static class LogOrFileProcessor implements Callable<Boolean> {

        MethodList                  methodList;

        Map<Method, MethodProfiler> methodProfilerMap;

        LogOrFileProcessor(MethodList methodList, Map<Method, MethodProfiler> methodProfilerMap) {
            this.methodList = methodList;
            this.methodProfilerMap = methodProfilerMap;
        }

        @Override
        public Boolean call() throws Exception {
            MethodProfiler mp1 = methodProfilerMap.get(methodList.get(0));

            StringBuffer sb = getProcessBuffer(mp1);

            if (logger.isInfoEnabled()) {
                logger.info(sb.toString());
            }

            if (ProfileProperties.processToFile) {
                writeBufferToFile(sb, methodList);
            }
            return true;
        }

        private void writeBufferToFile(StringBuffer sb, MethodList methodList) {
            int methodListHash = methodList.hashCode();
            if (ProfileProperties.profileFilePath == null || ProfileProperties.profileFilePath.length() == 0) {
                if (logger.isInfoEnabled()) {
                    logger.info(new StringBuffer("profiler  filepath is not defined!").toString());
                }
                return;
            }
            String fullName = ProfileProperties.profileFilePath + methodListHash + ".txt";
            Writer writer = null;
            try {
                writer = new FileWriter(fullName);
                writer.write(sb.toString());
            } catch (IOException e) {
                logger.error("fullName:" + fullName, e);
            } finally {
                if (writer != null) {
                    try {
                        writer.flush();
                        writer.close();
                        if (logger.isInfoEnabled()) {
                            logger.info(new StringBuffer("java-nano-profier save profile to file:").append(fullName).toString());
                        }
                    } catch (IOException e) {
                        logger.error("fullName:" + fullName, e);
                    }
                }
            }
        }

        private StringBuffer getProcessBuffer(MethodProfiler mp1) {
            StringBuffer sb = new StringBuffer();
            sb.append('\n');
            sb.append("Begin of method execute Links,copy to excel manually=======================>\n");
            sb.append("method\tinvokes \tduration(total ms)\tmax(ms)\tmin(ms)\tavg(ms)\tduration-self(ms)\tmax-self(ms)\tmin-self\tavg-self\t% of self\t% of total\n");
            DecimalFormat df = new DecimalFormat("#.00");
            long firstDuration = mp1.getDuration();

            for (Method method : methodList) {
                MethodProfiler mp = methodProfilerMap.get(method);

                for (int level = 0; level < mp.getLevel(); level++) {
                    sb.append(' ');
                }

                String className = method.getDeclaringClass().getName();
                String methodName = method.getName();
                sb.append(className).append('.').append(methodName).append("(...)");
                sb.append('\t').append(mp.getTimers());
                sb.append('\t').append(mp.getDuration() / nanoToMs);
                sb.append('\t').append(mp.getMax() / nanoToMs);
                sb.append('\t').append(mp.getMin() / nanoToMs);
                sb.append('\t').append(mp.getDuration() / mp.getTimers() / nanoToMs);

                sb.append('\t').append(mp.getDurationSelf() / nanoToMs);
                sb.append('\t').append(mp.getMaxSelf() / nanoToMs);
                sb.append('\t').append(mp.getMinSelf() / nanoToMs);
                sb.append('\t').append(mp.getDurationSelf() / mp.getTimers() / nanoToMs);
                Double d = (double) Math.round(mp.getDurationSelf() * 10000 / mp.getDuration()) / 100.0;
                sb.append('\t').append(df.format(d));
                d = (double) Math.round(mp.getDurationSelf() * 10000 / firstDuration) / 100.0;
                sb.append('\t').append(df.format(d));
                sb.append('\n');
            }
            sb.append("<============================End of method execute Links,copy to excel manually\n");
            return sb;
        }
    }

}
