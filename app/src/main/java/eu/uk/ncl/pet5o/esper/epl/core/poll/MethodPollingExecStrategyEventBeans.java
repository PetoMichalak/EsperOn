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
package eu.uk.ncl.pet5o.esper.epl.core.poll;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableReader;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableService;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import net.sf.cglib.reflect.FastMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class MethodPollingExecStrategyEventBeans extends MethodPollingExecStrategyBase {
    public MethodPollingExecStrategyEventBeans(EventAdapterService eventAdapterService, FastMethod method, EventType eventType, Object invocationTarget, MethodPollingExecStrategyEnum strategy, VariableReader variableReader, String variableName, VariableService variableService) {
        super(eventAdapterService, method, eventType, invocationTarget, strategy, variableReader, variableName, variableService);
    }

    protected List<EventBean> handleResult(Object invocationResult) {
        if (invocationResult == null) {
            return Collections.emptyList();
        }
        if (invocationResult.getClass().isArray()) {
            return Arrays.asList((eu.uk.ncl.pet5o.esper.client.EventBean[]) invocationResult);
        }
        if (invocationResult instanceof Collection) {
            Collection collection = (Collection) invocationResult;
            int length = collection.size();
            if (length == 0) {
                return Collections.emptyList();
            }
            ArrayList<EventBean> rowResult = new ArrayList<EventBean>(length);
            Iterator<EventBean> it = collection.iterator();
            for (; it.hasNext(); ) {
                eu.uk.ncl.pet5o.esper.client.EventBean value = it.next();
                if (value != null) {
                    rowResult.add(value);
                }
            }
            return rowResult;
        }
        Iterator<EventBean> it = (Iterator<EventBean>) invocationResult;
        if (!it.hasNext()) {
            return Collections.emptyList();
        }
        ArrayList<EventBean> rowResult = new ArrayList<>();
        for (; it.hasNext(); ) {
            eu.uk.ncl.pet5o.esper.client.EventBean value = it.next();
            if (value != null) {
                rowResult.add(value);
            }
        }
        return rowResult;
    }
}
