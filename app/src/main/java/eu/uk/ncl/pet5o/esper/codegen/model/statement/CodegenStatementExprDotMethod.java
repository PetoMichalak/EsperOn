/*
 ***************************************************************************************
 *  Copyright (C) 2006 EsperTech, Inc. All rights reserved.                            *
 *  http://www.espertech.com/esper                                                     *
 *  http://www.espertech.com                                                           *
 *  ---------------------------------------------------------------------------------- *
 *  The software in this package is published under the terms of the GPL license       *
 *  a copy of which has been included with this distribution in the license.txt file.  *
 ***************************************************************************************
 */
package eu.uk.ncl.pet5o.esper.codegen.model.statement;

import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;

import java.util.Map;
import java.util.Set;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.mergeClassesExpressions;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.renderExpressions;

public class CodegenStatementExprDotMethod extends CodegenStatementBase {
    private final CodegenExpression expression;
    private final String method;
    private final CodegenExpression[] params;

    public CodegenStatementExprDotMethod(CodegenExpression expression, String method, CodegenExpression[] params) {
        this.expression = expression;
        this.method = method;
        this.params = params;
    }

    public void renderStatement(StringBuilder builder, Map<Class, String> imports, boolean isInnerClass) {
        if (expression instanceof CodegenExpressionRef) {
            expression.render(builder, imports, isInnerClass);
        } else {
            builder.append("(");
            expression.render(builder, imports, isInnerClass);
            builder.append(")");
        }
        builder.append('.').append(method).append("(");
        renderExpressions(builder, params, imports, isInnerClass);
        builder.append(")");
    }

    public void mergeClasses(Set<Class> classes) {
        expression.mergeClasses(classes);
        mergeClassesExpressions(classes, params);
    }
}
