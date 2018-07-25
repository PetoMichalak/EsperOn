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
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.arrayAtIndex;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayWithInit;

public class AggregationAgentRewriteStream implements AggregationAgent, AggregationAgentForge {

    private final int streamNum;

    public AggregationAgentRewriteStream(int streamNum) {
        this.streamNum = streamNum;
    }

    public AggregationAgent makeAgent(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] rewrite = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventsPerStream[streamNum]};
        aggregationState.applyEnter(rewrite, exprEvaluatorContext);
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] rewrite = new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventsPerStream[streamNum]};
        aggregationState.applyLeave(rewrite, exprEvaluatorContext);
    }

    public CodegenExpression applyEnterCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(true, parent, symbols, classScope);
    }

    public CodegenExpression applyLeaveCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return applyCodegen(false, parent, symbols, classScope);
    }

    private CodegenExpression applyCodegen(boolean enter, CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return exprDotMethod(symbols.getAddState(parent), enter ? "applyEnter" : "applyLeave", newArrayWithInit(eu.uk.ncl.pet5o.esper.client.EventBean.class, arrayAtIndex(symbols.getAddEPS(parent), constant(streamNum))), symbols.getAddExprEvalCtx(parent));
    }

    public ExprForge getOptionalFilter() {
        return null;
    }
}
