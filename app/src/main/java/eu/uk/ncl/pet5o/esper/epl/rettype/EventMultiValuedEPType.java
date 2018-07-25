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
package eu.uk.ncl.pet5o.esper.epl.rettype;

import eu.uk.ncl.pet5o.esper.client.EventType;

/**
 * Clazz can be either
 * - Collection
 * - Array i.e. "EventType[].class"
 */
public class EventMultiValuedEPType implements EPType {
    private final Class container;
    private final EventType component;

    protected EventMultiValuedEPType(Class container, EventType component) {
        this.container = container;
        this.component = component;
    }

    public Class getContainer() {
        return container;
    }

    public EventType getComponent() {
        return component;
    }
}
