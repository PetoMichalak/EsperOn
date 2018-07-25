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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergroup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultSetProcessorRowPerGroupOutputAllHelperImpl implements ResultSetProcessorRowPerGroupOutputAllHelper {

    protected final ResultSetProcessorRowPerGroup processor;

    private final Map<Object, EventBean[]> groupReps = new LinkedHashMap<>();
    private final Map<Object, EventBean> groupRepsOutputLastUnordRStream = new LinkedHashMap<>();
    private boolean first;

    public ResultSetProcessorRowPerGroupOutputAllHelperImpl(ResultSetProcessorRowPerGroup processor) {
        this.processor = processor;
    }

    public void processView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        generateRemoveStreamJustOnce(isGenerateSynthetic, false);

        if (newData != null) {
            for (com.espertech.esper.client.EventBean aNewData : newData) {
                com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[]{aNewData};
                Object mk = processor.generateGroupKeySingle(eventsPerStream, true);
                groupReps.put(mk, eventsPerStream);

                if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream.containsKey(mk)) {
                    com.espertech.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(false, mk, eventsPerStream, true, isGenerateSynthetic);
                    if (event != null) {
                        groupRepsOutputLastUnordRStream.put(mk, event);
                    }
                }
                processor.getAggregationService().applyEnter(eventsPerStream, mk, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (com.espertech.esper.client.EventBean anOldData : oldData) {
                com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[]{anOldData};
                Object mk = processor.generateGroupKeySingle(eventsPerStream, true);

                if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream.containsKey(mk)) {
                    com.espertech.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(false, mk, eventsPerStream, false, isGenerateSynthetic);
                    if (event != null) {
                        groupRepsOutputLastUnordRStream.put(mk, event);
                    }
                }
                processor.getAggregationService().applyLeave(eventsPerStream, mk, processor.getAgentInstanceContext());
            }
        }
    }

    public void processJoin(Set<MultiKey<EventBean>> newData, Set<MultiKey<EventBean>> oldData, boolean isGenerateSynthetic) {
        generateRemoveStreamJustOnce(isGenerateSynthetic, true);

        if (newData != null) {
            for (MultiKey<com.espertech.esper.client.EventBean> aNewData : newData) {
                Object mk = processor.generateGroupKeySingle(aNewData.getArray(), true);
                groupReps.put(mk, aNewData.getArray());

                if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream.containsKey(mk)) {
                    com.espertech.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(true, mk, aNewData.getArray(), true, isGenerateSynthetic);
                    if (event != null) {
                        groupRepsOutputLastUnordRStream.put(mk, event);
                    }
                }
                processor.getAggregationService().applyEnter(aNewData.getArray(), mk, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (MultiKey<com.espertech.esper.client.EventBean> anOldData : oldData) {
                Object mk = processor.generateGroupKeySingle(anOldData.getArray(), false);
                if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream.containsKey(mk)) {
                    com.espertech.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(true, mk, anOldData.getArray(), false, isGenerateSynthetic);
                    if (event != null) {
                        groupRepsOutputLastUnordRStream.put(mk, event);
                    }
                }
                processor.getAggregationService().applyLeave(anOldData.getArray(), mk, processor.getAgentInstanceContext());
            }
        }
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputView(boolean isSynthesize) {
        generateRemoveStreamJustOnce(isSynthesize, false);
        return output(isSynthesize, false);
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputJoin(boolean isSynthesize) {
        generateRemoveStreamJustOnce(isSynthesize, true);
        return output(isSynthesize, true);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<com.espertech.esper.client.EventBean[]> output(boolean isSynthesize, boolean join) {
        // generate latest new-events from group representatives
        List<EventBean> newEvents = new ArrayList<>(4);
        processor.generateOutputBatchedArrFromIterator(join, groupReps.entrySet().iterator(), true, isSynthesize, newEvents, null);
        com.espertech.esper.client.EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new com.espertech.esper.client.EventBean[newEvents.size()]);

        // use old-events as retained, if any
        com.espertech.esper.client.EventBean[] oldEventsArr = null;
        if (!groupRepsOutputLastUnordRStream.isEmpty()) {
            Collection<EventBean> oldEvents = groupRepsOutputLastUnordRStream.values();
            oldEventsArr = oldEvents.toArray(new com.espertech.esper.client.EventBean[oldEvents.size()]);
            groupRepsOutputLastUnordRStream.clear();
        }
        first = true;

        if (newEventsArr == null && oldEventsArr == null) {
            return null;
        }
        return new UniformPair<>(newEventsArr, oldEventsArr);
    }

    private void generateRemoveStreamJustOnce(boolean isSynthesize, boolean join) {
        if (first && processor.isSelectRStream()) {
            for (Map.Entry<Object, EventBean[]> groupRep : groupReps.entrySet()) {
                Object mk = processor.generateGroupKeySingle(groupRep.getValue(), false);
                com.espertech.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(join, mk, groupRep.getValue(), false, isSynthesize);
                if (event != null) {
                    groupRepsOutputLastUnordRStream.put(mk, event);
                }
            }
        }
        first = false;
    }
}
