/*
 * Copyright 2016 the original author or authors.
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

package org.gradle.plugin.devel.tasks;

import org.apache.commons.io.FileUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.Incubating;
import org.gradle.api.UncheckedIOException;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.util.PropertiesUtils;
import org.gradle.plugin.devel.PluginDeclaration;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * Generates plugin descriptors from plugin declarations.
 */
@Incubating
public class GeneratePluginDescriptors extends DefaultTask {

    private final ListProperty<PluginDeclaration> declarations;
    private final DirectoryProperty outputDirectory;

    public GeneratePluginDescriptors() {
        ObjectFactory objectFactory = getProject().getObjects();
        declarations = objectFactory.listProperty(PluginDeclaration.class).empty();
        outputDirectory = objectFactory.directoryProperty();
    }

    @Input
    public ListProperty<PluginDeclaration> getDeclarations() {
        return declarations;
    }

    @OutputDirectory
    public DirectoryProperty getOutputDirectory() {
        return outputDirectory;
    }

    @TaskAction
    public void generatePluginDescriptors() {
        File outputDir = outputDirectory.get().getAsFile();
        clearOutputDirectory(outputDir);
        for (PluginDeclaration declaration : getDeclarations().get()) {
            File descriptorFile = new File(outputDir, declaration.getId() + ".properties");
            Properties properties = new Properties();
            properties.setProperty("implementation-class", declaration.getImplementationClass());
            writePropertiesTo(properties, descriptorFile);
        }
    }

    private void clearOutputDirectory(File directoryToClear) {
        try {
            FileUtils.cleanDirectory(directoryToClear);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void writePropertiesTo(Properties properties, File descriptorFile) {
        try {
            PropertiesUtils.store(properties, descriptorFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
