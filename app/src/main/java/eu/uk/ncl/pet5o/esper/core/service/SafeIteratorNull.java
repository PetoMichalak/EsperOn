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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.SafeIterator;

import java.util.NoSuchElementException;

public class SafeIteratorNull<E> implements SafeIterator<E> {
    public static final SafeIterator<eu.uk.ncl.pet5o.esper.client.EventBean> NULL_EVENT_ITER = new SafeIteratorNull<eu.uk.ncl.pet5o.esper.client.EventBean>();

    /**
     * Ctor.
     */
    public SafeIteratorNull() {
    }

    public boolean hasNext() {
        return false;
    }

    public E next() {
        throw new NoSuchElementException();
    }

    public void close() {
    }

    public void remove() {
        throw new UnsupportedOperationException("Remove operation not supported");
    }
}
