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
package de.uhh.lt.codeanno.webapp.remoteapi.config;

import java.util.Optional;

import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import de.uhh.lt.codeanno.webapp.remoteapi.LegacyRemoteApiController;
import de.uhh.lt.codeanno.webapp.remoteapi.aero.AeroRemoteApiController;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.ApiSelectorBuilder;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2 // Loads the spring beans required by the framework
public class SwaggerConfig
{
    /*
     * Just do avoid Springfox to auto-scan for all APIs
     */
    @ConditionalOnExpression("!(" + RemoteApiConfig.REMOTE_API_ENABLED_CONDITION + ")")
    @Bean
    public Docket defaultDocket()
    {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).select();
        builder.paths(path -> false);
        // @formatter:off
        return builder.build()
                .groupName("Remote API disbled")
                .apiInfo(new ApiInfoBuilder()
                        .title("Remote API disabled")
                        .description(String.join(" ",
                                "The remote API is disabled."))
                        .license("")
                        .licenseUrl("")
                .build());
        // @formatter:on
    }

    @ConditionalOnExpression(RemoteApiConfig.REMOTE_API_ENABLED_CONDITION)
    @Bean
    public Docket legacyRemoteApiDocket()
    {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).select();
        builder.paths(path -> path.matches(LegacyRemoteApiController.API_BASE + "/.*"));
        // @formatter:off
        return builder.build()
                .groupName("Legacy API")
                .genericModelSubstitutes(Optional.class);
        // @formatter:on
    }

    @ConditionalOnExpression(RemoteApiConfig.REMOTE_API_ENABLED_CONDITION)
    @Bean
    public Docket areoRemoteApiDocket()
    {
        ApiSelectorBuilder builder = new Docket(DocumentationType.SWAGGER_2).select();
        builder.paths(path -> path.matches(AeroRemoteApiController.API_BASE + "/.*"));
        // @formatter:off
        return builder.build()
                .groupName("AERO API")
                .apiInfo(new ApiInfoBuilder()
                        .title("AERO")
                        .version("1.0.0") 
                        .description(String.join(" ",
                                "Annotation Editor Remote Operations API. ",
                                "https://openminted.github.io/releases/aero-spec/1.0.0/omtd-aero/"))
                        .license("Apache License 2.0")
                        .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.txt")
                        .build())
                .genericModelSubstitutes(Optional.class);
        // @formatter:on
    }
}
