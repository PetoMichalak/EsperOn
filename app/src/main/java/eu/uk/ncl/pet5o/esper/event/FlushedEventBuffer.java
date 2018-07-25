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
package eu.uk.ncl.pet5o.esper.event;

import com.espertech.esper.client.EventBean;

import java.util.ArrayDeque;

/**
 * Buffer for events - accumulates events until flushed.
 */
public class FlushedEventBuffer {
    private ArrayDeque<EventBean[]> remainEvents = new ArrayDeque<EventBean[]>();

    /**
     * Add an event array to buffer.
     *
     * @param events to add
     */
    public void add(com.espertech.esper.client.EventBean[] events) {
        if (events != null) {
            remainEvents.add(events);
        }
    }

    /**
     * Get the events currently buffered. Returns null if the buffer is empty. Flushes the buffer.
     *
     * @return array of events in buffer or null if empty
     */
    public com.espertech.esper.client.EventBean[] getAndFlush() {
        com.espertech.esper.client.EventBean[] flattened = EventBeanUtility.flatten(remainEvents);
        remainEvents.clear();
        return flattened;
    }

    /**
     * Empty buffer.
     */
    public void flush() {
        remainEvents.clear();
    }
}
