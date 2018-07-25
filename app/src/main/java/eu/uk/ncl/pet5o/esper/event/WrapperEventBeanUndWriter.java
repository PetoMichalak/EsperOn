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
package eu.uk.ncl.pet5o.esper.event;

import eu.uk.ncl.pet5o.esper.client.EventBean;

/**
 * Writer for values to a wrapper event.
 */
public class WrapperEventBeanUndWriter implements EventBeanWriter {
    private final EventBeanWriter undWriter;

    /**
     * Ctor.
     *
     * @param undWriter writer to the underlying object
     */
    public WrapperEventBeanUndWriter(EventBeanWriter undWriter) {
        this.undWriter = undWriter;
    }

    public void write(Object[] values, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        DecoratingEventBean wrappedEvent = (DecoratingEventBean) theEvent;
        eu.uk.ncl.pet5o.esper.client.EventBean eventWrapped = wrappedEvent.getUnderlyingEvent();
        undWriter.write(values, eventWrapped);
    }
}
