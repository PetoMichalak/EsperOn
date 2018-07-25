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
import eu.uk.ncl.pet5o.esper.collection.ViewUpdatedCollection;
import eu.uk.ncl.pet5o.esper.view.ViewDataVisitable;
import eu.uk.ncl.pet5o.esper.view.ViewDataVisitor;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;
import eu.uk.ncl.pet5o.esper.view.Viewable;

import java.util.Iterator;

/**
 * View that provides access to prior events posted by the parent view for use by 'prior' expression nodes.
 */
public class PriorEventView extends ViewSupport implements ViewDataVisitable {
    private Viewable parent;
    protected ViewUpdatedCollection buffer;

    /**
     * Ctor.
     *
     * @param buffer is handling the actual storage of events for use in the 'prior' expression
     */
    public PriorEventView(ViewUpdatedCollection buffer) {
        this.buffer = buffer;
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
        buffer.update(newData, oldData);
        this.updateChildren(newData, oldData);
    }

    public void setParent(Viewable parent) {
        this.parent = parent;
    }

    /**
     * Returns the underlying buffer used for access to prior events.
     *
     * @return buffer
     */
    public ViewUpdatedCollection getBuffer() {
        return buffer;
    }

    public EventType getEventType() {
        return parent.getEventType();
    }

    public Iterator<EventBean> iterator() {
        return parent.iterator();
    }

    public void visitView(ViewDataVisitor viewDataVisitor) {
        viewDataVisitor.visitPrimary(buffer, "Prior");
    }
}
