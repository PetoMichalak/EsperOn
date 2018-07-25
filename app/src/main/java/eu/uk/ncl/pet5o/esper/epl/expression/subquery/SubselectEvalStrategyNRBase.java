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
package eu.uk.ncl.pet5o.esper.epl.expression.subquery;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.Collection;

public abstract class SubselectEvalStrategyNRBase implements SubselectEvalStrategyNR {
    protected final ExprEvaluator valueEval;
    protected final ExprEvaluator selectEval;
    private final boolean resultWhenNoMatchingEvents;

    protected abstract Object evaluateInternal(Object leftResult, eu.uk.ncl.pet5o.esper.client.EventBean[] events, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService);

    public SubselectEvalStrategyNRBase(ExprEvaluator valueEval, ExprEvaluator selectEval, boolean resultWhenNoMatchingEvents) {
        this.valueEval = valueEval;
        this.selectEval = selectEval;
        this.resultWhenNoMatchingEvents = resultWhenNoMatchingEvents;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, boolean isNewData, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext, AggregationService aggregationService) {
        if (matchingEvents == null || matchingEvents.size() == 0) {
            return resultWhenNoMatchingEvents;
        }

        Object leftResult = valueEval.evaluate(eventsPerStream, isNewData, exprEvaluatorContext);
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = EventBeanUtility.allocatePerStreamShift(eventsPerStream);
        return evaluateInternal(leftResult, events, isNewData, matchingEvents, exprEvaluatorContext, aggregationService);
    }

}
