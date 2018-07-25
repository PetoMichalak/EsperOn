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
package eu.uk.ncl.pet5o.esper.event.arr;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.event.EventPropertyWriter;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

public class ObjectArrayEventBeanPropertyWriter implements EventPropertyWriter {

    protected final int index;

    public ObjectArrayEventBeanPropertyWriter(int index) {
        this.index = index;
    }

    public void write(Object value, EventBean target) {
        ObjectArrayBackedEventBean arrayEvent = (ObjectArrayBackedEventBean) target;
        write(value, arrayEvent.getProperties());
    }

    public void write(Object value, Object[] array) {
        array[index] = value;
    }
}
