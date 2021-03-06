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
package com.my.container.test.tck;

import com.my.container.Container;
import com.my.container.Configuration;
import junit.framework.Test;
import org.atinject.tck.Tck;
import org.atinject.tck.auto.Car;

/**
 * JSR-330 TCK.
 *
 * @author Kevin Pollet
 */
public class TckSuiteInitialisation {

	/**
	 * Configure and return the suite.
	 *
	 * @return the suite.
	 */
	public static Test suite() {
		Configuration config = Container.configure();
		Container injector = config.addBindingProvider( new TckBindingProvider() ).buildContainer();

		Car car = injector.get( Car.class );
		return Tck.testsFor( car, false, true );
	}

}
