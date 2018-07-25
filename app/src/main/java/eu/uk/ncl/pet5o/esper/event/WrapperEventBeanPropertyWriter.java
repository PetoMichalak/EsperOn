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
 * Writer for a set of wrapper event object values.
 */
public class WrapperEventBeanPropertyWriter implements EventBeanWriter {
    private final EventPropertyWriter[] writerArr;

    /**
     * Ctor.
     *
     * @param writerArr writers are writing properties.
     */
    public WrapperEventBeanPropertyWriter(EventPropertyWriter[] writerArr) {
        this.writerArr = writerArr;
    }

    public void write(Object[] values, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        for (int i = 0; i < values.length; i++) {
            writerArr[i].write(values[i], theEvent);
        }
    }
}
