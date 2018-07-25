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
package eu.uk.ncl.pet5o.esper.epl.table.strategy;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.service.common.AggregationRowPair;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import com.espertech.esper.epl.table.mgmt.TableMetadataColumn;
import com.espertech.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;
import java.util.Map;

public class ExprTableEvalStrategyUngroupedTopLevel extends ExprTableEvalStrategyUngroupedBase implements ExprTableAccessEvalStrategy {

    private final Map<String, TableMetadataColumn> items;

    public ExprTableEvalStrategyUngroupedTopLevel(TableAndLockProviderUngrouped provider, Map<String, TableMetadataColumn> items) {
        super(provider);
        this.items = items;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean event = lockTableReadAndGet(context);
        if (event == null) {
            return null;
        }
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(event);
        return ExprTableEvalStrategyUtil.evalMap(event, row, items, eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean event = lockTableReadAndGet(context);
        if (event == null) {
            return null;
        }
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(event);
        return ExprTableEvalStrategyUtil.evalTypable(event, row, items, eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }
}
