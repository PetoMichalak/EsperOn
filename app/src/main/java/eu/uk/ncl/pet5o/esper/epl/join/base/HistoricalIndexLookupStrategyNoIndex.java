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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.table.EventTable;

import java.util.Iterator;

/**
 * Full table scan strategy for a poll-based cache result.
 */
public class HistoricalIndexLookupStrategyNoIndex implements HistoricalIndexLookupStrategy {
    public HistoricalIndexLookupStrategyNoIndex() {
    }

    public Iterator<EventBean> lookup(EventBean lookupEvent, EventTable[] index, ExprEvaluatorContext context) {
        return index[0].iterator();
    }

    public String toQueryPlan() {
        return this.getClass().getSimpleName();
    }
}
