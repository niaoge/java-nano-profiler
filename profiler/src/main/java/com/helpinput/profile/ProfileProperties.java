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

import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class ProfileProperties.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: ProfileProperties.java, v 0.1 2016-7-14 13:12:07 niaoge Exp $$
 */
public class ProfileProperties {
    final static Logger            logger                    = LoggerFactory.getLogger(ProfileProperties.class);
    public final static Properties DEFAULT_PROPERTIES        = new Properties();
    private final static String    PROFILE_PROPERTIES_NAME   = "java-nano-profile.properties";

    private final static String    PROCESS_WHEN_INVOKE_TIMES = "process.when.invoke.times";
    public static long             processTimers             = 4999;
    public final static long       processTimersMin          = 10;

    private final static String    PROCESS_THEN_CLEAN        = "process.then.clear";
    public static boolean          clearnAfterProcess        = true;

    private final static String    PROCESS_TO_FILE           = "process.to.file";
    public static boolean          processToFile             = false;

    private final static String    PROCESS_TO_FILEPATH       = "process.to.filepath";
    public static String           profileFilePath           = null;

    static {
        try {
            new PropertiesInitializer().autoConfig();
        } catch (Throwable e) {
            //skip
        }
    }

    /**
     * The Class PropertiesInitializer.
     *
     * @author niaoge(78493244@qq.com , niaoge@gmail.com)
     * @version $Id: ProfileProperties.java, v 0.1 2016-7-14 13:12:07 niaoge Exp $$
     */
    static class PropertiesInitializer {
        public void autoConfig() {
            loadPropertiesFromFile();
            String processTimersStr = getStringProperty(PROCESS_WHEN_INVOKE_TIMES);
            processTimers = stringToLong(processTimersStr, processTimersMin);
            clearnAfterProcess = !("false".endsWith(getStringProperty(PROCESS_THEN_CLEAN)));
            processToFile = "true".equals(getStringProperty(PROCESS_TO_FILE));
            profileFilePath = getStringProperty(PROCESS_TO_FILEPATH);
        }
    }

    private static long stringToLong(String source, long dftInt) {
        Long result = null;
        try {
            result = Long.parseLong(source);
        } catch (NumberFormatException e) {
            logger.error(new StringBuffer(source).append(" is not an int!").toString(), e);
        }
        if (result == null || result < dftInt) {
            result = dftInt;
        }
        return result;
    }

    public static String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            //skip
        }
        return (prop == null) ? DEFAULT_PROPERTIES.getProperty(name) : prop;
    }

    public static void loadPropertiesFromFile() {
        InputStream imputStream = AccessController.doPrivileged(new PrivilegedAction<InputStream>() {
            public InputStream run() {
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                if (cl != null) {
                    return cl.getResourceAsStream(PROFILE_PROPERTIES_NAME);
                } else {
                    return ClassLoader.getSystemResourceAsStream(PROFILE_PROPERTIES_NAME);
                }
            }
        });

        if (null != imputStream) {
            try {
                DEFAULT_PROPERTIES.load(imputStream);
                imputStream.close();
            } catch (java.io.IOException e) {
                // skip
            }
        }
    }
}
