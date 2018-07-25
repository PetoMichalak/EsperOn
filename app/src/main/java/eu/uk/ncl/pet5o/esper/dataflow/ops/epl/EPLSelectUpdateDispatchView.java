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
package eu.uk.ncl.pet5o.esper.dataflow.ops.epl;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.core.service.UpdateDispatchView;
import eu.uk.ncl.pet5o.esper.dataflow.ops.Select;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.Iterator;

public class EPLSelectUpdateDispatchView extends ViewSupport implements UpdateDispatchView {

    private final Select select;

    public EPLSelectUpdateDispatchView(Select select) {
        this.select = select;
    }

    public void newResult(UniformPair<EventBean[]> result) {
        select.outputOutputRateLimited(result);
    }

    public void update(EventBean[] newData, EventBean[] oldData) {
    }

    public EventType getEventType() {
        return null;
    }

    public Iterator<EventBean> iterator() {
        return null;
    }
}
