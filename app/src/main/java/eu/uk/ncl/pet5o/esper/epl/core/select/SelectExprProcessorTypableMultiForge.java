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
package eu.uk.ncl.pet5o.esper.epl.core.select;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeRenderable;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprTypableReturnForge;
import eu.uk.ncl.pet5o.esper.event.EventBeanManufacturer;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.TypeWidener;

public class SelectExprProcessorTypableMultiForge implements SelectExprProcessorTypableForge {

    protected final ExprTypableReturnForge typable;
    protected final boolean hasWideners;
    protected final TypeWidener[] wideners;
    protected final EventBeanManufacturer factory;
    protected final EventType targetType;
    protected final boolean firstRowOnly;

    public SelectExprProcessorTypableMultiForge(ExprTypableReturnForge typable, boolean hasWideners, TypeWidener[] wideners, EventBeanManufacturer factory, EventType targetType, boolean firstRowOnly) {
        this.typable = typable;
        this.hasWideners = hasWideners;
        this.wideners = wideners;
        this.factory = factory;
        this.targetType = targetType;
        this.firstRowOnly = firstRowOnly;
    }

    public ExprEvaluator getExprEvaluator() {
        if (firstRowOnly) {
            return new SelectExprProcessorTypableMultiEvalFirstRow(this, typable.getTypableReturnEvaluator());
        }
        return new SelectExprProcessorTypableMultiEval(this, typable.getTypableReturnEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (firstRowOnly) {
            return SelectExprProcessorTypableMultiEvalFirstRow.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
        return SelectExprProcessorTypableMultiEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getUnderlyingEvaluationType() {
        if (firstRowOnly) {
            return targetType.getUnderlyingType();
        }
        return JavaClassHelper.getArrayType(targetType.getUnderlyingType());
    }

    public Class getEvaluationType() {
        if (firstRowOnly) {
            return eu.uk.ncl.pet5o.esper.client.EventBean.class;
        }
        return eu.uk.ncl.pet5o.esper.client.EventBean[].class;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return typable.getForgeRenderable();
    }
}
