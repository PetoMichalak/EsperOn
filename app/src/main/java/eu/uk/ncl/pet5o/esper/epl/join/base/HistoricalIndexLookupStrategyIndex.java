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
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraphValueEntryHashKeyed;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;
import eu.uk.ncl.pet5o.esper.epl.join.table.PropertyIndexedEventTable;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Index lookup strategy into a poll-based cache result.
 */
public class HistoricalIndexLookupStrategyIndex implements HistoricalIndexLookupStrategy {
    private final EventBean[] eventsPerStream;
    private final int lookupStream;
    private final ExprEvaluator[] evaluators;

    public HistoricalIndexLookupStrategyIndex(EventType eventType, int lookupStream, List<QueryGraphValueEntryHashKeyed> hashKeys) {
        this.evaluators = new ExprEvaluator[hashKeys.size()];
        for (int i = 0; i < hashKeys.size(); i++) {
            evaluators[i] = hashKeys.get(i).getKeyExpr().getForge().getExprEvaluator();
        }
        this.eventsPerStream = new EventBean[lookupStream + 1];
        this.lookupStream = lookupStream;
    }

    public Iterator<EventBean> lookup(EventBean lookupEvent, EventTable[] indexTable, ExprEvaluatorContext exprEvaluatorContext) {
        // The table may not be indexed as the cache may not actively cache, in which case indexing doesn't makes sense
        if (indexTable[0] instanceof PropertyIndexedEventTable) {
            PropertyIndexedEventTable index = (PropertyIndexedEventTable) indexTable[0];
            Object[] keys = getKeys(lookupEvent, exprEvaluatorContext);

            Set<EventBean> events = index.lookup(keys);
            if (events != null) {
                return events.iterator();
            }
            return null;
        }

        return indexTable[0].iterator();
    }

    private Object[] getKeys(EventBean theEvent, ExprEvaluatorContext exprEvaluatorContext) {
        eventsPerStream[lookupStream] = theEvent;
        Object[] keys = new Object[evaluators.length];
        for (int i = 0; i < evaluators.length; i++) {
            keys[i] = evaluators[i].evaluate(eventsPerStream, true, exprEvaluatorContext);
        }
        return keys;
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName() + " evaluators " + ExprNodeUtilityCore.printEvaluators(evaluators);
    }
}
