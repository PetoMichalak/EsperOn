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

import java.util.Map;

/**
 * Interface for event types that provide decorating event properties as a name-value map.
 */
public interface DecoratingEventBean {
    /**
     * Returns decorating properties.
     *
     * @return property name and values
     */
    public Map<String, Object> getDecoratingProperties();

    /**
     * Returns the underlying event to the decorated event.
     *
     * @return underlying
     */
    public com.espertech.esper.client.EventBean getUnderlyingEvent();
}
