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
package eu.uk.ncl.pet5o.esper.core.context.util;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.service.UpdateDispatchView;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.Iterator;

public class ContextMergeView extends ViewSupport implements UpdateDispatchView {

    private final EventType eventType;

    public ContextMergeView(EventType eventType) {
        this.eventType = eventType;
    }

    public void update(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        // no action required
    }

    public void newResult(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result) {
        if (result != null) {
            updateChildren(result.getFirst(), result.getSecond());
        }
    }

    public EventType getEventType() {
        return eventType;
    }

    public Iterator<EventBean> iterator() {
        throw new UnsupportedOperationException("Iterator not supported");
    }
}
