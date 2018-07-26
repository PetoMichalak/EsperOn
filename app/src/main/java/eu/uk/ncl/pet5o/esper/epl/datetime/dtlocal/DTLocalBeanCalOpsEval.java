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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCast;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class DTLocalBeanCalOpsEval implements DTLocalEvaluator {
    private final DTLocalBeanCalOpsForge forge;
    private final DTLocalEvaluator inner;

    public DTLocalBeanCalOpsEval(DTLocalBeanCalOpsForge forge, DTLocalEvaluator inner) {
        this.forge = forge;
        this.inner = inner;
    }

    public Object evaluate(Object target, EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object timestamp = forge.getter.get((EventBean) target);
        if (timestamp == null) {
            return null;
        }
        return inner.evaluate(timestamp, eventsPerStream, isNewData, exprEvaluatorContext);
    }

    public static CodegenExpression codegen(DTLocalBeanCalOpsForge forge, CodegenExpression inner, Class innerType, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.innerReturnType, DTLocalBeanCalOpsEval.class, codegenClassScope).addParam(EventBean.class, "target");

        methodNode.getBlock().declareVar(forge.getterReturnType, "timestamp", CodegenLegoCast.castSafeFromObjectType(forge.getterReturnType, forge.getter.eventBeanGetCodegen(ref("target"), methodNode, codegenClassScope)));
        if (!forge.getterReturnType.isPrimitive()) {
            methodNode.getBlock().ifRefNullReturnNull("timestamp");
        }
        methodNode.getBlock().methodReturn(forge.inner.codegen(ref("timestamp"), forge.getterReturnType, methodNode, exprSymbol, codegenClassScope));
        return localMethod(methodNode, inner);
    }
}
