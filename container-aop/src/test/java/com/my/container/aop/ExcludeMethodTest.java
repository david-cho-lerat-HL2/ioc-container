/*
 * Copyright 2011 Kevin Pollet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.my.container.aop;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import com.my.container.Container;
import com.my.container.aop.services.HelloService;
import com.my.container.aop.services.impl.HelloServiceWithInterceptor;
import com.my.container.aop.services.impl.MockInterceptor;
import com.my.container.binding.FluentBindingProvider;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author kevinpollet
 */
public class ExcludeMethodTest {

    private Container context;

    @Before
    public void setUp() {
        this.context = Container.configure().addBindingProvider( new FluentBindingProvider(){
            @Override
            public void configureBindings() {
                bind(HelloService.class).to(HelloServiceWithInterceptor.class);
            }
        }).buildContainer();
    }

    @Test
    public void testExcludeMethodInterceptor() throws NoSuchFieldException, IllegalAccessException {
        HelloService service = this.context.get( HelloService.class );

        Assert.assertNotNull(service);
        Assert.assertTrue(Proxy.isProxyClass(service.getClass()));

        //GetMock
        InterceptorInvocationHandler handler = (InterceptorInvocationHandler) Proxy.getInvocationHandler(service);
        Field field = handler.getClass().getDeclaredField("interceptors");
        field.setAccessible(true);

        Object[] interceptors = (Object[]) field.get(handler);
        MockInterceptor mock = (MockInterceptor)interceptors[0];

        //Call service
        service.sayHello("Test");

        Assert.assertTrue(interceptors.length > 0);
        Assert.assertEquals(0, mock.getBeforeNbCall());
        Assert.assertEquals(0, mock.getAfterNbCall());
    }
    
}
