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
package eu.uk.ncl.pet5o.esper.epl.variable;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.SingleEventIterator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.view.ViewSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * A view that handles the setting of variables upon receipt of a triggering event.
 * <p>
 * Variables are updated atomically and thus a separate commit actually updates the
 * new variable values, or a rollback if an exception occured during validation.
 */
public class OnSetVariableView extends ViewSupport {
    private final OnSetVariableViewFactory factory;
    private final ExprEvaluatorContext exprEvaluatorContext;

    private final com.espertech.esper.client.EventBean[] eventsPerStream = new com.espertech.esper.client.EventBean[1];

    public OnSetVariableView(OnSetVariableViewFactory factory, ExprEvaluatorContext exprEvaluatorContext) {
        this.factory = factory;
        this.exprEvaluatorContext = exprEvaluatorContext;
    }

    public void update(com.espertech.esper.client.EventBean[] newData, com.espertech.esper.client.EventBean[] oldData) {
        if ((newData == null) || (newData.length == 0)) {
            return;
        }

        Map<String, Object> values = null;
        boolean produceOutputEvents = factory.getStatementResultService().isMakeNatural() || factory.getStatementResultService().isMakeSynthetic();

        if (produceOutputEvents) {
            values = new HashMap<String, Object>();
        }

        eventsPerStream[0] = newData[newData.length - 1];
        factory.getVariableReadWritePackage().writeVariables(factory.getVariableService(), eventsPerStream, values, exprEvaluatorContext);

        if (values != null) {
            com.espertech.esper.client.EventBean[] newDataOut = new com.espertech.esper.client.EventBean[1];
            newDataOut[0] = factory.getEventAdapterService().adapterForTypedMap(values, factory.getEventType());
            this.updateChildren(newDataOut, null);
        }
    }

    public EventType getEventType() {
        return factory.getEventType();
    }

    public Iterator<EventBean> iterator() {
        Map<String, Object> values = factory.getVariableReadWritePackage().iterate(exprEvaluatorContext.getAgentInstanceId());
        com.espertech.esper.client.EventBean theEvent = factory.getEventAdapterService().adapterForTypedMap(values, factory.getEventType());
        return new SingleEventIterator(theEvent);
    }
}
