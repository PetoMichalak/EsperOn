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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

import java.util.Collection;
import java.util.Collections;

/**
 * Represents the aggregation accessor that provides the result for the "first" aggregation function without index.
 */
public class AggregationAccessorFirstNoEval implements AggregationAccessor, AggregationAccessorForge {
    public final static AggregationAccessorFirstNoEval INSTANCE = new AggregationAccessorFirstNoEval();

    private AggregationAccessorFirstNoEval() {
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL; // not currently applicable as table-related only
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = ((AggregationStateLinear) state).getFirstValue();
        if (bean == null) {
            return null;
        }
        return bean.getUnderlying();
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = ((AggregationStateLinear) state).getFirstValue();
        if (bean == null) {
            return null;
        }
        return Collections.singletonList(bean);
    }

    public Collection<Object> getEnumerableScalar(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        Object value = getValue(state, eventsPerStream, isNewData, exprEvaluatorContext);
        if (value == null) {
            return null;
        }
        return Collections.singletonList(value);
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return ((AggregationStateLinear) state).getFirstValue();
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableEventsCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableEventCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableScalarCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }
}
