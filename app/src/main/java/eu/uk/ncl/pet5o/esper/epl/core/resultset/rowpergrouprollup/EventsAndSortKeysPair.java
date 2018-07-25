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

public class EventsAndSortKeysPair {
    private final com.espertech.esper.client.EventBean[] events;
    private final Object[] sortKeys;

    public EventsAndSortKeysPair(com.espertech.esper.client.EventBean[] events, Object[] sortKeys) {
        this.events = events;
        this.sortKeys = sortKeys;
    }

    public com.espertech.esper.client.EventBean[] getEvents() {
        return events;
    }

    public Object[] getSortKeys() {
        return sortKeys;
    }
}
