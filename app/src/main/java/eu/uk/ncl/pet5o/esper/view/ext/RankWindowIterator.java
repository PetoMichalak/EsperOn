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
package eu.uk.ncl.pet5o.esper.view.ext;

import eu.uk.ncl.pet5o.esper.collection.MixedEventBeanAndCollectionIteratorBase;

import java.util.SortedMap;

/**
 * Iterator for use by {@link eu.uk.ncl.pet5o.esper.view.ext.RankWindowView}.
 */
public final class RankWindowIterator extends MixedEventBeanAndCollectionIteratorBase {
    private final SortedMap<Object, Object> window;

    /**
     * Ctor.
     *
     * @param window - sorted map with events
     */
    public RankWindowIterator(SortedMap<Object, Object> window) {
        super(window.keySet().iterator());
        this.window = window;
        init();
    }

    protected Object getValue(Object iteratorKeyValue) {
        return window.get(iteratorKeyValue);
    }
}
