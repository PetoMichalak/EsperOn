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

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;

/**
 * Represents the aggregation accessor that provides the result for the "window" aggregation function.
 */
public class AggregationAccessorWindowWEvalForge implements AggregationAccessorForge {
    private final int streamNum;
    private final ExprForge childNode;
    private final Class componentType;

    /**
     * Ctor.
     *
     * @param streamNum     stream id
     * @param childNode     expression
     * @param componentType type
     */
    public AggregationAccessorWindowWEvalForge(int streamNum, ExprForge childNode, Class componentType) {
        this.streamNum = streamNum;
        this.childNode = childNode;
        this.componentType = componentType;
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator child = ExprNodeCompiler.allocateEvaluator(childNode, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new AggregationAccessorWindowWEval(streamNum, child, componentType);
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL;
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorWindowWEval.getValueCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public void getEnumerableEventsCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorWindowWEval.getEnumerableEventsCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public void getEnumerableEventCodegen(AggregationAccessorForgeGetCodegenContext context) {
        context.getMethod().getBlock().methodReturn(constantNull());
    }

    public void getEnumerableScalarCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationAccessorWindowWEval.getEnumerableScalarCodegen(this, (AggregationStateLinearForge) context.getAccessStateForge(), context);
    }

    public int getStreamNum() {
        return streamNum;
    }

    public ExprForge getChildNode() {
        return childNode;
    }

    public Class getComponentType() {
        return componentType;
    }
}
