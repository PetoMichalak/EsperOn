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
package eu.uk.ncl.pet5o.esper.epl.expression.dot.inner;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.CodegenLegoCast;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.dot.ExprDotEvalRootChildInnerEval;

import java.util.Collection;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class InnerDotScalarUnpackEventEval implements ExprDotEvalRootChildInnerEval {

    private ExprEvaluator rootEvaluator;

    public InnerDotScalarUnpackEventEval(ExprEvaluator rootEvaluator) {
        this.rootEvaluator = rootEvaluator;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object target = rootEvaluator.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        if (target instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
            return ((eu.uk.ncl.pet5o.esper.client.EventBean) target).getUnderlying();
        }
        return target;
    }

    public static CodegenExpression codegenEvaluate(InnerDotScalarUnpackEventForge forge, CodegenMethodScope codegenMethodScope, ExprForgeCodegenSymbol exprSymbol, CodegenClassScope codegenClassScope) {
        CodegenMethodNode methodNode = codegenMethodScope.makeChild(forge.getRootForge().getEvaluationType(), InnerDotScalarUnpackEventEval.class, codegenClassScope);

        methodNode.getBlock()
                .declareVar(Object.class, "target", forge.getRootForge().evaluateCodegen(Object.class, methodNode, exprSymbol, codegenClassScope))
                .ifInstanceOf("target", eu.uk.ncl.pet5o.esper.client.EventBean.class)
                .blockReturn(CodegenLegoCast.castSafeFromObjectType(forge.getRootForge().getEvaluationType(), exprDotMethod(cast(eu.uk.ncl.pet5o.esper.client.EventBean.class, ref("target")), "getUnderlying")))
                .methodReturn(CodegenLegoCast.castSafeFromObjectType(forge.getRootForge().getEvaluationType(), ref("target")));
        return localMethod(methodNode);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

}
