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
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggAggregationAgentFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

public class AggregationAgentDefaultWFilterForge implements AggregationAgentForge {
    private final ExprForge filterEval;

    public AggregationAgentDefaultWFilterForge(ExprForge filterEval) {
        this.filterEval = filterEval;
    }

    public AggregationAgent makeAgent(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator evaluator = ExprNodeCompiler.allocateEvaluator(filterEval, engineImportService, ExprAggAggregationAgentFactory.class, isFireAndForget, statementName);
        return new AggregationAgentDefaultWFilter(evaluator);
    }

    public CodegenExpression applyEnterCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return AggregationAgentDefaultWFilter.applyEnterCodegen(this, parent, symbols, classScope);
    }

    public CodegenExpression applyLeaveCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return AggregationAgentDefaultWFilter.applyLeaveCodegen(this, parent, symbols, classScope);
    }

    public ExprForge getFilterEval() {
        return filterEval;
    }

    public ExprForge getOptionalFilter() {
        return filterEval;
    }
}
