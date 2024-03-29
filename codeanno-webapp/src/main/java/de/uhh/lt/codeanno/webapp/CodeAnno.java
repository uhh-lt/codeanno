/*
 * Licensed to the Technische Universität Darmstadt under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The Technische Universität Darmstadt 
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.
 *  
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.uhh.lt.codeanno.webapp;

import static org.springframework.boot.WebApplicationType.SERVLET;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.swing.JWindow;

import org.apache.catalina.connector.Connector;
import org.apache.uima.cas.impl.CASImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;

import de.tudarmstadt.ukp.clarin.webanno.plugin.api.PluginManager;
import de.tudarmstadt.ukp.clarin.webanno.plugin.impl.PluginManagerImpl;
import de.tudarmstadt.ukp.clarin.webanno.support.SettingsUtil;
import de.tudarmstadt.ukp.clarin.webanno.support.standalone.LoadingSplashScreen;
import de.tudarmstadt.ukp.clarin.webanno.support.standalone.ShutdownDialogAvailableEvent;
import de.uhh.lt.codeanno.webapp.config.CodeAnnoApplicationContextInitializer;
import de.uhh.lt.codeanno.webapp.config.CodeAnnoBanner;

/**
 * Boots CodeAnno in standalone JAR or WAR modes.
 */
@SpringBootApplication(scanBasePackages = {"de.tudarmstadt.ukp.clarin.webanno", "de.uhh.lt.codeanno"})
@EntityScan(basePackages = {"de.tudarmstadt.ukp.clarin.webanno", "de.uhh.lt.codeanno"})
@EnableAsync
public class CodeAnno
    extends SpringBootServletInitializer
{
    private static final String PROTOCOL = "AJP/1.3";

    @Value("${server.ajp.port:-1}")
    private int ajpPort;

    @Value("${server.ajp.secret-required:true}")
    private String ajpSecretRequired;

    @Value("${server.ajp.secret:}")
    private String ajpSecret;

    @Value("${server.ajp.address:127.0.0.1}")
    private String ajpAddress;

    @Bean
    public PluginManager pluginManager()
    {
        PluginManagerImpl pluginManager = new PluginManagerImpl(
                SettingsUtil.getApplicationHome().toPath().resolve("plugins"));
        Runtime.getRuntime().addShutdownHook(new Thread(() -> pluginManager.stopPlugins()));
        return pluginManager;
    }

    // The CodeAnno User model class picks this bean up by name!
    @Bean
    public PasswordEncoder passwordEncoder()
    {
        // Set up a DelegatingPasswordEncoder which decodes legacy passwords using the
        // StandardPasswordEncoder but encodes passwords using the modern BCryptPasswordEncoder
        String encoderForEncoding = "bcrypt";
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put(encoderForEncoding, new BCryptPasswordEncoder());
        DelegatingPasswordEncoder delegatingEncoder = new DelegatingPasswordEncoder(
                encoderForEncoding, encoders);
        // Decode legacy passwords without encoder ID using the StandardPasswordEncoder
        delegatingEncoder.setDefaultPasswordEncoderForMatches(new StandardPasswordEncoder());
        return delegatingEncoder;
    }

    @Bean
    public TomcatServletWebServerFactory servletContainer()
    {
        TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
        if (ajpPort > 0) {
            Connector ajpConnector = new Connector(PROTOCOL);
            ajpConnector.setPort(ajpPort);
            ajpConnector.setAttribute("secretRequired", ajpSecretRequired);
            ajpConnector.setAttribute("secret", ajpSecret);
            ajpConnector.setAttribute("address", ajpAddress);
            factory.addAdditionalTomcatConnectors(ajpConnector);
        }
        return factory;
    }

    @Override
    protected SpringApplicationBuilder createSpringApplicationBuilder()
    {
        SpringApplicationBuilder builder = super.createSpringApplicationBuilder();
        builder.properties("running.from.commandline=false");
        init(builder);
        return builder;
    }

    private static void init(SpringApplicationBuilder aBuilder)
    {
        // WebAnno relies on FS IDs being stable, so we need to enable this
        System.setProperty(CASImpl.ALWAYS_HOLD_ONTO_FSS, "true");

        aBuilder.banner(new CodeAnnoBanner());
        aBuilder.initializers(new CodeAnnoApplicationContextInitializer());
        aBuilder.headless(false);

        // Traditionally, the WebAnno configuration file is called settings.properties and is
        // either located in webanno.home or under the user's home directory. Make sure we pick
        // it up from there in addition to reading the built-in application.properties file.
        aBuilder.properties("spring.config.additional-location="
                + "optional:${codeanno.home:${user.home}/.codeanno}/settings.properties");
    }

    public static void main(String[] args) throws Exception
    {
        Optional<JWindow> splash = LoadingSplashScreen
                .setupScreen(CodeAnno.class.getResource("splash.png"));

        SpringApplicationBuilder builder = new SpringApplicationBuilder();
        // Add the main application as the root Spring context
        builder.sources(CodeAnno.class).web(SERVLET);

        // Signal that we may need the shutdown dialog
        builder.properties("running.from.commandline=true");
        init(builder);
        builder.listeners(event -> {
            if (event instanceof ApplicationReadyEvent
                    || event instanceof ShutdownDialogAvailableEvent) {
                splash.ifPresent(it -> it.dispose());
            }
        });
        builder.run(args);
    }
}
