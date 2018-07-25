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

import eu.uk.ncl.pet5o.esper.client.EPRuntime;
import eu.uk.ncl.pet5o.esper.event.SendableEvent;

import java.util.Map;

public class SendableEventMap implements SendableEvent {
    private final Map<String, Object> event;
    private final String typeName;

    public SendableEventMap(Map<String, Object> event, String typeName) {
        this.event = event;
        this.typeName = typeName;
    }

    public void send(EPRuntime runtime) {
        runtime.sendEvent(event, typeName);
    }

    public Object getUnderlying() {
        return event;
    }
}
