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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationGroupByRollupLevel;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ResultSetProcessorRowPerGroupRollupOutputAllHelperImpl implements ResultSetProcessorRowPerGroupRollupOutputAllHelper {

    private final ResultSetProcessorRowPerGroupRollup processor;
    private final Map<Object, EventBean[]>[] outputLimitGroupRepsPerLevel;
    private final Map<Object, EventBean>[] groupRepsOutputLastUnordRStream;
    private boolean first;

    public ResultSetProcessorRowPerGroupRollupOutputAllHelperImpl(ResultSetProcessorRowPerGroupRollup processor, int levelCount) {
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

    public void processView(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData, boolean isGenerateSynthetic) {
        generateRemoveStreamJustOnce(isGenerateSynthetic, false);

        // apply to aggregates
        Object[] groupKeysPerLevel = new Object[processor.getGroupByRollupDesc().getLevels().length];
        eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream;
        if (newData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean aNewData : newData) {
                eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[]{aNewData};
                Object groupKeyComplete = processor.generateGroupKeySingle(eventsPerStream, true);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;
                    if (outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, eventsPerStream) == null) {
                        if (processor.isSelectRStream()) {
                            processor.generateOutputBatchedMapUnsorted(false, groupKey, level, eventsPerStream, true, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                        }
                    }
                }
                processor.getAggregationService().applyEnter(eventsPerStream, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
        if (oldData != null) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean anOldData : oldData) {
                eventsPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[]{anOldData};
                Object groupKeyComplete = processor.generateGroupKeySingle(eventsPerStream, false);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;
                    if (outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, eventsPerStream) == null) {
                        if (processor.isSelectRStream()) {
                            processor.generateOutputBatchedMapUnsorted(true, groupKey, level, eventsPerStream, false, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                        }
                    }
                }
                processor.getAggregationService().applyLeave(eventsPerStream, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
    }

    public void processJoin(Set<MultiKey<EventBean>> newEvents, Set<MultiKey<EventBean>> oldEvents, boolean isGenerateSynthetic) {
        generateRemoveStreamJustOnce(isGenerateSynthetic, true);

        // apply to aggregates
        Object[] groupKeysPerLevel = new Object[processor.getGroupByRollupDesc().getLevels().length];
        if (newEvents != null) {
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> newEvent : newEvents) {
                eu.uk.ncl.pet5o.esper.client.EventBean[] aNewData = newEvent.getArray();
                Object groupKeyComplete = processor.generateGroupKeySingle(aNewData, true);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;
                    if (outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, aNewData) == null) {
                        if (processor.isSelectRStream()) {
                            processor.generateOutputBatchedMapUnsorted(false, groupKey, level, aNewData, true, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                        }
                    }
                }
                processor.getAggregationService().applyEnter(aNewData, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
        if (oldEvents != null) {
            for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> oldEvent : oldEvents) {
                eu.uk.ncl.pet5o.esper.client.EventBean[] aOldData = oldEvent.getArray();
                Object groupKeyComplete = processor.generateGroupKeySingle(aOldData, false);
                for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                    Object groupKey = level.computeSubkey(groupKeyComplete);
                    groupKeysPerLevel[level.getLevelNumber()] = groupKey;
                    if (outputLimitGroupRepsPerLevel[level.getLevelNumber()].put(groupKey, aOldData) == null) {
                        if (processor.isSelectRStream()) {
                            processor.generateOutputBatchedMapUnsorted(true, groupKey, level, aOldData, false, isGenerateSynthetic, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                        }
                    }
                }
                processor.getAggregationService().applyLeave(aOldData, groupKeysPerLevel, processor.getAgentInstanceContext());
            }
        }
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputView(boolean isSynthesize) {
        generateRemoveStreamJustOnce(isSynthesize, false);
        return output(isSynthesize, false);
    }

    public UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> outputJoin(boolean isSynthesize) {
        generateRemoveStreamJustOnce(isSynthesize, true);
        return output(isSynthesize, true);
    }

    public void destroy() {
        // no action required
    }

    private UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> output(boolean isSynthesize, boolean isJoin) {

        List<EventBean> newEvents = new ArrayList<>(4);
        for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
            Map<Object, EventBean[]> groupGenerators = outputLimitGroupRepsPerLevel[level.getLevelNumber()];
            for (Map.Entry<Object, EventBean[]> entry : groupGenerators.entrySet()) {
                processor.generateOutputBatched(entry.getKey(), level, entry.getValue(), true, isSynthesize, newEvents, null);
            }
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] newEventsArr = (newEvents.isEmpty()) ? null : newEvents.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[newEvents.size()]);

        eu.uk.ncl.pet5o.esper.client.EventBean[] oldEventsArr = null;
        if (processor.isSelectRStream()) {
            List<EventBean> oldEventList = new ArrayList<>(4);
            for (Map<Object, EventBean> entry : groupRepsOutputLastUnordRStream) {
                oldEventList.addAll(entry.values());
                entry.clear();
            }
            if (!oldEventList.isEmpty()) {
                oldEventsArr = oldEventList.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[oldEventList.size()]);
            }
        }

        first = true;

        if (newEventsArr == null && oldEventsArr == null) {
            return null;
        }
        return new UniformPair<>(newEventsArr, oldEventsArr);
    }

    private void generateRemoveStreamJustOnce(boolean isSynthesize, boolean join) {
        if (first && processor.isSelectRStream()) {
            for (AggregationGroupByRollupLevel level : processor.getGroupByRollupDesc().getLevels()) {
                for (Map.Entry<Object, EventBean[]> groupRep : outputLimitGroupRepsPerLevel[level.getLevelNumber()].entrySet()) {
                    Object groupKeyPartial = processor.generateGroupKeySingle(groupRep.getValue(), false);
                    Object groupKey = level.computeSubkey(groupKeyPartial);
                    processor.generateOutputBatchedMapUnsorted(join, groupKey, level, groupRep.getValue(), false, isSynthesize, groupRepsOutputLastUnordRStream[level.getLevelNumber()]);
                }
            }
        }
        first = false;
    }
}
