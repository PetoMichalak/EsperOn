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
import eu.uk.ncl.pet5o.esper.epl.expression.core.*;
import eu.uk.ncl.pet5o.esper.event.EventBeanManufacturer;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.util.TypeWidener;

import java.io.StringWriter;

public class SelectExprProcessorTypableSingleForge implements SelectExprProcessorTypableForge, ExprNodeRenderable {
    protected final ExprTypableReturnForge typable;
    protected final boolean hasWideners;
    protected final TypeWidener[] wideners;
    protected final EventBeanManufacturer factory;
    protected final EventType targetType;
    protected final boolean singleRowOnly;

    public SelectExprProcessorTypableSingleForge(ExprTypableReturnForge typable, boolean hasWideners, TypeWidener[] wideners, EventBeanManufacturer factory, EventType targetType, boolean singleRowOnly) {
        this.typable = typable;
        this.hasWideners = hasWideners;
        this.wideners = wideners;
        this.factory = factory;
        this.targetType = targetType;
        this.singleRowOnly = singleRowOnly;
    }

    public ExprEvaluator getExprEvaluator() {
        if (singleRowOnly) {
            return new SelectExprProcessorTypableSingleEvalSingleRow(this, typable.getTypableReturnEvaluator());
        }
        return new SelectExprProcessorTypableSingleEval(this, typable.getTypableReturnEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        if (singleRowOnly) {
            return SelectExprProcessorTypableSingleEvalSingleRow.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
        }
        return SelectExprProcessorTypableSingleEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getUnderlyingEvaluationType() {
        if (singleRowOnly) {
            return targetType.getUnderlyingType();
        }
        return JavaClassHelper.getArrayType(targetType.getUnderlyingType());
    }

    public Class getEvaluationType() {
        if (singleRowOnly) {
            return eu.uk.ncl.pet5o.esper.client.EventBean.class;
        }
        return eu.uk.ncl.pet5o.esper.client.EventBean[].class;
    }

    public void toEPL(StringWriter writer, ExprPrecedenceEnum parentPrecedence) {
        typable.getForgeRenderable().toEPL(writer, parentPrecedence);
    }

    public ExprNodeRenderable getForgeRenderable() {
        return this;
    }
}
