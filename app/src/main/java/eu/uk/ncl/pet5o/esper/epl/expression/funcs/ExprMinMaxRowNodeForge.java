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
package eu.uk.ncl.pet5o.esper.epl.expression.funcs;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;

public class ExprMinMaxRowNodeForge implements ExprForge {
    private final ExprMinMaxRowNode parent;
    private final Class resultType;

    public ExprMinMaxRowNodeForge(ExprMinMaxRowNode parent, Class resultType) {
        this.parent = parent;
        this.resultType = resultType;
    }

    public ExprMinMaxRowNode getForgeRenderable() {
        return parent;
    }

    public ExprEvaluator getExprEvaluator() {
        ExprEvaluator[] evaluators = ExprNodeUtilityCore.getEvaluatorsNoCompile(parent.getChildNodes());
        return new ExprMinMaxRowNodeForgeEval(this, evaluators, ExprNodeUtilityCore.getForges(parent.getChildNodes()));
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprMinMaxRowNodeForgeEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return resultType;
    }
}
