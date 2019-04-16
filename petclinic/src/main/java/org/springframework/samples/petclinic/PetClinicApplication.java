/*
 * Copyright 2012-2018 the original author or authors.
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

package org.springframework.samples.petclinic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.flow.server.VaadinServlet;
import com.vaadin.flow.spring.annotation.EnableVaadin;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 *
 */
@SpringBootApplication
@Configuration
@EnableVaadin("org.springframework.samples.webcomponent")
public class PetClinicApplication {

    @Autowired
    private WebApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<VaadinServlet> frontendResourcesRegistrationBean() {
        ServletRegistrationBean<VaadinServlet> registration = new ServletRegistrationBean<>(
                new VaadinServlet(), "/frontend/*", "/vaadin/*");
        registration.setAsyncSupported(true);
        return registration;
    }

}
