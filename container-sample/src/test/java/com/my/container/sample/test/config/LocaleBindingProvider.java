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
package com.my.container.sample.test.config;

import java.util.Locale;

import com.my.container.binding.FluentBindingProvider;
import com.my.container.sample.GreetingService;
import com.my.container.sample.HelloService;
import com.my.container.sample.impl.EnglishHelloService;
import com.my.container.sample.impl.FrenchHelloService;
import com.my.container.sample.impl.HelloGreetingService;

/**
 * @author Kevin Pollet
 */
public class LocaleBindingProvider extends FluentBindingProvider {
	@Override
	public void configureBindings() {
		bind( GreetingService.class ).to( HelloGreetingService.class );

		if ( Locale.getDefault() == Locale.FRENCH ) {
			bind( HelloService.class ).to( FrenchHelloService.class );
		}
		else {
			bind( HelloService.class ).to( EnglishHelloService.class );
		}
	}
}
