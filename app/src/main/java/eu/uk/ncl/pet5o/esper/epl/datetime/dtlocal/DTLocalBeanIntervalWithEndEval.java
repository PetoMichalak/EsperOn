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
package eu.uk.ncl.pet5o.esper.epl.datetime.dtlocal;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenBlock;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCast;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class DTLocalBeanIntervalWithEndEval implements DTLocalEvaluator {
    private final EventPropertyGetter getterStartTimestamp;
    private final EventPropertyGetter getterEndTimestamp;
    private final DTLocalEvaluatorIntervalComp inner;

    public DTLocalBeanIntervalWithEndEval(EventPropertyGetter getterStartTimestamp, EventPropertyGetter getterEndTimestamp, DTLocalEvaluatorIntervalComp inner) {
        this.getterStartTimestamp = getterStartTimestamp;
        this.getterEndTimestamp = getterEndTimestamp;
        this.inner = inner;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object start = getterStartTimestamp.get((EventBean) target);
        if (start == null) {
            return null;
        }
        Object end = getterEndTimestamp.get((EventBean) target);
        if (end == null) {
            return null;
        }
        return inner.evaluate(start, end, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalBeanIntervalWithEndForge forge, CodegenExpression inner, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(Boolean.class, DTLocalBeanIntervalWithEndEval.class, codegenClassScope).addParam(EventBean.class, "target");

        CodegenBlock block = methodNode.getBlock();
        block.declareVar(forge.getterStartReturnType, "start", CodegenLegoCast.castSafeFromObjectType(forge.getterStartReturnType, forge.getterStartTimestamp.eventBeanGetCodegen(ref("target"), methodNode, codegenClassScope)));
        if (!forge.getterStartReturnType.isPrimitive()) {
            block.ifRefNullReturnNull("start");
        }
        block.declareVar(forge.getterEndReturnType, "end", CodegenLegoCast.castSafeFromObjectType(forge.getterEndReturnType, forge.getterEndTimestamp.eventBeanGetCodegen(ref("target"), methodNode, codegenClassScope)));
        if (!forge.getterEndReturnType.isPrimitive()) {
            block.ifRefNullReturnNull("end");
        }
        block.methodReturn(forge.inner.codegen(ref("start"), ref("end"), methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
