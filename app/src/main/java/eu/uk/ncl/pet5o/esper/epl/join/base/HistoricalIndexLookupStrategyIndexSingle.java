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
package eu.uk.ncl.pet5o.esper.epl.join.base;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraphValueEntryHashKeyed;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTableSingle;

import java.util.Iterator;
import java.util.Set;

/**
 * Index lookup strategy into a poll-based cache result.
 */
public class HistoricalIndexLookupStrategyIndexSingle implements HistoricalIndexLookupStrategy {
    private final EventBean[] eventsPerStream;
    private final ExprEvaluator evaluator;
    private final int lookupStream;

    public HistoricalIndexLookupStrategyIndexSingle(int lookupStream, QueryGraphValueEntryHashKeyed hashKey) {
        this.eventsPerStream = new EventBean[lookupStream + 1];
        this.evaluator = hashKey.getKeyExpr().getForge().getExprEvaluator();
        this.lookupStream = lookupStream;
    }

    public Iterator<EventBean> lookup(EventBean lookupEvent, EventTable[] indexTable, ExprEvaluatorContext exprEvaluatorContext) {
        // The table may not be indexed as the cache may not actively cache, in which case indexing doesn't makes sense
        if (indexTable[0] instanceof PropertyIndexedEventTableSingle) {
            PropertyIndexedEventTableSingle index = (PropertyIndexedEventTableSingle) indexTable[0];
            eventsPerStream[lookupStream] = lookupEvent;
            Object key = evaluator.evaluate(eventsPerStream, true, exprEvaluatorContext);

            Set<EventBean> events = index.lookup(key);
            if (events != null) {
                return events.iterator();
            }
            return null;
        }

        return indexTable[0].iterator();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluator " + evaluator.getClass().getSimpleName();
    }
}
