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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import java.util.Collections;
import java.util.Map;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.staticMethod;

public class SelectExprProcessorTypableMapEval implements ExprEvaluator {
    private final SelectExprProcessorTypableMapForge forge;
    private final ExprEvaluator innerEvaluator;

    public SelectExprProcessorTypableMapEval(SelectExprProcessorTypableMapForge forge, ExprEvaluator innerEvaluator) {
        this.forge = forge;
        this.innerEvaluator = innerEvaluator;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Map<String, Object> values = (Map<String, Object>) innerEvaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (values == null) {
            return forge.eventAdapterService.adapterForTypedMap(Collections.<String, Object>emptyMap(), forge.mapType);
        }
        return forge.eventAdapterService.adapterForTypedMap(values, forge.mapType);
    }

    public static CodegenExpression codegen(SelectExprProcessorTypableMapForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMember eventAdapterService = codegenClassScope.makeAddMember(EventAdapterService.class, forge.eventAdapterService);
        CodegenMember mapType = codegenClassScope.makeAddMember(EventType.class, forge.mapType);
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(eu.uk.ncl.pet5o.esper.client.EventBean.class, SelectExprProcessorTypableMapEval.class, codegenClassScope);

        methodNode.getBlock()
                .declareVar(Map.class, "values", forge.innerForge.evaluateCodegen(Map.class, methodNode, exprSymbol, codegenClassScope))
                .declareVarNoInit(Map.class, "map")
                .ifRefNull("values")
                .assignRef("values", staticMethod(Collections.class, "emptyMap"))
                .blockEnd()
                .methodReturn(exprDotMethod(member(eventAdapterService.getMemberId()), "adapterForTypedMap", ref("values"), member(mapType.getMemberId())));
        return localMethod(methodNode);
    }

}
