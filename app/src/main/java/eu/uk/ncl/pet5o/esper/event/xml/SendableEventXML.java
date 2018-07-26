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
package eu.uk.ncl.pet5o.esper.event.xml;

import eu.uk.ncl.pet5o.esper.client.EPRuntime;
import eu.uk.ncl.pet5o.esper.event.SendableEvent;
import org.w3c.dom.Node;

public class SendableEventXML implements SendableEvent {
    private final Node event;

    public SendableEventXML(Node event) {
        this.event = event;
    }

    public void send(EPRuntime runtime) {
        runtime.sendEvent(event);
    }

    public Object getUnderlying() {
        return event;
    }
}
