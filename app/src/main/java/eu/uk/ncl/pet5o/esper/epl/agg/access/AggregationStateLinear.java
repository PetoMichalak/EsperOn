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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import com.espertech.esper.client.EventBean;

import java.util.Collection;
import java.util.Iterator;

public interface AggregationStateLinear {
    /**
     * Returns the first (oldest) value entered.
     *
     * @return first value
     */
    public com.espertech.esper.client.EventBean getFirstValue();

    /**
     * Returns the newest (last) value entered.
     *
     * @return last value
     */
    public com.espertech.esper.client.EventBean getLastValue();

    /**
     * Counting from the first element to the last, returns the oldest (first) value entered for index zero
     * and the n-th oldest value for index N.
     *
     * @param index index
     * @return last value
     */
    public com.espertech.esper.client.EventBean getFirstNthValue(int index);

    /**
     * Counting from the last element to the first, returns the newest (last) value entered for index zero
     * and the n-th newest value for index N.
     *
     * @param index index
     * @return last value
     */
    public com.espertech.esper.client.EventBean getLastNthValue(int index);

    /**
     * Returns all events for the group.
     *
     * @return group event iterator
     */
    public Iterator<EventBean> iterator();

    /**
     * Returns all events for the group.
     *
     * @return group event iterator
     */
    public Collection<EventBean> collectionReadOnly();

    /**
     * Returns the number of events in the group.
     *
     * @return size
     */
    public int size();
}
