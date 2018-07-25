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

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

/**
 * Base interface for providing access-aggregations, i.e. aggregations that mirror a data window
 * but group by the group-by clause and that do not mirror the data windows sorting policy.
 */
public interface AggregationState {
    /**
     * Enter an event.
     *
     * @param eventsPerStream      all events in all streams, typically implementations pick the relevant stream's events to add
     * @param exprEvaluatorContext expression eval context
     */
    void applyEnter(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext);

    /**
     * Remove an event.
     *
     * @param eventsPerStream      all events in all streams, typically implementations pick the relevant stream's events to remove
     * @param exprEvaluatorContext expression eval context
     */
    void applyLeave(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext);

    /**
     * Clear all events in the group.
     */
    public void clear();
}
