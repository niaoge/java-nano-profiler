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
 * The Class MethodList.
 *
 * @author niaoge(78493244@qq.com , niaoge@gmail.com)
 * @version $Id: MethodList.java, v 0.1 2016-7-14 13:09:48 niaoge Exp $$
 */
public class MethodList extends ArrayList<Method> {

    /**  */
    private static final long serialVersionUID = 1L;

    private int               hashCode         = 0;

    @Override
    public void add(int index, Method element) {
        super.add(index, element);
        hashCode = 0;
    }

    @Override
    public int hashCode() {
        if (hashCode != 0) {
            return hashCode;
        }

        hashCode = super.hashCode();
        return hashCode;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }

        if (!(o instanceof List)) {
            return false;
        }

        return this.hashCode() == o.hashCode();
    }

}
