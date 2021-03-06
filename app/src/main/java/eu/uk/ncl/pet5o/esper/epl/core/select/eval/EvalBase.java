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
package eu.uk.ncl.pet5o.esper.epl.core.select.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

public abstract class EvalBase {

    protected final SelectExprForgeContext context;
    protected final EventType resultEventType;

    public EvalBase(SelectExprForgeContext context, EventType resultEventType) {
        this.context = context;
        this.resultEventType = resultEventType;
    }

    public EventAdapterService getEventAdapterService() {
        return context.getEventAdapterService();
    }

    public EventType getResultEventType() {
        return resultEventType;
    }
}
