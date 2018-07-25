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

import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateLinearForge;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

/**
 * Represents the aggregation accessor that provides the result for the "last" aggregation function without index.
 */
public class AggregationAccessorLastWEvalForge implements AggregationAccessorForge {
    private final int streamNum;
    private final ExprForge childNode;

    /**
     * Ctor.
     *
     * @param streamNum stream id
     * @param childNode expression
     */
    public AggregationAccessorLastWEvalForge(int streamNum, ExprForge childNode) {
        this.streamNum = streamNum;
        this.childNode = childNode;
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator childEval = ExprNodeCompiler.allocateEvaluator(childNode, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new AggregationAccessorLastWEval(streamNum, childEval);
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL;
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorLastWEval.getValueCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public void getEnumerableEventsCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorLastWEval.getEnumerableEventsCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public void getEnumerableEventCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorLastWEval.getEnumerableEventCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public void getEnumerableScalarCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorLastWEval.getEnumerableScalarCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public int getStreamNum() {
        return streamNum;
    }

    public ExprForge getChildNode() {
        return childNode;
    }
}
