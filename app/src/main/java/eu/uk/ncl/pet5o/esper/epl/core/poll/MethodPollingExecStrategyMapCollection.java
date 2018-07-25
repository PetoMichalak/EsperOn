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

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableReader;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableService;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;

import net.sf.cglib.reflect.FastMethod;

import java.util.Map;

public class MethodPollingExecStrategyMapCollection extends MethodPollingExecStrategyBaseCollection {
    public MethodPollingExecStrategyMapCollection(EventAdapterService eventAdapterService, FastMethod method, EventType eventType, Object invocationTarget, MethodPollingExecStrategyEnum strategy, VariableReader variableReader, String variableName, VariableService variableService) {
        super(eventAdapterService, method, eventType, invocationTarget, strategy, variableReader, variableName, variableService);
    }

    protected eu.uk.ncl.pet5o.esper.client.EventBean getEventBean(Object value) {
        return eventAdapterService.adapterForTypedMap((Map<String, Object>) value, eventType);
    }
}
