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

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;

public class AggregationAgentDefault implements AggregationAgent, AggregationAgentForge {
    public final static AggregationAgentDefault INSTANCE = new AggregationAgentDefault();

    private AggregationAgentDefault() {
    }

    public AggregationAgent makeAgent(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        aggregationState.applyEnter(eventsPerStream, exprEvaluatorContext);
    }

    public void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext, AggregationState aggregationState) {
        aggregationState.applyLeave(eventsPerStream, exprEvaluatorContext);
    }

    public CodegenExpression applyEnterCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return exprDotMethod(symbols.getAddState(parent), "applyEnter", REF_EPS, REF_EXPREVALCONTEXT);
    }

    public CodegenExpression applyLeaveCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return exprDotMethod(symbols.getAddState(parent), "applyLeave", REF_EPS, REF_EXPREVALCONTEXT);
    }

    public ExprForge getOptionalFilter() {
        return null;
    }
}
