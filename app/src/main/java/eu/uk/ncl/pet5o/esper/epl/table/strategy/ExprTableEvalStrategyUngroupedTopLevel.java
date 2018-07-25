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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessEvalStrategy;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadataColumn;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.util.Collection;
import java.util.Map;

public class ExprTableEvalStrategyUngroupedTopLevel extends ExprTableEvalStrategyUngroupedBase implements ExprTableAccessEvalStrategy {

    private final Map<String, TableMetadataColumn> items;

    public ExprTableEvalStrategyUngroupedTopLevel(TableAndLockProviderUngrouped provider, Map<String, TableMetadataColumn> items) {
        super(provider);
        this.items = items;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean event = lockTableReadAndGet(context);
        if (event == null) {
            return null;
        }
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(event);
        return ExprTableEvalStrategyUtil.evalMap(event, row, items, eventsPerStream, isNewData, context);
    }

    public Object[] evaluateTypableSingle(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        ObjectArrayBackedEventBean event = lockTableReadAndGet(context);
        if (event == null) {
            return null;
        }
        AggregationRowPair row = ExprTableEvalStrategyUtil.getRow(event);
        return ExprTableEvalStrategyUtil.evalTypable(event, row, items, eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetROCollectionScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return null;
    }
}
