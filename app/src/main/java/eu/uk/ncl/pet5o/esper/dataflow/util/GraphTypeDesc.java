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
package eu.uk.ncl.pet5o.esper.dataflow.util;

import eu.uk.ncl.pet5o.esper.client.EventType;

public class GraphTypeDesc {

    private final boolean wildcard;
    private final boolean underlying;
    private final EventType eventType;

    public GraphTypeDesc(boolean wildcard, boolean underlying, EventType eventType) {
        this.wildcard = wildcard;
        this.underlying = underlying;
        this.eventType = eventType;
    }

    public boolean isWildcard() {
        return wildcard;
    }

    public boolean isUnderlying() {
        return underlying;
    }

    public EventType getEventType() {
        return eventType;
    }
}
