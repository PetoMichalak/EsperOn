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
package eu.uk.ncl.pet5o.esper.epl.expression.ops;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;

public abstract class ExprLikeNodeForge implements ExprForge {
    private final ExprLikeNode parent;
    private final boolean isNumericValue;

    public abstract ExprEvaluator getExprEvaluator();

    public abstract CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope);

    public ExprLikeNodeForge(ExprLikeNode parent, boolean isNumericValue) {
        this.parent = parent;
        this.isNumericValue = isNumericValue;
    }

    public ExprLikeNode getForgeRenderable() {
        return parent;
    }

    public boolean isNumericValue() {
        return isNumericValue;
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }
}
