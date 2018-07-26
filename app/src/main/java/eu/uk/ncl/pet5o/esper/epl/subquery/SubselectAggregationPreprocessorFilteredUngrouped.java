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
package eu.uk.ncl.pet5o.esper.epl.subquery;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Collection;

public class SubselectAggregationPreprocessorFilteredUngrouped extends SubselectAggregationPreprocessorBase {

    public SubselectAggregationPreprocessorFilteredUngrouped(AggregationService aggregationService, ExprEvaluator filterEval, ExprEvaluator[] groupKeys) {
        super(aggregationService, filterEval, groupKeys);
    }

    public void evaluate(EventBean[] eventsPerStream, Collection<EventBean> matchingEvents, ExprEvaluatorContext exprEvaluatorContext) {

        aggregationService.clearResults(exprEvaluatorContext);
        if (matchingEvents == null) {
            return;
        }
        EventBean[] events = new EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, events, 1, eventsPerStream.length);

        for (EventBean subselectEvent : matchingEvents) {
            events[0] = subselectEvent;
            Boolean pass = (Boolean) filterEval.evaluate(events, true, exprEvaluatorContext);
            if ((pass != null) && pass) {
                aggregationService.applyEnter(events, null, exprEvaluatorContext);
            }
        }
    }
}
