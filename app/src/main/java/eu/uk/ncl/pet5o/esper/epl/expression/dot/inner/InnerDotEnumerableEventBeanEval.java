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
package eu.uk.ncl.pet5o.esper.epl.expression.dot.inner;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.epl.expression.core.ExprEnumerationEval;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.dot.ExprDotEvalRootChildInnerEval;

import java.util.Collection;

public class InnerDotEnumerableEventBeanEval implements ExprDotEvalRootChildInnerEval {

    private final ExprEnumerationEval rootLambdaEvaluator;

    public InnerDotEnumerableEventBeanEval(ExprEnumerationEval rootLambdaEvaluator) {
        this.rootLambdaEvaluator = rootLambdaEvaluator;
    }

    public Object evaluate(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return rootLambdaEvaluator.evaluateGetEventBean(eventsPerStream, isNewData, context);
    }

    public Collection<EventBean> evaluateGetROCollectionEvents(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return rootLambdaEvaluator.evaluateGetROCollectionEvents(eventsPerStream, isNewData, context);
    }

    public Collection evaluateGetROCollectionScalar(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return rootLambdaEvaluator.evaluateGetROCollectionScalar(eventsPerStream, isNewData, context);
    }

    public com.espertech.esper.client.EventBean evaluateGetEventBean(com.espertech.esper.client.EventBean[] eventsPerStream, boolean isNewData, ExprEvaluatorContext context) {
        return rootLambdaEvaluator.evaluateGetEventBean(eventsPerStream, isNewData, context);
    }

}
