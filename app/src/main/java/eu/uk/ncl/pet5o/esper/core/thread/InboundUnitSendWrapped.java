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
package eu.uk.ncl.pet5o.esper.core.thread;

import eu.uk.ncl.pet5o.esper.core.service.EPRuntimeEventSender;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Inbound unit for wrapped events.
 */
public class InboundUnitSendWrapped implements InboundUnitRunnable {
    private static final Logger log = LoggerFactory.getLogger(InboundUnitSendWrapped.class);
    private final eu.uk.ncl.pet5o.esper.client.EventBean eventBean;
    private final EPRuntimeEventSender runtime;

    /**
     * Ctor.
     *
     * @param theEvent inbound event, wrapped
     * @param runtime  to process
     */
    public InboundUnitSendWrapped(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EPRuntimeEventSender runtime) {
        this.eventBean = theEvent;
        this.runtime = runtime;
    }

    public void run() {
        try {
            runtime.processWrappedEvent(eventBean);
        } catch (RuntimeException e) {
            log.error("Unexpected error processing wrapped event: " + e.getMessage(), e);
        }
    }
}
