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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowforall;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import eu.uk.ncl.pet5o.esper.event.EventBeanUtility;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

public class ResultSetProcessorRowForAllOutputAllHelperImpl implements ResultSetProcessorRowForAllOutputAllHelper {
    private final ResultSetProcessorRowForAll processor;
    private final Deque<EventBean> eventsOld = new ArrayDeque<>(2);
    private final Deque<EventBean> eventsNew = new ArrayDeque<>(2);

    public ResultSetProcessorRowForAllOutputAllHelperImpl(ResultSetProcessorRowForAll processor) {
        this.processor = processor;
    }

    public void processView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream()) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, false);
            EventBeanUtility.addToCollection(events, eventsOld);
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        ResultSetProcessorUtil.applyAggViewResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newData, oldData, eventsPerStream);

        eu.uk.ncl.pet5o.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, false);
        EventBeanUtility.addToCollection(events, eventsNew);
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream()) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, true);
            EventBeanUtility.addToCollection(events, eventsOld);
        }

        ResultSetProcessorUtil.applyAggJoinResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newEvents, oldEvents);

        eu.uk.ncl.pet5o.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, true);
        EventBeanUtility.addToCollection(events, eventsNew);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputView(boolean isGenerateSynthetic) {
        return output(isGenerateSynthetic, false);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputJoin(boolean isGenerateSynthetic) {
        return output(isGenerateSynthetic, true);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> output(boolean isGenerateSynthetic, boolean isJoin) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] oldEvents = EventBeanUtility.toArrayNullIfEmpty(eventsOld);
        eu.uk.ncl.pet5o.esper.client.EventBean[] newEvents = EventBeanUtility.toArrayNullIfEmpty(eventsNew);

        if (newEvents == null) {
            newEvents = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, isJoin);
        }
        if (oldEvents == null && processor.isSelectRStream()) {
            oldEvents = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, isJoin);
        }

        UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result = null;
        if (oldEvents != null || newEvents != null) {
            result = new UniformPair<>(newEvents, oldEvents);
        }

        eventsOld.clear();
        eventsNew.clear();
        return result;
    }
}
