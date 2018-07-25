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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.rowpergrouprollup;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.collection.MultiKey;
import com.espertech.esper.collection.UniformPair;
import com.espertech.esper.epl.agg.service.common.AggregationGroupByRollupLevel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultSetProcessorRowPerGroupRollupOutputLastHelperImpl implements ResultSetProcessorRowPerGroupRollupOutputLastHelper {

    private final ResultSetProcessorRowPerGroupRollup processor;
    private final Map<Object, EventBean[]>[] outputLimitGroupRepsPerLevel;
    private final Map<Object, EventBean>[] groupRepsOutputLastUnordRStream;

    public ResultSetProcessorRowPerGroupRollupOutputLastHelperImpl(ResultSetProcessorRowPerGroupRollup processor, int levelCount) {
        this.processor = processor;

        outputLimitGroupRepsPerLevel = (LinkedHashMap<Object, EventBean[]>[]) new LinkedHashMap[levelCount];
        for (int i = 0; i < levelCount; i++) {
            outputLimitGroupRepsPerLevel[i] = new LinkedHashMap<>();
        }

        if (processor.isSelectRStream()) {
            groupRepsOutputLastUnordRStream = (LinkedHashMap<Object, EventBean>[]) new LinkedHashMap[levelCount];
            for (int i = 0; i < levelCount; i++) {
                groupRepsOutputLastUnordRStream[i] = new LinkedHashMap<>();
            }
        } else {
            groupRepsOutputLastUnordRStream = null;
        }
    }

    public void processView(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        // apply to aggregates
        Object[] groupKeysPerLevel = new Object[processor.getGroupByRollupDesc().getLevels().length];
        com.espertech.esper.client.EventBean[] eventsPerStream;
        if (newData != null) {
            for (com.espertech.esper.client.EventBean aNewData : newData) {
                eventsPerStream = new com.espertech.esper.client.EventBean[]{aNewData};
                Object groupKeyComplete = processor.generateGroupKeySingle(eventsPerStream, true);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;

                    outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, eventsPerStream);
                    if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream[level.getLevelNumber()].containsKey(groupKey)) {
                        processor.generateOutputBatchedMapUnsorted(false, groupKey, level, eventsPerStream, true, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                    }
                }
                processor.getAggregationService().applyEnter(eventsPerStream, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (com.espertech.esper.client.EventBean anOldData : oldData) {
                eventsPerStream = new com.espertech.esper.client.EventBean[]{anOldData};
                Object groupKeyComplete = processor.generateGroupKeySingle(eventsPerStream, false);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;

                    outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, eventsPerStream);
                    if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream[level.getLevelNumber()].containsKey(groupKey)) {
                        processor.generateOutputBatchedMapUnsorted(false, groupKey, level, eventsPerStream, false, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                    }
                }
                processor.getAggregationService().applyLeave(eventsPerStream, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        // apply to aggregates
        Object[] groupKeysPerLevel = new Object[processor.getGroupByRollupDesc().getLevels().length];
        if (newEvents != null) {
            for (MultiKey<com.espertech.esper.client.EventBean> newEvent : newEvents) {
                com.espertech.esper.client.EventBean[] aNewData = newEvent.getArray();
                Object groupKeyComplete = processor.generateGroupKeySingle(aNewData, true);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;

                    outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, aNewData);
                    if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream[level.getLevelNumber()].containsKey(groupKey)) {
                        processor.generateOutputBatchedMapUnsorted(false, groupKey, level, aNewData, true, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                    }
                }
                processor.getAggregationService().applyEnter(aNewData, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
        if (oldEvents != null) {
            for (MultiKey<com.espertech.esper.client.EventBean> oldEvent : oldEvents) {
                com.espertech.esper.client.EventBean[] aOldData = oldEvent.getArray();
                Object groupKeyComplete = processor.generateGroupKeySingle(aOldData, false);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;

                    outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, aOldData);
                    if (processor.isSelectRStream() && !groupRepsOutputLastUnordRStream[level.getLevelNumber()].containsKey(groupKey)) {
                        processor.generateOutputBatchedMapUnsorted(false, groupKey, level, aOldData, false, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                    }
                }
                processor.getAggregationService().applyLeave(aOldData, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputView(boolean isSynthesize) {
        return output(isSynthesize, false);
    }

    public UniformPair<com.espertech.esper.client.EventBean[]> outputJoin(boolean isSynthesize) {
        return output(isSynthesize, true);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<com.espertech.esper.client.EventBean[]> output(boolean isSynthesize, boolean isJoin) {

        List<EventBean> newEvents = new ArrayList<EventBean>(4);
        for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
            Map<Object, EventBean[]> groupGenerators = outputLimitGroupRepsPerLevel[level.getLevelNumber()];
            for (Map.Entry<Object, EventBean[]> entry : groupGenerators.entrySet()) {
                processor.generateOutputBatched(entry.getKey(), level, entry.getValue(), true, isSynthesize, newEvents, null);
            }
        }
        com.espertech.esper.client.EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new com.espertech.esper.client.EventBean[newEvents.size()]);
        for (Map<Object, EventBean[]> outputLimitGroupRepsPerLevelItem : outputLimitGroupRepsPerLevel) {
            outputLimitGroupRepsPerLevelItem.clear();
        }

        com.espertech.esper.client.EventBean[] oldEventsArr = null;
        if (groupRepsOutputLastUnordRStream != null) {
            List<EventBean> oldEventList = new ArrayList<EventBean>(4);
            for (Map<Object, EventBean> entry : groupRepsOutputLastUnordRStream) {
                oldEventList.addAll(entry.values());
            }
            if (!oldEventList.isEmpty()) {
                oldEventsArr = oldEventList.toArray(new com.espertech.esper.client.EventBean[oldEventList.size()]);
                for (Map<Object, EventBean> groupRepsOutputLastUnordRStreamItem : groupRepsOutputLastUnordRStream) {
                    groupRepsOutputLastUnordRStreamItem.clear();
                }
            }
        }

        if (newEventsArr == null && oldEventsArr == null) {
            return null;
        }
        return new UniformPair<com.espertech.esper.client.EventBean[]>(newEventsArr, oldEventsArr);
    }
}
