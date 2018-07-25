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
import eu.uk.ncl.pet5o.esper.util.CollectionUtil;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.Iterator;

public class NoopView extends ViewSupport implements DataWindowView {

    private final NoopViewFactory viewFactory;

    public NoopView(NoopViewFactory viewFactory) {
        this.viewFactory = viewFactory;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
    }

    public EventType getEventType() {
        return viewFactory.getEventType();
    }

    public Iterator<EventBean> iterator() {
        return CollectionUtil.NULL_EVENT_ITERATOR;
    }

    public void visitView(ViewDataVisitor viewDataVisitor) {
    }
}
