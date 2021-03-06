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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Represents the aggregation accessor that provides the result for the "window" aggregation function.
 */
public class AggregationAccessorWindowNoEval implements AggregationAccessor, AggregationAccessorForge {
    private final Class componentType;

    public AggregationAccessorWindowNoEval(Class componentType) {
        this.componentType = componentType;
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL; // not currently applicable as table-related only
    }

    public Object getValue(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        if (linear.size() == 0) {
            return null;
        }
        Object array = Array.newInstance(componentType, linear.size());
        Iterator<EventBean> it = linear.iterator();
        int count = 0;
        for (; it.hasNext(); ) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = it.next();
            Array.set(array, count++, bean.getUnderlying());
        }
        return array;
    }

    public Collection<EventBean> getEnumerableEvents(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        if (linear.size() == 0) {
            return null;
        }
        return linear.collectionReadOnly();
    }

    public Collection<Object> getEnumerableScalar(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        AggregationStateLinear linear = (AggregationStateLinear) state;
        if (linear.size() == 0) {
            return null;
        }
        List<Object> values = new ArrayList<Object>(linear.size());
        Iterator<EventBean> it = linear.iterator();
        for (; it.hasNext(); ) {
            eu.uk.ncl.pet5o.esper.client.EventBean bean = it.next();
            values.add(bean.getUnderlying());
        }
        return values;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean getEnumerableEvent(AggregationState state, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext) {
        return null;
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
