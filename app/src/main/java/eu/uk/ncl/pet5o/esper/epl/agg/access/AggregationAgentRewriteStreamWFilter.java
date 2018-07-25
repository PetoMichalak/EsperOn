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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayWithInit;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class AggregationAgentRewriteStreamWFilter implements AggregationAgent {

    private final int streamNum;
    private final ExprEvaluator filterEval;

    public AggregationAgentRewriteStreamWFilter(int streamNum, ExprEvaluator filterEval) {
        this.streamNum = streamNum;
        this.filterEval = filterEval;
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] rewrite = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventsPerStream[streamNum]};
            aggregationState.applyEnter(rewrite, exprEvaluatorContext);
        }
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, false, exprEvaluatorContext);
        if (pass != null && pass) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] rewrite = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventsPerStream[streamNum]};
            aggregationState.applyLeave(rewrite, exprEvaluatorContext);
        }
    }

    public static CodegenExpression applyEnterCodegen(AggregationAgentRewriteStreamWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(true, forge, parent, symbols, classScope);
    }

    public static CodegenExpression applyLeaveCodegen(AggregationAgentRewriteStreamWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(false, forge, parent, symbols, classScope);
    }

    private static CodegenExpression applyCodegen(boolean enter, AggregationAgentRewriteStreamWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenMethodNode method = parent.makeChild(void.class, AggregationAgentRewriteStreamWFilter.class, classScope);
        Class evalType = forge.getFilterEval().getEvaluationType();
        method.getBlock().declareVar(evalType, "pass", forge.getFilterEval().evaluateCodegen(evalType, method, symbols, classScope));
        if (!evalType.isPrimitive()) {
            method.getBlock().ifRefNull("pass").blockReturnNoValue();
        }
        method.getBlock().ifCondition(not(ref("pass"))).blockReturnNoValue();
        CodegenExpressionRef state = symbols.getAddState(method);
        method.getBlock()
                .declareVar(eu.uk.ncl.pet5o.esper.client.EventBean[].class, "rewrite", newArrayWithInit(eu.uk.ncl.pet5o.esper.client.EventBean.class, arrayAtIndex(symbols.getAddEPS(method), constant(forge.getStreamNum()))))
                .expression(exprDotMethod(state, enter ? "applyEnter" : "applyLeave", ref("rewrite"), symbols.getAddExprEvalCtx(method)));
        return localMethod(method);
    }
}
