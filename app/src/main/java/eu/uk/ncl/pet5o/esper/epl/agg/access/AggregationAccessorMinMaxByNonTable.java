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

import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotUnderlying;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;

/**
 * Represents the aggregation accessor that provides the result for the "maxBy" aggregation function.
 */
public class AggregationAccessorMinMaxByNonTable extends AggregationAccessorMinMaxByBase implements AggregationAccessorForge {
    public AggregationAccessorMinMaxByNonTable(boolean max) {
        super(max);
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL;
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = getEnumerableEvent(state, eventsPerStream, isNewData, context);
        if (event == null) {
            return null;
        }
        return event.getUnderlying();
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        AggregationStateSortedCodegen forge = (AggregationStateSortedCodegen) context.getAccessStateForge();
        context.getMethod().getBlock().declareVar(eu.uk.ncl.pet5o.esper.client.EventBean.class, "event", max ? forge.getLastValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()) : forge.getFirstValueCodegen(context.getColumn(), context.getClassScope(), context.getMethod()))
                .ifRefNullReturnNull("event")
                .methodReturn(exprDotUnderlying(ref("event")));
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }
}
