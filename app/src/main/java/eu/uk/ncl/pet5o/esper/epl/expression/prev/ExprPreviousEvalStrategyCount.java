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

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.view.window.RandomAccessByIndex;
import com.espertech.esper.view.window.RandomAccessByIndexGetter;
import com.espertech.esper.view.window.RelativeAccessByEventNIndex;
import com.espertech.esper.view.window.RelativeAccessByEventNIndexGetter;

import java.util.Collection;

public class ExprPreviousEvalStrategyCount implements ExprPreviousEvalStrategy {
    private final int streamNumber;
    private final RandomAccessByIndexGetter randomAccessGetter;
    private final RelativeAccessByEventNIndexGetter relativeAccessGetter;

    public ExprPreviousEvalStrategyCount(int streamNumber, RandomAccessByIndexGetter randomAccessGetter, RelativeAccessByEventNIndexGetter relativeAccessGetter) {
        this.streamNumber = streamNumber;
        this.randomAccessGetter = randomAccessGetter;
        this.relativeAccessGetter = relativeAccessGetter;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        long size;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            size = randomAccess.getWindowCount();
        } else {
            com.espertech.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            size = relativeAccess.getWindowToEventCount();
        }

        return size;
    }

    public Collection<EventBean> evaluateGetCollEvents(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetCollScalar(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }
}