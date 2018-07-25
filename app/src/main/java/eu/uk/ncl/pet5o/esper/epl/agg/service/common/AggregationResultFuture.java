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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

/**
 * Interface for use by aggregate expression nodes representing aggregate functions such as 'sum' or 'avg' to use
 * to obtain the current value for the function at time of expression evaluation.
 */
public interface AggregationResultFuture {
    /**
     * Returns current aggregation state, for use by expression node representing an aggregation function.
     *
     * @param column               is assigned to the aggregation expression node and passed as an column (index) into a row
     * @param agentInstanceId      the context partition id
     * @param eventsPerStream      events per stream
     * @param isNewData            new vs removed indicator
     * @param exprEvaluatorContext context
     * @return current aggragation state
     */
    public Object getValue(int column, int agentInstanceId, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext exprEvaluatorContext);

    public Collection<EventBean> getCollectionOfEvents(int column, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);

    public com.espertech.esper.client.EventBean getEventBean(int column, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);

    public Object getGroupKey(int agentInstanceId);

    public Collection<Object> getGroupKeys(ExprEvaluatorContext exprEvaluatorContext);

    public Collection<Object> getCollectionScalar(int column, com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context);
}
