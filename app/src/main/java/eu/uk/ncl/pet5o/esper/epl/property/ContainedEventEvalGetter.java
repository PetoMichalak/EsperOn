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
package eu.uk.ncl.pet5o.esper.epl.property;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;

public class ContainedEventEvalGetter implements ContainedEventEval {

    private final EventPropertyGetter getter;

    public ContainedEventEvalGetter(EventPropertyGetter getter) {
        this.getter = getter;
    }

    public Object getFragment(EventBean eventBean, EventBean[] eventsPerStream, ExprEvaluatorContext exprEvaluatorContext) {
        return getter.getFragment(eventBean);
    }
}
