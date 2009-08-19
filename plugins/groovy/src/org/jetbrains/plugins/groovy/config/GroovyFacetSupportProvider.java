/*
 * Copyright 2000-2007 JetBrains s.r.o.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.plugins.groovy.config;

import com.intellij.ide.util.newProjectWizard.FrameworkSupportConfigurable;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportModel;
import com.intellij.ide.util.newProjectWizard.FrameworkSupportProvider;
import com.intellij.ide.util.projectWizard.ModuleBuilder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.plugins.groovy.config.ui.GroovyFacetEditor;

/**
 * @author peter
 */
public class GroovyFacetSupportProvider extends FrameworkSupportProvider {
  protected GroovyFacetSupportProvider() {
    super("Groovy", "Groovy et al");
  }

  @Override
  public boolean isEnabledForModuleBuilder(@NotNull ModuleBuilder builder) {
    return super.isEnabledForModuleBuilder(builder) && !(builder instanceof GroovyAwareModuleBuilder);
  }

  @NotNull
  public FrameworkSupportConfigurable createConfigurable(final @NotNull FrameworkSupportModel model) {
    return new GroovySupportConfigurable(new GroovyFacetEditor(model.getProject()));
  }

}
