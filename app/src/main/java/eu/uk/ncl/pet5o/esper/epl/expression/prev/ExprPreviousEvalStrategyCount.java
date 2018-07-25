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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndex;
import eu.uk.ncl.pet5o.esper.view.window.RandomAccessByIndexGetter;
import eu.uk.ncl.pet5o.esper.view.window.RelativeAccessByEventNIndex;
import eu.uk.ncl.pet5o.esper.view.window.RelativeAccessByEventNIndexGetter;

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

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        long size;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            size = randomAccess.getWindowCount();
        } else {
            eu.uk.ncl.pet5o.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            size = relativeAccess.getWindowToEventCount();
        }

        return size;
    }

    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }
}
