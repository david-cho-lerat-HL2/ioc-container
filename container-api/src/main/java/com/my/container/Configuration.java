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
package com.my.container;

import com.my.container.binding.BindingProvider;

/**
 * @author Kevin Pollet
 */
public interface Configuration<C extends Configuration> {
	/**
	 * Add a binding provider
	 *
	 * @param provider the provider to add
	 * @param <T> the provider type
	 *
	 * @return Configuration interface for fluent configuration
	 */
	<T extends BindingProvider> C addBindingProvider(T provider);

	/**
	 * Get a configured instance of a container.
	 *
	 * @return a configured instance of container
	 */
	Container buildContainer();
}
