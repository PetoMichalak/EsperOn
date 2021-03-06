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
package eu.uk.ncl.pet5o.esper.event.map;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.event.EventBeanWriter;
import eu.uk.ncl.pet5o.esper.event.MappedEventBean;

import java.util.Map;

/**
 * Writer method for writing to Map-type events.
 */
public class MapEventBeanWriterPerProp implements EventBeanWriter {
    private final MapEventBeanPropertyWriter[] writers;

    /**
     * Ctor.
     *
     * @param writers names of properties to write
     */
    public MapEventBeanWriterPerProp(MapEventBeanPropertyWriter[] writers) {
        this.writers = writers;
    }

    /**
     * Write values to an event.
     *
     * @param values   to write
     * @param theEvent to write to
     */
    public void write(Object[] values, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        MappedEventBean mappedEvent = (MappedEventBean) theEvent;
        Map<String, Object> map = mappedEvent.getProperties();

        for (int i = 0; i < writers.length; i++) {
            writers[i].write(values[i], map);
        }
    }
}
