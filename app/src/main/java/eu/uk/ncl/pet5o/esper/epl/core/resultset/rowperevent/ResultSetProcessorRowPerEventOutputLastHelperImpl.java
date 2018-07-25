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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowperevent;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;

import java.util.Set;

public class ResultSetProcessorRowPerEventOutputLastHelperImpl implements ResultSetProcessorRowPerEventOutputLastHelper {
    private final ResultSetProcessorRowPerEvent processor;

    private com.espertech.esper.client.EventBean lastEventIStreamForOutputLast;
    private com.espertech.esper.client.EventBean lastEventRStreamForOutputLast;

    public ResultSetProcessorRowPerEventOutputLastHelperImpl(ResultSetProcessorRowPerEvent processor) {
        this.processor = processor;
    }

    public void processView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        UniformPair<com.espertech.esper.client.EventBean[]> pair = processor.processViewResult(newData, oldData, isGenerateSynthetic);
        apply(pair);
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        UniformPair<com.espertech.esper.client.EventBean[]> pair = processor.processJoinResult(newEvents, oldEvents, isGenerateSynthetic);
        apply(pair);
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> output() {
        UniformPair<com.espertech.esper.client.EventBean[]> newOldEvents = null;
        if (lastEventIStreamForOutputLast != null) {
            com.espertech.esper.client.EventBean[] istream = new com.espertech.esper.client.EventBean[]{lastEventIStreamForOutputLast};
            newOldEvents = new UniformPair<>(istream, null);
        }
        if (lastEventRStreamForOutputLast != null) {
            com.espertech.esper.client.EventBean[] rstream = new com.espertech.esper.client.EventBean[]{lastEventRStreamForOutputLast};
            if (newOldEvents == null) {
                newOldEvents = new UniformPair<>(null, rstream);
            } else {
                newOldEvents.setSecond(rstream);
            }
        }

        lastEventIStreamForOutputLast = null;
        lastEventRStreamForOutputLast = null;
        return newOldEvents;
    }

    public void destroy() {
        // no action required
    }

    private void apply(UniformPair<com.espertech.esper.client.EventBean[]> pair) {
        if (pair == null) {
            return;
        }
        if (pair.getFirst() != null && pair.getFirst().length > 0) {
            lastEventIStreamForOutputLast = pair.getFirst()[pair.getFirst().length - 1];
        }
        if (pair.getSecond() != null && pair.getSecond().length > 0) {
            lastEventRStreamForOutputLast = pair.getSecond()[pair.getSecond().length - 1];
        }
    }
}
