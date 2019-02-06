/*
 * Copyright 2000-2018 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.springframework.samples.webcomponent;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.WebComponent;
import com.vaadin.flow.server.startup.WebComponentRegistryInitializer;
import com.vaadin.flow.server.webcomponent.WebComponentRegistry;

public class VaadinServletContextInitializer
        implements ServletContextInitializer {

    private final ApplicationContext appContext;

    private class WebComponentServletContextListener extends
            WebComponentRegistryInitializer implements ServletContextListener {

        @SuppressWarnings("unchecked")
        @Override
        public void contextInitialized(ServletContextEvent event) {
            WebComponentRegistry registry = WebComponentRegistry
                    .getInstance(event.getServletContext());

            if (registry.getWebComponents() == null
                    || registry.getWebComponents().isEmpty()) {
                Set<Class<? extends Component>> webComponents = findByAnnotation(
                        getWebComponentPackages(), WebComponent.class)
                                .map(c -> (Class<? extends Component>) c)
                                .collect(Collectors.toSet());

                validateDistinct(webComponents);
                validateComponentName(webComponents);

                Map<String, Class<? extends Component>> webComponentMap = webComponents
                        .stream().collect(Collectors
                                .toMap(this::getWebComponentName, c -> c));

                registry.setWebComponents(webComponentMap);
            }
        }

        @Override
        public void contextDestroyed(ServletContextEvent sce) {
            // no need to do anything
        }

    }

    public VaadinServletContextInitializer(ApplicationContext context) {
        appContext = context;
    }

    @Override
    public void onStartup(ServletContext servletContext)
            throws ServletException {
        servletContext.addListener(new WebComponentServletContextListener());
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<?>> findByAnnotation(Collection<String> packages,
            Class<? extends Annotation>... annotations) {
        return findByAnnotation(packages, Stream.of(annotations));
    }

    @SuppressWarnings("unchecked")
    private Stream<Class<?>> findByAnnotation(Collection<String> packages,
            Stream<Class<? extends Annotation>> annotations) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                false);
        scanner.setResourceLoader(appContext);
        annotations.forEach(annotation -> scanner
                .addIncludeFilter(new AnnotationTypeFilter(annotation)));

        return packages.stream().map(scanner::findCandidateComponents)
                .flatMap(set -> set.stream()).map(this::getBeanClass);
    }

    private Class<?> getBeanClass(BeanDefinition beanDefinition) {
        AbstractBeanDefinition definition = (AbstractBeanDefinition) beanDefinition;
        Class<?> beanClass;
        if (definition.hasBeanClass()) {
            beanClass = definition.getBeanClass();
        } else {
            try {
                beanClass = definition
                        .resolveBeanClass(appContext.getClassLoader());
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(e);
            }
        }
        return beanClass;
    }

    private List<String> getWebComponentPackages() {
        return Collections.singletonList(
                VaadinServletContextInitializer.class.getPackage().getName());
    }
}
