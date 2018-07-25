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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.agggrouped;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.agg.service.common.AggregationService;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Iterator for group-by with aggregation.
 */
public class ResultSetProcessorAggregateGroupedIterator implements Iterator<EventBean> {
    private final Iterator<EventBean> sourceIterator;
    private final ResultSetProcessorAggregateGrouped resultSetProcessor;
    private final AggregationService aggregationService;
    private com.espertech.esper.client.EventBean nextResult;
    private final com.espertech.esper.client.EventBean[] eventsPerStream;
    private final ExprEvaluatorContext exprEvaluatorContext;

    /**
     * Ctor.
     *
     * @param sourceIterator       is the parent iterator
     * @param resultSetProcessor   for constructing result rows
     * @param aggregationService   for pointing to the right aggregation row
     * @param exprEvaluatorContext context for expression evalauation
     */
    public ResultSetProcessorAggregateGroupedIterator(Iterator<EventBean> sourceIterator, ResultSetProcessorAggregateGrouped resultSetProcessor, AggregationService aggregationService, ExprEvaluatorContext exprEvaluatorContext) {
        this.sourceIterator = sourceIterator;
        this.resultSetProcessor = resultSetProcessor;
        this.aggregationService = aggregationService;
        eventsPerStream = new com.espertech.esper.client.EventBean[1];
        this.exprEvaluatorContext = exprEvaluatorContext;
    }

    public boolean hasNext() {
        if (nextResult != null) {
            return true;
        }
        findNext();
        if (nextResult != null) {
            return true;
        }
        return false;
    }

    public com.espertech.esper.client.EventBean next() {
        if (nextResult != null) {
            com.espertech.esper.client.EventBean result = nextResult;
            nextResult = null;
            return result;
        }
        findNext();
        if (nextResult != null) {
            com.espertech.esper.client.EventBean result = nextResult;
            nextResult = null;
            return result;
        }
        throw new NoSuchElementException();
    }

    private void findNext() {
        while (sourceIterator.hasNext()) {
            com.espertech.esper.client.EventBean candidate = sourceIterator.next();
            eventsPerStream[0] = candidate;

            Object groupKey = resultSetProcessor.generateGroupKeySingle(eventsPerStream, true);
            aggregationService.setCurrentAccess(groupKey, exprEvaluatorContext.getAgentInstanceId(), null);

            if (resultSetProcessor.hasHavingClause()) {
                boolean pass = resultSetProcessor.evaluateHavingClause(eventsPerStream, true, exprEvaluatorContext);
                if (!pass) {
                    continue;
                }
            }

            nextResult = resultSetProcessor.getSelectExprProcessor().process(eventsPerStream, true, true, exprEvaluatorContext);

            break;
        }
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }
}
