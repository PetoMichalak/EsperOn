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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.localMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

public class AggregationAgentDefaultWFilter implements AggregationAgent {
    private final ExprEvaluator filterEval;

    public AggregationAgentDefaultWFilter(ExprEvaluator filterEval) {
        this.filterEval = filterEval;
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, true, exprEvaluatorContext);
        if (pass != null && pass) {
            aggregationState.applyEnter(eventsPerStream, exprEvaluatorContext);
        }
    }

    public static CodegenExpression applyEnterCodegen(AggregationAgentDefaultWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(true, forge, parent, symbols, classScope);
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        Boolean pass = (Boolean) filterEval.evaluate(eventsPerStream, false, exprEvaluatorContext);
        if (pass != null && pass) {
            aggregationState.applyLeave(eventsPerStream, exprEvaluatorContext);
        }
    }

    public static CodegenExpression applyLeaveCodegen(AggregationAgentDefaultWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(false, forge, parent, symbols, classScope);
    }

    private static CodegenExpression applyCodegen(boolean enter, AggregationAgentDefaultWFilterForge forge, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(enter, forge.getFilterEval(), parent, symbols, classScope);
    }

    public static CodegenExpression applyCodegen(boolean enter, ExprForge optionalFilter, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        CodegenMethodNode method = parent.makeChild(void.class, AggregationAgentDefaultWFilter.class, classScope);

        if (optionalFilter != null) {
            Class evalType = optionalFilter.getEvaluationType();
            method.getBlock().declareVar(evalType, "pass", optionalFilter.evaluateCodegen(evalType, method, symbols, classScope));
            if (!evalType.isPrimitive()) {
                method.getBlock().ifRefNull("pass").blockReturnNoValue();
            }
            method.getBlock().ifCondition(not(ref("pass"))).blockReturnNoValue();
        }
        CodegenExpressionRef state = symbols.getAddState(method);
        method.getBlock().expression(exprDotMethod(state, enter ? "applyEnter" : "applyLeave", symbols.getAddEPS(method), symbols.getAddExprEvalCtx(method)));
        return localMethod(method);
    }
}
