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
package eu.uk.ncl.pet5o.esper.event.bean;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.event.EventBeanWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Writer for a set of event properties to a bean event.
 */
public class BeanEventBeanWriter implements EventBeanWriter {
    private static final Logger log = LoggerFactory.getLogger(BeanEventBeanWriter.class);

    private final BeanEventPropertyWriter[] writers;

    /**
     * Writes to use.
     *
     * @param writers writers
     */
    public BeanEventBeanWriter(BeanEventPropertyWriter[] writers) {
        this.writers = writers;
    }

    public void write(Object[] values, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        for (int i = 0; i < values.length; i++) {
            writers[i].write(values[i], theEvent);
        }
    }
}
