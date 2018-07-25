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
package eu.uk.ncl.pet5o.esper.epl.expression.core;

import com.espertech.esper.client.EventBean;

import java.util.Collection;

/**
 * Interface for evaluating of an event re. enumeration.
 */
public interface ExprEnumerationGivenEvent {
    public Collection<EventBean> evaluateEventGetROCollectionEvents(com.espertech.esper.client.EventBean event, ExprEvaluatorContext context);

    public Collection evaluateEventGetROCollectionScalar(com.espertech.esper.client.EventBean event, ExprEvaluatorContext context);

    public com.espertech.esper.client.EventBean evaluateEventGetEventBean(com.espertech.esper.client.EventBean event, ExprEvaluatorContext context);
}
