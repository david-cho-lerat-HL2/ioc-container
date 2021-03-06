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

import com.my.container.engine.ContainerConfigurationImpl;
import com.my.container.engine.ContainerImpl;
import com.my.container.spi.ContainerProvider;

/**
 * @author Kevin Pollet
 */
public class IOCContainerProvider implements ContainerProvider {

	public Configuration configure() {
		return new ContainerConfigurationImpl();
	}

	public <T extends Configuration> T useSpecificConfiguration(Class<T> clazz) {
		if ( ContainerConfiguration.class.equals( ContainerConfiguration.class ) ) {
			return clazz.cast( new ContainerConfigurationImpl() );
		}
		throw new ContainerException( String.format( "Configuration class %s is not supported", clazz.getName() ) );
	}

	public Container buildContainer(Configuration configuration) {
		return new ContainerImpl( configuration );
	}
}
