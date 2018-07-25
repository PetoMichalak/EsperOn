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
import net.sf.cglib.reflect.FastMethod;

public class BeanEventPropertyWriterIndexedProp extends BeanEventPropertyWriter {

    private final int index;

    public BeanEventPropertyWriterIndexedProp(Class clazz, FastMethod writerMethod, int index) {
        super(clazz, writerMethod);
        this.index = index;
    }

    public void write(Object value, eu.uk.ncl.pet5o.esper.client.EventBean target) {
        super.invoke(new Object[]{index, value}, target.getUnderlying());
    }
}
