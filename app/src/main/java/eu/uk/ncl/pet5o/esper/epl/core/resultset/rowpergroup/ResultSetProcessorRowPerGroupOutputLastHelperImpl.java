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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultSetProcessorRowPerGroupOutputLastHelperImpl implements ResultSetProcessorRowPerGroupOutputLastHelper {

    protected final ResultSetProcessorRowPerGroup processor;
    private final Map<Object, EventBean[]> groupReps = new LinkedHashMap<>();
    private final Map<Object, EventBean> groupRepsOutputLastUnordRStream = new LinkedHashMap<>();

    public ResultSetProcessorRowPerGroupOutputLastHelperImpl(ResultSetProcessorRowPerGroup processor) {
        this.processor = processor;
    }

    public void processView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        if (newData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean aNewData : newData) {
                eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[]{aNewData};
                Object mk = processor.generateGroupKeySingle(eventsPerStream, true);

                // if this is a newly encountered group, generate the remove stream event
                if (groupReps.put(mk, eventsPerStream) == null) {
                    if (processor.isSelectRStream()) {
                        eu.uk.ncl.pet5o.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(false, mk, eventsPerStream, true, isGenerateSynthetic);
                        if (event != null) {
                            groupRepsOutputLastUnordRStream.put(mk, event);
                        }
                    }
                }
                processor.getAggregationService().applyEnter(eventsPerStream, mk, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean anOldData : oldData) {
                eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[]{anOldData};
                Object mk = processor.generateGroupKeySingle(eventsPerStream, true);

                if (groupReps.put(mk, eventsPerStream) == null) {
                    if (processor.isSelectRStream()) {
                        eu.uk.ncl.pet5o.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(false, mk, eventsPerStream, false, isGenerateSynthetic);
                        if (event != null) {
                            groupRepsOutputLastUnordRStream.put(mk, event);
                        }
                    }
                }

                processor.getAggregationService().applyLeave(eventsPerStream, mk, processor.getAgentInstanceContext());
            }
        }
    }

    public void processJoin(Set<MultiKey<EventBean>> newData, Set<MultiKey<EventBean>> oldData, boolean isGenerateSynthetic) {
        if (newData != null) {
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> aNewData : newData) {
                Object mk = processor.generateGroupKeySingle(aNewData.getArray(), true);
                if (groupReps.put(mk, aNewData.getArray()) == null) {
                    if (processor.isSelectRStream()) {
                        eu.uk.ncl.pet5o.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(true, mk, aNewData.getArray(), false, isGenerateSynthetic);
                        if (event != null) {
                            groupRepsOutputLastUnordRStream.put(mk, event);
                        }
                    }
                }
                processor.getAggregationService().applyEnter(aNewData.getArray(), mk, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> anOldData : oldData) {
                Object mk = processor.generateGroupKeySingle(anOldData.getArray(), false);
                if (groupReps.put(mk, anOldData.getArray()) == null) {
                    if (processor.isSelectRStream()) {
                        eu.uk.ncl.pet5o.esper.client.EventBean event = processor.generateOutputBatchedNoSortWMap(true, mk, anOldData.getArray(), false, isGenerateSynthetic);
                        if (event != null) {
                            groupRepsOutputLastUnordRStream.put(mk, event);
                        }
                    }
                }
                processor.getAggregationService().applyLeave(anOldData.getArray(), mk, processor.getAgentInstanceContext());
            }
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputView(boolean isSynthesize) {
        return output(isSynthesize, false);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputJoin(boolean isSynthesize) {
        return output(isSynthesize, true);
    }

    public void destroy() {
        // no action required
    }

    public void remove(Object key) {
        groupReps.remove(key);
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> output(boolean isSynthesize, boolean join) {
        List<EventBean> newEvents = new ArrayList<>(4);
        processor.generateOutputBatchedArrFromIterator(join, groupReps.entrySet().iterator(), true, isSynthesize, newEvents, null);
        groupReps.clear();
        eu.uk.ncl.pet5o.esper.client.EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[newEvents.size()]);

        eu.uk.ncl.pet5o.esper.client.EventBean[] oldEventsArr = null;
        if (!groupRepsOutputLastUnordRStream.isEmpty()) {
            Collection<EventBean> oldEvents = groupRepsOutputLastUnordRStream.values();
            oldEventsArr = oldEvents.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[oldEvents.size()]);
        }

        if (newEventsArr == null && oldEventsArr == null) {
            return null;
        }
        return new UniformPair<>(newEventsArr, oldEventsArr);
    }
}
