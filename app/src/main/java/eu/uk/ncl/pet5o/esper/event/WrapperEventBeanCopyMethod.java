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

import java.util.HashMap;
import java.util.Map;

/**
 * Copy method for wrapper events.
 */
public class WrapperEventBeanCopyMethod implements EventBeanCopyMethod {
    private final WrapperEventType wrapperEventType;
    private final EventAdapterService eventAdapterService;
    private final EventBeanCopyMethod underlyingCopyMethod;

    /**
     * Ctor.
     *
     * @param wrapperEventType     wrapper type
     * @param eventAdapterService  event adapter creation
     * @param underlyingCopyMethod copy method for the underlying event
     */
    public WrapperEventBeanCopyMethod(WrapperEventType wrapperEventType, EventAdapterService eventAdapterService, EventBeanCopyMethod underlyingCopyMethod) {
        this.wrapperEventType = wrapperEventType;
        this.eventAdapterService = eventAdapterService;
        this.underlyingCopyMethod = underlyingCopyMethod;
    }

    public com.espertech.esper.client.EventBean copy(com.espertech.esper.client.EventBean theEvent) {
        DecoratingEventBean decorated = (DecoratingEventBean) theEvent;
        com.espertech.esper.client.EventBean decoratedUnderlying = decorated.getUnderlyingEvent();
        com.espertech.esper.client.EventBean copiedUnderlying = underlyingCopyMethod.copy(decoratedUnderlying);
        if (copiedUnderlying == null) {
            return null;
        }
        Map<String, Object> copiedMap = new HashMap<String, Object>(decorated.getDecoratingProperties());
        return eventAdapterService.adapterForTypedWrapper(copiedUnderlying, copiedMap, wrapperEventType);
    }
}
