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
package eu.uk.ncl.pet5o.esper.view.internal;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.event.FlushedEventBuffer;
import eu.uk.ncl.pet5o.esper.core.service.EPStatementDispatch;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.Iterator;

/**
 * View to dispatch for a single stream (no join).
 */
public final class SingleStreamDispatchView extends ViewSupport implements EPStatementDispatch {
    private boolean hasData = false;
    private FlushedEventBuffer newDataBuffer = new FlushedEventBuffer();
    private FlushedEventBuffer oldDataBuffer = new FlushedEventBuffer();

    /**
     * Ctor.
     */
    public SingleStreamDispatchView() {
    }

    public final EventType getEventType() {
        return parent.getEventType();
    }

    public final Iterator<EventBean> iterator() {
        return parent.iterator();
    }

    public final void update(EventBean[] newData, EventBean[] oldData) {
        newDataBuffer.add(newData);
        oldDataBuffer.add(oldData);
        hasData = true;
    }

    public void execute() {
        if (hasData) {
            hasData = false;
            this.updateChildren(newDataBuffer.getAndFlush(), oldDataBuffer.getAndFlush());
        }
    }
}
