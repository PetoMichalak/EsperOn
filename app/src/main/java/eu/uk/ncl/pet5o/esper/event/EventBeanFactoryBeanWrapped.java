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

import java.util.Collections;

public class EventBeanFactoryBeanWrapped implements EventBeanFactory {

    private final EventType beanEventType;
    private final EventType wrapperEventType;
    private final EventAdapterService eventAdapterService;

    public EventBeanFactoryBeanWrapped(EventType beanEventType, EventType wrapperEventType, EventAdapterService eventAdapterService) {
        this.beanEventType = beanEventType;
        this.wrapperEventType = wrapperEventType;
        this.eventAdapterService = eventAdapterService;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean wrap(Object underlying) {
        eu.uk.ncl.pet5o.esper.client.EventBean bean = eventAdapterService.adapterForTypedBean(underlying, beanEventType);
        return eventAdapterService.adapterForTypedWrapper(bean, Collections.<String, Object>emptyMap(), wrapperEventType);
    }

    public Class getUnderlyingType() {
        return beanEventType.getUnderlyingType();
    }
}
