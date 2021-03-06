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
package eu.uk.ncl.pet5o.esper.epl.join.exec.base;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.join.rep.Cursor;
import eu.uk.ncl.pet5o.esper.epl.join.table.UnindexedEventTable;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyDesc;
import eu.uk.ncl.pet5o.esper.epl.lookup.LookupStrategyType;
import eu.uk.ncl.pet5o.esper.metrics.instrumentation.InstrumentationHelper;

import java.util.Set;

/**
 * Lookup on an unindexed table returning the full table as matching events.
 */
public class FullTableScanLookupStrategy implements JoinExecTableLookupStrategy {
    private UnindexedEventTable eventIndex;

    /**
     * Ctor.
     *
     * @param eventIndex - table to use
     */
    public FullTableScanLookupStrategy(UnindexedEventTable eventIndex) {
        this.eventIndex = eventIndex;
    }

    public Set<EventBean> lookup(EventBean theEvent, Cursor cursor, ExprEvaluatorContext exprEvaluatorContext) {
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().qIndexJoinLookup(this, eventIndex);
        }
        Set<EventBean> result = eventIndex.getEventSet();
        if (result.isEmpty()) {
            if (InstrumentationHelper.ENABLED) {
                InstrumentationHelper.get().aIndexJoinLookup(null, null);
            }
            return null;
        }
        if (InstrumentationHelper.ENABLED) {
            InstrumentationHelper.get().aIndexJoinLookup(result, null);
        }
        return result;
    }

    /**
     * Returns the associated table.
     *
     * @return table for lookup.
     */
    public UnindexedEventTable getEventIndex() {
        return eventIndex;
    }

    public LookupStrategyDesc getStrategyDesc() {
        return new LookupStrategyDesc(LookupStrategyType.FULLTABLESCAN, null);
    }
}
