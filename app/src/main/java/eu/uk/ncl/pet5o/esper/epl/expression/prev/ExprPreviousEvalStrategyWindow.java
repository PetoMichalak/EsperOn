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
package eu.uk.ncl.pet5o.esper.epl.expression.prev;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndex;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndexGetter;
import eu.uk.ncl.pet5o.esper.view.window.RelativeAccessByEventNIndex;
import eu.uk.ncl.pet5o.esper.view.window.RelativeAccessByEventNIndexGetter;

import java.lang.reflect.Array;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;

public class ExprPreviousEvalStrategyWindow implements ExprPreviousEvalStrategy {
    private final int streamNumber;
    private final ExprEvaluator evalNode;
    private final Class componentType;
    private final RandomAccessByIndexGetter randomAccessGetter;
    private final RelativeAccessByEventNIndexGetter relativeAccessGetter;

    public ExprPreviousEvalStrategyWindow(int streamNumber, ExprEvaluator evalNode, Class componentType, RandomAccessByIndexGetter randomAccessGetter, RelativeAccessByEventNIndexGetter relativeAccessGetter) {
        this.streamNumber = streamNumber;
        this.evalNode = evalNode;
        this.componentType = componentType;
        this.randomAccessGetter = randomAccessGetter;
        this.relativeAccessGetter = relativeAccessGetter;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        Iterator<EventBean> events;
        int size;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            events = randomAccess.getWindowIterator();
            size = (int) randomAccess.getWindowCount();
        } else {
            eu.uk.ncl.pet5o.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            size = relativeAccess.getWindowToEventCount();
            events = relativeAccess.getWindowToEvent();
        }

        if (size <= 0) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean originalEvent = eventsPerStream[streamNumber];
        Object[] result = (Object[]) Array.newInstance(componentType, size);

        for (int i = 0; i < size; i++) {
            eventsPerStream[streamNumber] = events.next();
            Object evalResult = evalNode.evaluate(eventsPerStream, true, exprEvaluatorContext);
            result[i] = evalResult;
        }

        eventsPerStream[streamNumber] = originalEvent;
        return result;
    }

    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        Collection<EventBean> events;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            events = randomAccess.getWindowCollectionReadOnly();
        } else {
            eu.uk.ncl.pet5o.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            events = relativeAccess.getWindowToEventCollReadOnly();
        }
        return events;
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        Iterator<EventBean> events;
        int size;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            events = randomAccess.getWindowIterator();
            size = (int) randomAccess.getWindowCount();
        } else {
            eu.uk.ncl.pet5o.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            size = relativeAccess.getWindowToEventCount();
            events = relativeAccess.getWindowToEvent();
        }

        if (size <= 0) {
            return Collections.emptyList();
        }

        eu.uk.ncl.pet5o.esper.client.EventBean originalEvent = eventsPerStream[streamNumber];
        Deque deque = new ArrayDeque(size);
        for (int i = 0; i < size; i++) {
            eventsPerStream[streamNumber] = events.next();
            Object evalResult = evalNode.evaluate(eventsPerStream, true, context);
            deque.add(evalResult);
        }
        eventsPerStream[streamNumber] = originalEvent;
        return deque;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }
}
