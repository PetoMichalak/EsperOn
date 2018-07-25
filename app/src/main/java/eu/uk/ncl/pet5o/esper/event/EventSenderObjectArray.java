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

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.client.EventSender;
import eu.uk.ncl.pet5o.esper.core.service.EPRuntimeEventSender;
import eu.uk.ncl.pet5o.esper.core.thread.InboundUnitSendWrapped;
import eu.uk.ncl.pet5o.esper.core.thread.ThreadingOption;
import eu.uk.ncl.pet5o.esper.core.thread.ThreadingService;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventType;

/**
 * Event sender for map-backed events.
 * <p>
 * Allows sending only event objects of type map, does not check map contents. Any other event object generates an error.
 */
public class EventSenderObjectArray implements EventSender {
    private final EPRuntimeEventSender runtimeEventSender;
    private final EventAdapterService eventAdapterService;
    private final ObjectArrayEventType objectArrayEventType;
    private final ThreadingService threadingService;

    /**
     * Ctor.
     *
     * @param runtimeEventSender   for processing events
     * @param objectArrayEventType the event type
     * @param threadingService     for inbound threading
     * @param eventAdapterService  for event bean creation
     */
    public EventSenderObjectArray(EPRuntimeEventSender runtimeEventSender, ObjectArrayEventType objectArrayEventType, EventAdapterService eventAdapterService, ThreadingService threadingService) {
        this.runtimeEventSender = runtimeEventSender;
        this.objectArrayEventType = objectArrayEventType;
        this.threadingService = threadingService;
        this.eventAdapterService = eventAdapterService;
    }

    public void sendEvent(Object theEvent) {
        if (!(theEvent.getClass().isArray())) {
            throw new EPException("Unexpected event object of type " + theEvent.getClass().getName() + ", expected Object[]");
        }

        Object[] arr = (Object[]) theEvent;
        eu.uk.ncl.pet5o.esper.client.EventBean objectArrayEvent = eventAdapterService.adapterForTypedObjectArray(arr, objectArrayEventType);

        if ((ThreadingOption.isThreadingEnabled) && (threadingService.isInboundThreading())) {
            threadingService.submitInbound(new InboundUnitSendWrapped(objectArrayEvent, runtimeEventSender));
        } else {
            runtimeEventSender.processWrappedEvent(objectArrayEvent);
        }
    }

    public void route(Object theEvent) {
        if (!(theEvent.getClass().isArray())) {
            throw new EPException("Unexpected event object of type " + theEvent.getClass().getName() + ", expected Object[]");
        }
        Object[] arr = (Object[]) theEvent;
        eu.uk.ncl.pet5o.esper.client.EventBean objectArrayEvent = eventAdapterService.adapterForTypedObjectArray(arr, objectArrayEventType);
        runtimeEventSender.routeEventBean(objectArrayEvent);
    }
}
