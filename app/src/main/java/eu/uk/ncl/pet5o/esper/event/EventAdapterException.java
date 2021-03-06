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

import eu.uk.ncl.pet5o.esper.client.EPException;

/**
 * This exception is thrown to indicate a problem resolving an event type by name.
 */
public class EventAdapterException extends EPException {
    private static final long serialVersionUID = -6762596875991767135L;

    /**
     * Ctor.
     *
     * @param message - error message
     */
    public EventAdapterException(final String message) {
        super(message);
    }

    /**
     * Ctor.
     *
     * @param message - error message
     * @param nested  - nested exception
     */
    public EventAdapterException(final String message, Throwable nested) {
        super(message, nested);
    }
}
