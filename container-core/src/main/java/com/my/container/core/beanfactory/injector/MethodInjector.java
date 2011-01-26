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
package com.my.container.core.beanfactory.injector;

import com.my.container.InjectionContext;
import com.my.container.core.ContextBeanFactoryImpl;
import com.my.container.core.InjectionContextImpl;
import com.my.container.exceptions.NoSuchBeanDefinitionException;
import com.my.container.binding.Binding;
import com.my.container.binding.ProvidedBinding;
import com.my.container.exceptions.BeanDependencyInjectionException;
import com.my.container.core.provider.GenericProvider;
import com.my.container.util.ProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Qualifier;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import static com.my.container.util.ReflectionHelper.isOverridden;

/**
 * Definition pf an Injector of fields.
 *
 * @author Kevin Pollet
 */
public class MethodInjector {

    private final Logger logger = LoggerFactory.getLogger(MethodInjector.class);

    private final Injector injector;

    /**
     * Construct a Method injector.
     *
     * @param injector the parent injector
     */
    public MethodInjector(final Injector injector) {
        this.injector = injector;
    }

    /**
     * Inject method parameters of methods
     * annotated by @Inject annotation.
     *
     * @param context  the injection core
     * @param clazz    the current class injected
     * @param instance the instance injected
     */
    public void injectMethodsDependencies(final InjectionContext context, final Class<?> clazz, final Object instance) {
        this.logger.debug("Inject methods of class {}", clazz.getName());

        for (Method method : clazz.getDeclaredMethods()) {

            if (method.isAnnotationPresent(Inject.class)) {

                if (Modifier.isAbstract(method.getModifiers())) {
                    throw new BeanDependencyInjectionException(String.format("Injection of dependencies in abstract method %s is invalid in class %s.", method.getName(), clazz.getName()));
                }

                if (!isOverridden(ProxyHelper.getTargetClass(instance), method)) {

                    Class<?>[] parametersClass = method.getParameterTypes();
                    Object[] parameters = new Object[parametersClass.length];
                    Type[] parametersType = method.getGenericParameterTypes();
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();

                    context.getAlreadyInjectedBean().put( instance.getClass(), instance );

                    for (int i = 0; i < parametersClass.length; i++) {

                        Binding<?> injectionBinding;
                        Annotation qualifier = null;
                        Class<?> parameterClass = parametersClass[i];

                        //Method cannot declare parameter of type of it's enclosing class
                        if (parameterClass.isAssignableFrom(clazz)) {
                            throw new BeanDependencyInjectionException(String.format("Method injection for %s cannot have a parameter of their own type.", parametersClass[i].getSimpleName()));
                        }

                        //Get parameter qualifier
                        for (Annotation annotation : parameterAnnotations[i]) {
                            if (annotation.annotationType().isAnnotationPresent(Qualifier.class)) {
                                qualifier = annotation;
                                break;
                            }
                        }

                        //Create parameter instance
                        if (parameterClass.isAssignableFrom(Provider.class)) {
                            if (parametersType[i] instanceof ParameterizedType) {
                                Class<?> classToInject = (Class<?>) ((ParameterizedType) parametersType[i]).getActualTypeArguments()[0];
                                injectionBinding = ((ContextBeanFactoryImpl) context.getContextBeanFactory()).getProviderHolder().getBindingFor(classToInject, qualifier);
                                if (injectionBinding == null) {
                                    injectionBinding = ((ContextBeanFactoryImpl) context.getContextBeanFactory()).getBindingHolder().getBindingFor(classToInject, qualifier);
                                    if (injectionBinding == null) {
                                        throw new NoSuchBeanDefinitionException(String.format("There is no binding defined for the class %s", classToInject.getName()));
                                    }
                                    parameters[i] = new GenericProvider(((ContextBeanFactoryImpl) context.getContextBeanFactory()), injectionBinding.getImplementation());
                                } else {
                                    parameters[i] = this.injector.constructClass(context, ((ProvidedBinding) injectionBinding).getProvider());
                                }
                            }
                        } else {
                            injectionBinding = ((ContextBeanFactoryImpl) context.getContextBeanFactory()).getBindingHolder().getBindingFor(parameterClass, qualifier);
                            if (injectionBinding == null) {
                                throw new NoSuchBeanDefinitionException(String.format("There is no binding defined for the class %s", parameterClass.getName()));
                            }
                            parameters[i] = this.injector.constructClass(context, injectionBinding.getImplementation());
                        }
                    }


                    //Call method
                    try {

                        if (!method.isAccessible()) {
                            method.setAccessible(true);
                        }
                        method.invoke(ProxyHelper.getTargetObject(instance), parameters);
                    }
                    catch (IllegalAccessException ex) {
                        throw new BeanDependencyInjectionException(ex);
                    }
                    catch (InvocationTargetException ex) {
                        throw new BeanDependencyInjectionException(ex);
                    }

                    //Remove mark
                    context.getAlreadyInjectedBean().remove(instance.getClass());

                }
            }
        }
    }

}
