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
package eu.uk.ncl.pet5o.esper.event.vaevent;

import com.espertech.esper.client.EventBean;

/**
 * A variant event is a type that can represent many event types.
 */
public interface VariantEvent {
    /**
     * Returns the underlying event.
     *
     * @return underlying event
     */
    public com.espertech.esper.client.EventBean getUnderlyingEventBean();
}
