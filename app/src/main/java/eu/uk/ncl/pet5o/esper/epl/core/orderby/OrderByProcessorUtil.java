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
package eu.uk.ncl.pet5o.esper.epl.core.orderby;

import eu.uk.ncl.pet5o.esper.client.EventBean;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class OrderByProcessorUtil {

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param outgoingEvents outgoing
     * @param sortValuesMultiKeys keys
     * @param comparator comparator
     * @return sorted
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] sortGivenOutgoingAndSortKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, List<Object> sortValuesMultiKeys, Comparator<Object> comparator) {
        // Map the sort values to the corresponding outgoing events
        Map<Object, List<EventBean>> sortToOutgoing = new HashMap<>();
        int countOne = 0;
        for (Object sortValues : sortValuesMultiKeys) {
            List<EventBean> list = sortToOutgoing.get(sortValues);
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(outgoingEvents[countOne++]);
            sortToOutgoing.put(sortValues, list);
        }

        // Sort the sort values
        Collections.sort(sortValuesMultiKeys, comparator);

        // Sort the outgoing events in the same order
        Set<Object> sortSet = new LinkedHashSet<>(sortValuesMultiKeys);
        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[outgoingEvents.length];
        int countTwo = 0;
        for (Object sortValues : sortSet) {
            Collection<EventBean> output = sortToOutgoing.get(sortValues);
            for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : output) {
                result[countTwo++] = theEvent;
            }
        }

        return result;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param outgoingEvents events
     * @param orderKeys keys
     * @param comparator comparator
     * @return sorted
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] sortWOrderKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, Comparator<Object> comparator) {
        TreeMap<Object, Object> sort = new TreeMap<>(comparator);

        if (outgoingEvents == null || outgoingEvents.length < 2) {
            return outgoingEvents;
        }

        for (int i = 0; i < outgoingEvents.length; i++) {
            Object entry = sort.get(orderKeys[i]);
            if (entry == null) {
                sort.put(orderKeys[i], outgoingEvents[i]);
            } else if (entry instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
                List<EventBean> list = new ArrayList<EventBean>();
                list.add((eu.uk.ncl.pet5o.esper.client.EventBean) entry);
                list.add(outgoingEvents[i]);
                sort.put(orderKeys[i], list);
            } else {
                List<EventBean> list = (List<EventBean>) entry;
                list.add(outgoingEvents[i]);
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[outgoingEvents.length];
        int count = 0;
        for (Object entry : sort.values()) {
            if (entry instanceof List) {
                List<EventBean> output = (List<EventBean>) entry;
                for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : output) {
                    result[count++] = theEvent;
                }
            } else {
                result[count++] = (eu.uk.ncl.pet5o.esper.client.EventBean) entry;
            }
        }
        return result;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param outgoingEvents outgoing
     * @param orderKeys keys
     * @param comparator comparator
     * @return min or max
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean determineLocalMinMaxWOrderKeys(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, Comparator<Object> comparator) {
        Object localMinMax = null;
        eu.uk.ncl.pet5o.esper.client.EventBean outgoingMinMaxBean = null;

        for (int i = 0; i < outgoingEvents.length; i++) {
            boolean newMinMax = localMinMax == null || comparator.compare(localMinMax, orderKeys[i]) > 0;
            if (newMinMax) {
                localMinMax = orderKeys[i];
                outgoingMinMaxBean = outgoingEvents[i];
            }
        }

        return outgoingMinMaxBean;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param outgoingEvents outgoing
     * @param orderKeys keys
     * @param comparator comparator
     * @param rowLimitProcessor row limit
     * @return min or max
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] sortWOrderKeysWLimit(eu.uk.ncl.pet5o.esper.client.EventBean[] outgoingEvents, Object[] orderKeys, Comparator<Object> comparator, RowLimitProcessor rowLimitProcessor) {
        rowLimitProcessor.determineCurrentLimit();

        if (rowLimitProcessor.getCurrentRowLimit() == 1 &&
                rowLimitProcessor.getCurrentOffset() == 0 &&
                outgoingEvents != null && outgoingEvents.length > 1) {
            eu.uk.ncl.pet5o.esper.client.EventBean minmax = OrderByProcessorUtil.determineLocalMinMaxWOrderKeys(outgoingEvents, orderKeys, comparator);
            return new eu.uk.ncl.pet5o.esper.client.EventBean[]{minmax};
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] sorted = OrderByProcessorUtil.sortWOrderKeys(outgoingEvents, orderKeys, comparator);
        return rowLimitProcessor.applyLimit(sorted);
    }
}
