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

import eu.uk.ncl.pet5o.esper.client.EventBeanFactory;
import eu.uk.ncl.pet5o.esper.client.EventType;

public class EventBeanFactoryObjectArray implements EventBeanFactory {
    private final EventType type;
    private final EventAdapterService eventAdapterService;

    public EventBeanFactoryObjectArray(EventType type, EventAdapterService eventAdapterService) {
        this.type = type;
        this.eventAdapterService = eventAdapterService;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean wrap(Object underlying) {
        return eventAdapterService.adapterForTypedObjectArray((Object[]) underlying, type);
    }

    public Class getUnderlyingType() {
        return Object[].class;
    }
}
