/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
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
package com.intellij.codeInspection;

import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.uast.UastVisitorAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.uast.UClass;
import org.jetbrains.uast.UField;
import org.jetbrains.uast.UFile;
import org.jetbrains.uast.UMethod;
import org.jetbrains.uast.visitor.AbstractUastVisitor;

public abstract class AbstractBaseUastLocalInspectionTool extends LocalInspectionTool {
  private static final Condition<PsiElement> PROBLEM_ELEMENT_CONDITION = Conditions.and(Conditions.instanceOf(PsiFile.class, PsiClass.class, PsiMethod.class, PsiField.class), Conditions.notInstanceOf(PsiTypeParameter.class));

  /**
   * Override this to report problems at method level.
   *
   * @param method     to check.
   * @param manager    InspectionManager to ask for ProblemDescriptors from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return {@code null} if no problems found or not applicable at method level.
   */
  @Nullable
  public ProblemDescriptor[] checkMethod(@NotNull UMethod method, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  /**
   * Override this to report problems at class level.
   *
   * @param aClass     to check.
   * @param manager    InspectionManager to ask for ProblemDescriptors from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return {@code null} if no problems found or not applicable at class level.
   */
  @Nullable
  public ProblemDescriptor[] checkClass(@NotNull UClass aClass, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  /**
   * Override this to report problems at field level.
   *
   * @param field      to check.
   * @param manager    InspectionManager to ask for ProblemDescriptors from.
   * @param isOnTheFly true if called during on the fly editor highlighting. Called from Inspect Code action otherwise.
   * @return {@code null} if no problems found or not applicable at field level.
   */
  @Nullable
  public ProblemDescriptor[] checkField(@NotNull UField field, @NotNull InspectionManager manager, boolean isOnTheFly) {
    return null;
  }

  @Override
  @NotNull
  public PsiElementVisitor buildVisitor(@NotNull final ProblemsHolder holder, final boolean isOnTheFly) {
    return new UastVisitorAdapter(new AbstractUastVisitor() {
      @Override
      public boolean visitClass(UClass node) {
        addDescriptors(checkClass(node, holder.getManager(), isOnTheFly));
        return true;
      }

      @Override
      public boolean visitMethod(UMethod node) {
        addDescriptors(checkMethod(node, holder.getManager(), isOnTheFly));
        return true;
      }

      @Override
      public boolean visitField(UField node) {
        addDescriptors(checkField(node, holder.getManager(), isOnTheFly));
        return true;
      }

      @Override
      public boolean visitFile(UFile node) {
        addDescriptors(checkFile(node.getPsi(), holder.getManager(), isOnTheFly));
        return true;
      }

      private void addDescriptors(final ProblemDescriptor[] descriptors) {
        if (descriptors != null) {
          for (ProblemDescriptor descriptor : descriptors) {
            holder.registerProblem(descriptor);
          }
        }
      }
    });
  }

  @Override
  public PsiNamedElement getProblemElement(final PsiElement psiElement) {
    return (PsiNamedElement)PsiTreeUtil.findFirstParent(psiElement, PROBLEM_ELEMENT_CONDITION);
  }
}
