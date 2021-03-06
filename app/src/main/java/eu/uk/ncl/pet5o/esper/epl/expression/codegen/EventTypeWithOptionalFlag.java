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
package eu.uk.ncl.pet5o.esper.epl.expression.codegen;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;

public class EventTypeWithOptionalFlag {
    private final CodegenExpressionRef ref;
    private final EventType eventType;
    private final boolean optionalEvent;

    public EventTypeWithOptionalFlag(CodegenExpressionRef ref, EventType eventType, boolean optionalEvent) {
        this.ref = ref;
        this.eventType = eventType;
        this.optionalEvent = optionalEvent;
    }

    public CodegenExpressionRef getRef() {
        return ref;
    }

    public EventType getEventType() {
        return eventType;
    }

    public boolean isOptionalEvent() {
        return optionalEvent;
    }
}
