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

/**
 * Interface for reading all event properties of an event.
 */
public interface EventBeanReader {
    /**
     * Returns all event properties in the exact order they appear as properties.
     *
     * @param theEvent to read
     * @return property values
     */
    public Object[] read(eu.uk.ncl.pet5o.esper.client.EventBean theEvent);
}
