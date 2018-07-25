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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.core.resultset.core.ResultSetProcessorUtil;
import com.espertech.esper.event.EventBeanUtility;

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

    public void processView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream()) {
            com.espertech.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, false);
            EventBeanUtility.addToCollection(events, eventsOld);
        }

        com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[1];
        ResultSetProcessorUtil.applyAggViewResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newData, oldData, eventsPerStream);

        com.espertech.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, false);
        EventBeanUtility.addToCollection(events, eventsNew);
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream()) {
            com.espertech.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, true);
            EventBeanUtility.addToCollection(events, eventsOld);
        }

        ResultSetProcessorUtil.applyAggJoinResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newEvents, oldEvents);

        com.espertech.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, true);
        EventBeanUtility.addToCollection(events, eventsNew);
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputView(boolean isGenerateSynthetic) {
        return output(isGenerateSynthetic, false);
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputJoin(boolean isGenerateSynthetic) {
        return output(isGenerateSynthetic, true);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<com.espertech.esper.client.EventBean[]> output(boolean isGenerateSynthetic, boolean isJoin) {
        com.espertech.esper.client.EventBean[] oldEvents = EventBeanUtility.toArrayNullIfEmpty(eventsOld);
        com.espertech.esper.client.EventBean[] newEvents = EventBeanUtility.toArrayNullIfEmpty(eventsNew);

        if (newEvents == null) {
            newEvents = processor.getSelectListEventsAsArray(true, isGenerateSynthetic, isJoin);
        }
        if (oldEvents == null && processor.isSelectRStream()) {
            oldEvents = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, isJoin);
        }

        UniformPair<com.espertech.esper.client.EventBean[]> result = null;
        if (oldEvents != null || newEvents != null) {
            result = new UniformPair<>(newEvents, oldEvents);
        }

        eventsOld.clear();
        eventsNew.clear();
        return result;
    }
}
