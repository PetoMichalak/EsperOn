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

import java.util.Collection;
import java.util.Collections;

public class ExprPreviousEvalStrategyPrev implements ExprPreviousEvalStrategy {
    private final int streamNumber;
    private final ExprEvaluator indexNode;
    private final ExprEvaluator evalNode;
    private final RandomAccessByIndexGetter randomAccessGetter;
    private final RelativeAccessByEventNIndexGetter relativeAccessGetter;
    private final boolean isConstantIndex;
    private final Integer constantIndexNumber;
    private final boolean isTail;

    public ExprPreviousEvalStrategyPrev(int streamNumber, ExprEvaluator indexNode, ExprEvaluator evalNode, RandomAccessByIndexGetter randomAccessGetter, RelativeAccessByEventNIndexGetter relativeAccessGetter, boolean constantIndex, Integer constantIndexNumber, boolean tail) {
        this.streamNumber = streamNumber;
        this.indexNode = indexNode;
        this.evalNode = evalNode;
        this.randomAccessGetter = randomAccessGetter;
        this.relativeAccessGetter = relativeAccessGetter;
        isConstantIndex = constantIndex;
        this.constantIndexNumber = constantIndexNumber;
        isTail = tail;
    }

    public Object evaluate(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        eu.uk.ncl.pet5o.esper.client.EventBean substituteEvent = getSubstitute(eventsPerStream, exprEvaluatorContext);
        if (substituteEvent == null) {
            return null;
        }

        // Substitute original event with prior event, evaluate inner expression
        eu.uk.ncl.pet5o.esper.client.EventBean originalEvent = eventsPerStream[streamNumber];
        eventsPerStream[streamNumber] = substituteEvent;
        Object evalResult = evalNode.evaluate(eventsPerStream, true, exprEvaluatorContext);
        eventsPerStream[streamNumber] = originalEvent;

        return evalResult;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean evaluateGetEventBean(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return getSubstitute(eventsPerStream, context);
    }

    public Collection<EventBean> evaluateGetCollEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        return null;
    }

    public Collection evaluateGetCollScalar(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext context) {
        Object result = evaluate(eventsPerStream, context);
        if (result == null) {
            return null;
        }
        return Collections.singletonList(result);
    }

    private eu.uk.ncl.pet5o.esper.client.EventBean getSubstitute(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {

        // Use constant if supplied
        Integer index;
        if (isConstantIndex) {
            index = constantIndexNumber;
        } else {
            // evaluate first child, which returns the index
            Object indexResult = indexNode.evaluate(eventsPerStream, true, exprEvaluatorContext);
            if (indexResult == null) {
                return null;
            }
            index = ((Number) indexResult).intValue();
        }

        // access based on index returned
        eu.uk.ncl.pet5o.esper.client.EventBean substituteEvent;
        if (randomAccessGetter != null) {
            RandomAccessByIndex randomAccess = randomAccessGetter.getAccessor();
            if (!isTail) {
                substituteEvent = randomAccess.getNewData(index);
            } else {
                substituteEvent = randomAccess.getNewDataTail(index);
            }
        } else {
            eu.uk.ncl.pet5o.esper.client.EventBean evalEvent = eventsPerStream[streamNumber];
            RelativeAccessByEventNIndex relativeAccess = relativeAccessGetter.getAccessor(evalEvent);
            if (relativeAccess == null) {
                return null;
            }
            if (!isTail) {
                substituteEvent = relativeAccess.getRelativeToEvent(evalEvent, index);
            } else {
                substituteEvent = relativeAccess.getRelativeToEnd(index);
            }
        }
        return substituteEvent;
    }
}
