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
package eu.uk.ncl.pet5o.esper.event.map;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.EventBeanCopyMethod;
import eu.uk.ncl.pet5o.esper.event.MappedEventBean;

import java.util.HashMap;
import java.util.Map;

/**
 * Copy method for Map-underlying events.
 */
public class MapEventBeanCopyMethod implements EventBeanCopyMethod {
    private final MapEventType mapEventType;
    private final EventAdapterService eventAdapterService;

    /**
     * Ctor.
     *
     * @param mapEventType        map event type
     * @param eventAdapterService for copying events
     */
    public MapEventBeanCopyMethod(MapEventType mapEventType, EventAdapterService eventAdapterService) {
        this.mapEventType = mapEventType;
        this.eventAdapterService = eventAdapterService;
    }

    public EventBean copy(EventBean theEvent) {
        MappedEventBean mapped = (MappedEventBean) theEvent;
        Map<String, Object> props = mapped.getProperties();
        return eventAdapterService.adapterForTypedMap(new HashMap<String, Object>(props), mapEventType);
    }
}
