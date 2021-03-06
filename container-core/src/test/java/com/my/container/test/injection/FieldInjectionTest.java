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
package com.my.container.test.injection;

import java.lang.reflect.Field;

import com.my.container.Container;
import com.my.container.binding.FluentBindingProvider;
import com.my.container.test.injection.services.ServiceA;
import com.my.container.test.injection.services.ServiceB;
import com.my.container.test.injection.services.ServiceC;
import com.my.container.test.injection.services.ServiceD;
import com.my.container.test.injection.services.impl.EchoServiceC;
import com.my.container.test.injection.services.impl.LowerEcho;
import com.my.container.test.injection.services.impl.LowerEchoProvider;
import com.my.container.test.injection.services.impl.LowerEchoServiceC;
import com.my.container.test.injection.services.impl.UpperEchoServiceC;
import com.my.container.test.injection.services.impl.fields.FieldAbstractService;
import com.my.container.test.injection.services.impl.fields.FieldNamedServiceD;
import com.my.container.test.injection.services.impl.fields.FieldProviderServiceD;
import com.my.container.test.injection.services.impl.fields.FieldQualifierServiceD;
import com.my.container.test.injection.services.impl.fields.FieldServiceAImpl;
import com.my.container.test.injection.services.impl.fields.FieldServiceBImpl;
import junit.framework.Assert;
import org.junit.Test;


/**
 * @author Kevin Pollet
 */
public class FieldInjectionTest {

    @Test
    public void testFieldDependencyInjection() throws NoSuchFieldException, IllegalAccessException {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceA.class ).to( FieldServiceAImpl.class );
						bind( ServiceB.class ).to( FieldServiceBImpl.class );
						bind( ServiceC.class ).to( EchoServiceC.class );
						bind( ServiceD.class ).to( FieldNamedServiceD.class );
					}
				}
		).buildContainer();

        ServiceA service = injector.get( ServiceA.class );

        //Get dependency
        Field depB = service.getClass().getDeclaredField("serviceB");
        depB.setAccessible(true);

        Field depC = service.getClass().getDeclaredField("serviceC");
        depC.setAccessible(true);

        Assert.assertNotNull(service);
        Assert.assertNotNull("Dependency is null", depB.get(service));
        Assert.assertNotNull("Dependency is null", depC.get(service));
        Assert.assertEquals("Hello Injection", service.sayHelloTo("Injection"));
        Assert.assertEquals("Great injection", service.echo("Great injection"));
    }

    @Test
    public void testSuperFieldDependencyInjection() {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceA.class ).to( FieldServiceAImpl.class );
						bind( ServiceB.class ).to( FieldServiceBImpl.class );
						bind( ServiceC.class ).to( EchoServiceC.class );
						bind( ServiceD.class ).to( FieldNamedServiceD.class );
					}
				}
		).buildContainer();

        ServiceA service = injector.get( ServiceA.class );

        Assert.assertNotNull(service);
        Assert.assertNotNull("SuperClass dependency is null", ((FieldAbstractService) service).getServiceC());
        Assert.assertEquals("Hello Injection", service.sayHelloTo("Injection"));
    }

    @Test
    public void testExistingBeanFieldInjection() throws NoSuchFieldException, IllegalAccessException {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceA.class ).to( FieldServiceAImpl.class );
						bind( ServiceB.class ).to( FieldServiceBImpl.class );
						bind( ServiceC.class ).to( EchoServiceC.class );
						bind( ServiceD.class ).to( FieldNamedServiceD.class );
					}
				}
		).buildContainer();

        ServiceA service = new FieldServiceAImpl();
        injector.injectDependencies( service );

        //Get dependency
        Field depB = service.getClass().getDeclaredField("serviceB");
        depB.setAccessible(true);

        Field depC = service.getClass().getDeclaredField("serviceC");
        depC.setAccessible(true);

        Assert.assertNotNull(service);
        Assert.assertNotNull("Dependency is null", depB.get(service));
        Assert.assertNotNull("Dependency is null", depC.get(service));
        Assert.assertEquals("Hello Injection", service.sayHelloTo("Injection"));
        Assert.assertEquals("Great injection", service.echo("Great injection"));
    }

    @Test
    public void testNamedFieldInjection() {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceC.class ).to( UpperEchoServiceC.class ).named( "upperEchoService" );
						bind( ServiceD.class ).to( FieldNamedServiceD.class );
					}
				}
		).buildContainer();

        ServiceD serviceD = injector.get( ServiceD.class );

        Assert.assertNotNull(serviceD);
        Assert.assertEquals("ECHO", serviceD.echo("echo"));
    }

    @Test
    public void testQualifiedFieldInjection() {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceC.class ).to( LowerEchoServiceC.class ).qualifiedBy( LowerEcho.class );
						bind( ServiceD.class ).to( FieldQualifierServiceD.class );
					}
				}
		).buildContainer();

        ServiceD serviceD = injector.get( ServiceD.class );

        Assert.assertNotNull(serviceD);
        Assert.assertEquals("echo", serviceD.echo("ECHO"));
    }

    @Test
    public void testDefaultProviderFieldInjection() {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceC.class ).to( UpperEchoServiceC.class );
						bind( ServiceD.class ).to( FieldProviderServiceD.class );
					}
				}
		).buildContainer();

        ServiceD serviceD = injector.get( ServiceD.class );

        Assert.assertNotNull(serviceD);
        Assert.assertEquals("ECHO", serviceD.echo("echo"));
    }

    @Test
    public void testUserProviderFieldInjection() {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceC.class ).toProvider( LowerEchoProvider.class );
						bind( ServiceD.class ).to( FieldProviderServiceD.class );
					}
				}
		).buildContainer();

        ServiceD serviceD = injector.get( ServiceD.class );

        Assert.assertNotNull(serviceD);
        Assert.assertEquals("echo", serviceD.echo("eCho"));
    }

    @Test
    public void testCyclicDependencies() throws NoSuchFieldException, IllegalAccessException {
        Container injector = Container.configure().addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( ServiceA.class ).to( FieldServiceAImpl.class );
						bind( ServiceB.class ).to( FieldServiceBImpl.class );
						bind( ServiceC.class ).to( EchoServiceC.class );
						bind( ServiceD.class ).to( FieldNamedServiceD.class );
					}
				}
		).buildContainer();

        ServiceA serviceA = injector.get( ServiceA.class );

        //ServiceC dependency
        Field depB = FieldServiceAImpl.class.getDeclaredField("serviceB");
        depB.setAccessible(true);

        ServiceB serviceB = (ServiceB) depB.get(serviceA);
        Field depA = FieldServiceBImpl.class.getDeclaredField("serviceA");
        depA.setAccessible(true);

        Assert.assertNotNull(serviceA);
        Assert.assertNotNull(serviceB);
        Assert.assertSame("The objects have not the same reference in the cycle", serviceA, depA.get(serviceB));
    }

}
