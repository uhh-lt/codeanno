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
package de.tudarmstadt.ukp.clarin.webanno.ui.config;

import java.util.List;

import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.JavaScriptReferenceHeaderItem;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.apache.wicket.resource.JQueryPluginResourceReference;

import com.googlecode.wicket.jquery.ui.resource.JQueryUIResourceReference;

import de.agilecoders.wicket.core.Bootstrap;
import de.agilecoders.wicket.core.settings.IBootstrapSettings;
import de.agilecoders.wicket.core.util.Dependencies;

/**
 * Customized {@link JQueryUIResourceReference} that depends on Bootstrap such that bootstrap is
 * loaded before JQuery UI. This is necessary in order for the JQuery UI tooltip that we use e.g. on
 * the annotation page to take precedence over the less powerful Bootstrap tooltip (both are JQuery
 * plugins using the same name!)
 */
public class BootstrapAwareJQueryUIJavaScriptResourceReference
    extends JQueryPluginResourceReference
{
    private static final long serialVersionUID = 1L;

    private static final BootstrapAwareJQueryUIJavaScriptResourceReference INSTANCE = //
            new BootstrapAwareJQueryUIJavaScriptResourceReference();

    /**
     * Gets the instance of the resource reference
     *
     * @return the single instance of the resource reference
     */
    public static BootstrapAwareJQueryUIJavaScriptResourceReference get()
    {
        return INSTANCE;
    }

    @Override
    public List<HeaderItem> getDependencies()
    {
        IBootstrapSettings settings = Bootstrap.getSettings();
        final JavaScriptReferenceHeaderItem jsReference = JavaScriptHeaderItem.forReference(
                settings.getJsResourceReference(), new PageParameters(), "bootstrap-js");
        return Dependencies.combine(super.getDependencies(), jsReference);
    }

    /**
     * Private constructor
     */
    private BootstrapAwareJQueryUIJavaScriptResourceReference()
    {
        super(JQueryUIResourceReference.class, "jquery-ui.js");
    }
}
