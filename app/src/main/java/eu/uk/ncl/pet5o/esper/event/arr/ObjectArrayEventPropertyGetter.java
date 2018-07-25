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

import eu.uk.ncl.pet5o.esper.client.PropertyAccessException;
import eu.uk.ncl.pet5o.esper.event.EventPropertyGetterSPI;

/**
 * Property getter for Objectarray-underlying events.
 */
public interface ObjectArrayEventPropertyGetter extends EventPropertyGetterSPI {
    /**
     * Returns a property of an event.
     *
     * @param array to interrogate
     * @return property value
     * @throws eu.uk.ncl.pet5o.esper.client.PropertyAccessException for property access errors
     */
    public Object getObjectArray(Object[] array) throws PropertyAccessException;

    /**
     * Exists-function for properties in a object array-type event.
     *
     * @param array to interrogate
     * @return indicator
     */
    public boolean isObjectArrayExistsProperty(Object[] array);
}
