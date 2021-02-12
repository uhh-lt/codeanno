/*
 * Copyright 2021
 * Language Technology (LT) Universität Hamburg
 * and Ubiquitous Knowledge Processing (UKP) Lab Technische Universität Darmstadt
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
package de.tudarmstadt.ukp.clarin.webanno.webapp.config;

import java.io.PrintStream;

import org.springframework.boot.Banner;
import org.springframework.boot.ansi.AnsiColor;
import org.springframework.boot.ansi.AnsiOutput;
import org.springframework.boot.ansi.AnsiStyle;
import org.springframework.core.env.Environment;

import de.tudarmstadt.ukp.clarin.webanno.support.SettingsUtil;

public class CodeAnnoBanner
        implements Banner
{
    // @formatter:off
    private static final String[] BANNER = {
            "",
            "     ______            __       ___",
            "    / ____/____   ____/ /___   /   |   ____   ____   ____",
            "   / /    / __ \\ / __  // _ \\ / /| |  / __ \\ / __ \\ / __ \\",
            "  / /___ / /_/ // /_/ //  __// ___ | / / / // / / // /_/ /",
            "  \\____/ \\____/ \\__,_/ \\___//_/  |_|/_/ /_//_/ /_/ \\____/",
            ""
    };
    // @formatter:on

    @Override
    public void printBanner(Environment environment, Class<?> sourceClass, PrintStream printStream)
    {
        for (String line : BANNER) {
            printStream.println(line);
        }
        String version = SettingsUtil.getVersionString();
        printStream.println(AnsiOutput.toString(AnsiColor.GREEN, AnsiStyle.FAINT, version));
        printStream.println();
    }
}
