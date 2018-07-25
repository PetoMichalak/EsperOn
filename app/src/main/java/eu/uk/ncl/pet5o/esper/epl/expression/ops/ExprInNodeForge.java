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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.util.SimpleNumberCoercer;

/**
 * Represents the in-clause (set check) function in an expression tree.
 */
public class ExprInNodeForge implements ExprForge {
    private final ExprInNodeImpl parent;
    private final boolean mustCoerce;
    private final SimpleNumberCoercer coercer;
    private final Class coercionType;
    private final boolean hasCollectionOrArray;

    public ExprInNodeForge(ExprInNodeImpl parent, boolean mustCoerce, SimpleNumberCoercer coercer, Class coercionType, boolean hasCollectionOrArray) {
        this.parent = parent;
        this.mustCoerce = mustCoerce;
        this.coercer = coercer;
        this.coercionType = coercionType;
        this.hasCollectionOrArray = hasCollectionOrArray;
    }

    public ExprEvaluator getExprEvaluator() {
        ExprEvaluator[] evaluators = ExprNodeUtilityCore.getEvaluatorsNoCompile(parent.getChildNodes());
        if (hasCollectionOrArray) {
            return new ExprInNodeForgeEvalWColl(this, evaluators);
        }
        return new ExprInNodeForgeEvalNoColl(this, evaluators);
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return ExprInNodeForgeEvalWColl.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getEvaluationType() {
        return Boolean.class;
    }

    public ExprInNodeImpl getForgeRenderable() {
        return parent;
    }

    public boolean isMustCoerce() {
        return mustCoerce;
    }

    public SimpleNumberCoercer getCoercer() {
        return coercer;
    }

    public Class getCoercionType() {
        return coercionType;
    }

    public boolean isHasCollectionOrArray() {
        return hasCollectionOrArray;
    }
}
