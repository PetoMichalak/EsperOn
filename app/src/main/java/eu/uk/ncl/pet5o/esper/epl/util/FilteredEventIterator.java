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
package eu.uk.ncl.pet5o.esper.epl.util;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An iterator that filters events suppied by another iterator,
 * using a list of one or more filter expressions as filter.
 */
public class FilteredEventIterator implements Iterator<EventBean> {
    private final Iterator<EventBean> parent;
    private final ExprEvaluator[] filterList;
    private final eu.uk.ncl.pet5o.esper.client.EventBean[] eventPerStream = new eu.uk.ncl.pet5o.esper.client.EventBean[1];
    private final ExprEvaluatorContext exprEvaluatorContext;
    private eu.uk.ncl.pet5o.esper.client.EventBean next;

    /**
     * Ctor.
     *
     * @param filters              is a list of expression nodes for filtering
     * @param parent               is the iterator supplying the events to apply the filter on
     * @param exprEvaluatorContext context for expression evalauation
     */
    public FilteredEventIterator(ExprEvaluator[] filters, Iterator<EventBean> parent, ExprEvaluatorContext exprEvaluatorContext) {
        this.parent = parent;
        this.filterList = filters;
        this.exprEvaluatorContext = exprEvaluatorContext;
        getNext();
    }

    public boolean hasNext() {
        return next != null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean next() {
        if (next == null) {
            throw new NoSuchElementException();
        }

        eu.uk.ncl.pet5o.esper.client.EventBean result = next;
        getNext();
        return result;
    }

    public void remove() {
        throw new UnsupportedOperationException();
    }

    private void getNext() {
        if ((filterList == null) || (filterList.length == 0)) {
            if (parent.hasNext()) {
                next = parent.next();
            } else {
                next = null;
            }
            return;
        }

        while (parent.hasNext()) {
            next = parent.next();

            eventPerStream[0] = next;
            boolean pass = true;
            for (ExprEvaluator filter : filterList) {
                Boolean result = (Boolean) filter.evaluate(eventPerStream, true, exprEvaluatorContext);
                if (result == null || !result) {
                    pass = false;
                    break;
                }
            }

            if (pass) {
                return;
            }
        }

        next = null;
    }
}
