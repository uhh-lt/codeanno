/*
 * Copyright 2019
 * Ubiquitous Knowledge Processing (UKP) Lab and FG Language Technology
 * Technische Universität Darmstadt
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.tudarmstadt.ukp.clarin.webanno.support.interceptors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.ClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

@Component
public class GlobalInterceptorsRegistryImpl
    implements GlobalInterceptorsRegistry
{
    private final Logger log = LoggerFactory.getLogger(getClass());

    private final List<GlobalInterceptor> interceptorsProxy;

    private List<GlobalInterceptor> interceptors;

    public GlobalInterceptorsRegistryImpl(
            @Lazy @Autowired(required = false) List<GlobalInterceptor> aInterceptors)
    {
        interceptorsProxy = aInterceptors;
    }

    @EventListener
    public void onContextRefreshedEvent(ContextRefreshedEvent aEvent)
    {
        init();
    }

    public void init()
    {
        List<GlobalInterceptor> fsp = new ArrayList<>();

        if (interceptorsProxy != null) {
            fsp.addAll(interceptorsProxy);
            AnnotationAwareOrderComparator.sort(fsp);

            for (GlobalInterceptor fs : fsp) {
                log.info("Found global interceptor: {}",
                        ClassUtils.getAbbreviatedName(fs.getClass(), 20));
            }
        }

        interceptors = Collections.unmodifiableList(fsp);
    }

    @Override
    public List<GlobalInterceptor> getInterceptors()
    {
        return interceptors;
    }
}
