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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.samples.webcomponent.VaadinServletContextInitializer;
import org.springframework.web.context.WebApplicationContext;

import com.vaadin.flow.server.VaadinServlet;

/**
 * PetClinic Spring Boot Application.
 *
 * @author Dave Syer
 *
 */
@SpringBootApplication
@Configuration
public class PetClinicApplication {

    @Autowired
    private WebApplicationContext context;

    private static class VaadinServletWithNonRootFrontendHandler
            extends VaadinServlet {

        @Override
        protected void service(HttpServletRequest request,
                HttpServletResponse response)
                throws ServletException, IOException {
            String servletPath = request.getServletPath();
            String pathInfo = request.getPathInfo();

            String queryString = request.getQueryString();

            if ("/vaadin".equals(servletPath)
                    && pathInfo.startsWith("/frontend/bower_components/")
                    && queryString == null) {
                response.sendRedirect(pathInfo);
            }
            super.service(request, response);
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(PetClinicApplication.class, args);
    }

    @Bean
    public ServletRegistrationBean<VaadinServlet> servletRegistrationBean() {
        ServletRegistrationBean<VaadinServlet> registration = new ServletRegistrationBean<>(
                new VaadinServletWithNonRootFrontendHandler(), "/vaadin/*",
                "/frontend/*");
        registration.setAsyncSupported(true);
        return registration;
    }

    @Bean
    public ServletContextInitializer contextInitializer() {
        return new VaadinServletContextInitializer(context);
    }

}
