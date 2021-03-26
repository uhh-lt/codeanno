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
package de.tudarmstadt.ukp.clarin.webanno.support;

import static org.apache.commons.lang3.StringUtils.substring;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

public class SettingsUtil
{
    private static String propApplicationHome = "webanno.home";
    private static String applicationUserHomeSubdir = ".webanno";

    public static final String PROP_BUILD_NUMBER = "buildNumber";
    public static final String PROP_TIMESTAMP = "timestamp";
    public static final String PROP_VERSION = "version";

    private static final String PROP_USER_HOME = "user.home";

    private static final String SETTINGS_FILE = "settings.properties";

    public static final String CFG_LOCALE = "locale";
    public static final String CFG_STYLE_LOGO = "style.logo";
    public static final String CFG_LOGIN_MESSAGE = "login.message";
    public static final String CFG_AUTH_MODE = "auth.mode";
    public static final String CFG_AUTH_PREAUTH_NEWUSER_ROLES = "auth.preauth.newuser.roles";
    public static final String CFG_WARNINGS_EMBEDDED_DATABASE = "warnings.embeddedDatabase";
    public static final String CFG_WARNINGS_UNSUPPORTED_BROWSER = "warnings.unsupportedBrowser";
    public static final String CFG_USER_ALLOW_PROFILE_ACCESS = "user.profile.accessible";

    public static final String CFG_LINK_PREFIX = "style.header.icon.";
    public static final String CFG_LINK_URL = ".linkUrl";
    public static final String CFG_LINK_IMAGE_URL = ".imageUrl";

    private static Properties versionInfo;
    private static Properties settings;

    public static void customizeApplication(String aPropertyName, String aSubdirName)
    {
        propApplicationHome = aPropertyName;
        applicationUserHomeSubdir = aSubdirName;
    }

    public static String getApplicationUserHomeSubdir()
    {
        return applicationUserHomeSubdir;
    }

    public static String getPropApplicationHome()
    {
        return propApplicationHome;
    }

    public static Properties getVersionProperties()
    {
        if (versionInfo == null) {
            try {
                versionInfo = PropertiesLoaderUtils
                        .loadProperties(new ClassPathResource("META-INF/version.properties"));
            }
            catch (IOException e) {
                versionInfo = new Properties();
                versionInfo.setProperty(PROP_VERSION, "unknown");
                versionInfo.setProperty(PROP_TIMESTAMP, "unknown");
                versionInfo.setProperty(PROP_BUILD_NUMBER, "unknown");
            }
        }

        return versionInfo;
    }

    public static String getVersionString()
    {
        Properties props = getVersionProperties();
        if ("unknown".equals(props.getProperty(PROP_VERSION))) {
            return "Version information not available";
        }
        else {
            return props.getProperty(PROP_VERSION) + " (" + props.getProperty(PROP_TIMESTAMP)
                    + ", build " + substring(props.getProperty(PROP_BUILD_NUMBER), 0, 8) + ")";
        }
    }

    public static File getApplicationHome()
    {
        String appHome = System.getProperty(propApplicationHome);
        String userHome = System.getProperty(PROP_USER_HOME);

        if (appHome != null) {
            return new File(appHome);
        }
        else {
            return new File(userHome + "/" + applicationUserHomeSubdir);
        }
    }

    /**
     * Locate the settings file and return its location.
     * 
     * @return the location of the settings file or {@code null} if none could be found.
     */
    public static File getSettingsFile()
    {
        String appHome = System.getProperty(propApplicationHome);
        String userHome = System.getProperty(PROP_USER_HOME);

        // Locate settings, first in application, then in user home
        File settings = null;
        if (appHome != null) {
            settings = new File(appHome, SETTINGS_FILE);
        }
        else if (userHome != null) {
            settings = new File(userHome + "/" + applicationUserHomeSubdir, SETTINGS_FILE);
        }

        if (settings.exists()) {
            return settings;
        }
        else {
            return null;
        }
    }

    /**
     * 
     * @deprecated To access setting properties, use Spring Boot
     *             {@link org.springframework.boot.context.properties.ConfigurationProperties}
     *             classes implementing a corresponding interface instead (e.g. @see
     *             de.tudarmstadt.ukp.clarin.webanno.ui.core.users.RemoteApiProperties).
     */
    @Deprecated
    public static Properties getSettings()
    {
        if (settings == null) {
            settings = new Properties();
            File settingsFile = getSettingsFile();
            if (settingsFile != null) {
                try (InputStream in = new FileInputStream(settingsFile)) {
                    settings.load(in);
                }
                catch (IOException e) {
                    LoggerFactory.getLogger(SettingsUtil.class)
                            .error("Unable to load settings file [" + settings + "]", e);
                }
            }
        }
        return settings;
    }

    public static List<ImageLinkDecl> getLinks()
    {
        Properties props = getSettings();
        Map<String, ImageLinkDecl> linkMap = new HashMap<>();
        for (String key : props.stringPropertyNames()) {
            if (key.startsWith(CFG_LINK_PREFIX)) {
                String id = StringUtils.substringBetween(key, CFG_LINK_PREFIX, ".");

                // Create new declaration for current ID if there is none so far
                ImageLinkDecl e = linkMap.get(id);
                if (e == null) {
                    e = new ImageLinkDecl(id);
                    linkMap.put(id, e);
                }

                // Record link URL
                if (key.endsWith(CFG_LINK_URL)) {
                    e.setLinkUrl(props.getProperty(key));
                }
                // Record link URL
                if (key.endsWith(CFG_LINK_IMAGE_URL)) {
                    e.setImageUrl(props.getProperty(key));
                }
            }
        }

        // Sort by ID
        List<ImageLinkDecl> links = new ArrayList<>(linkMap.values());
        links.sort(Comparator.comparing(ImageLinkDecl::getId));

        return links;
    }
}
