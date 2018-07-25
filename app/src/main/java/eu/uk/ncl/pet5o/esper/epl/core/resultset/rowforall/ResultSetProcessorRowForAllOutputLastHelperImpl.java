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

import java.util.Set;

public class ResultSetProcessorRowForAllOutputLastHelperImpl implements ResultSetProcessorRowForAllOutputLastHelper {
    private final ResultSetProcessorRowForAll processor;
    private eu.uk.ncl.pet5o.esper.client.EventBean[] lastEventRStreamForOutputLast;

    public ResultSetProcessorRowForAllOutputLastHelperImpl(ResultSetProcessorRowForAll processor) {
        this.processor = processor;
    }

    public void processView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream() && lastEventRStreamForOutputLast == null) {
            lastEventRStreamForOutputLast = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, false);
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
        ResultSetProcessorUtil.applyAggViewResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newData, oldData, eventsPerStream);
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        if (processor.isSelectRStream() && lastEventRStreamForOutputLast == null) {
            lastEventRStreamForOutputLast = processor.getSelectListEventsAsArray(false, isGenerateSynthetic, true);
        }

        ResultSetProcessorUtil.applyAggJoinResult(processor.getAggregationService(), processor.getExprEvaluatorContext(), newEvents, oldEvents);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputView(boolean isSynthesize) {
        return continueOutputLimitedLastNonBuffered(isSynthesize);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputJoin(boolean isSynthesize) {
        return continueOutputLimitedLastNonBuffered(isSynthesize);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> continueOutputLimitedLastNonBuffered(boolean isSynthesize) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] events = processor.getSelectListEventsAsArray(true, isSynthesize, false);
        UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result = new UniformPair<>(events, null);

        if (processor.isSelectRStream() && lastEventRStreamForOutputLast == null) {
            lastEventRStreamForOutputLast = processor.getSelectListEventsAsArray(false, isSynthesize, false);
        }
        if (lastEventRStreamForOutputLast != null) {
            result.setSecond(lastEventRStreamForOutputLast);
            lastEventRStreamForOutputLast = null;
        }

        return result;
    }
}
