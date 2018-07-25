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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForgeComplexityEnum;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeRenderable;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import java.util.Map;

public class SelectExprProcessorTypableMapForge implements SelectExprProcessorTypableForge {
    protected final EventType mapType;
    protected final ExprForge innerForge;
    protected  final EventAdapterService eventAdapterService;

    public SelectExprProcessorTypableMapForge(EventType mapType, ExprForge innerForge, EventAdapterService eventAdapterService) {
        this.mapType = mapType;
        this.innerForge = innerForge;
        this.eventAdapterService = eventAdapterService;
    }

    public ExprEvaluator getExprEvaluator() {
        return new SelectExprProcessorTypableMapEval(this, innerForge.getExprEvaluator());
    }

    public CodegenExpression evaluateCodegen(Class requiredType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        return SelectExprProcessorTypableMapEval.codegen(this, codegenMethodScope, exprSymbol, codegenClassScope);
    }

    public ExprForgeComplexityEnum getComplexity() {
        return ExprForgeComplexityEnum.INTER;
    }

    public Class getUnderlyingEvaluationType() {
        return Map.class;
    }

    public Class getEvaluationType() {
        return eu.uk.ncl.pet5o.esper.client.EventBean.class;
    }

    public ExprForge getInnerForge() {
        return innerForge;
    }

    public ExprNodeRenderable getForgeRenderable() {
        return innerForge.getForgeRenderable();
    }
}
