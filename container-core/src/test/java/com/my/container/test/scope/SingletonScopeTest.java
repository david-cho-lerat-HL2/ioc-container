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
package com.my.container.test.scope;

import com.my.container.binding.FluentBindingProvider;
import com.my.container.Configuration;
import com.my.container.Container;
import com.my.container.test.scope.services.HelloService;
import com.my.container.test.scope.services.impl.SingletonHelloService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the singleton scope.
 *
 * @author Kevin Pollet
 */
public class SingletonScopeTest {

	private Container injector;

	@Before
	public void setUp() {
		Configuration config = Container.configure();
		config.addBindingProvider(
				new FluentBindingProvider() {
					@Override
					public void configureBindings() {
						bind( HelloService.class ).to( SingletonHelloService.class );
					}
				}
		);
		injector = config.buildContainer();
	}

	@Test
	public void testSingletonScope() {
		HelloService firstInstance = injector.get( HelloService.class );
		HelloService secondInstance = injector.get( HelloService.class );

		Assert.assertNotNull( firstInstance );
		Assert.assertNotNull( secondInstance );
		Assert.assertSame( "Beans have not the same reference", firstInstance, secondInstance );
		Assert.assertEquals( "Hello", firstInstance.sayHello() );
		Assert.assertEquals( "Hello", secondInstance.sayHello() );
	}
}
