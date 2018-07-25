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

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.SingleEventIterator;
import eu.uk.ncl.pet5o.esper.core.service.StatementResultService;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.view.ViewSupport;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * View for handling create-variable syntax.
 * <p>
 * The view posts to listeners when a variable changes, if it has subviews.
 * <p>
 * The view returns the current variable value for the iterator.
 * <p>
 * The event type for such posted events is a single field Map with the variable value.
 */
public class CreateVariableView extends ViewSupport implements VariableChangeCallback {
    private final EventAdapterService eventAdapterService;
    private final VariableReader reader;
    private final EventType eventType;
    private final String variableName;
    private final StatementResultService statementResultService;

    /**
     * Ctor.
     *
     * @param eventAdapterService    for creating events
     * @param variableService        for looking up variables
     * @param variableName           is the name of the variable to create
     * @param statementResultService for coordinating on whether insert and remove stream events should be posted
     * @param statementId            statement id
     * @param agentInstanceId        agent instance id
     */
    public CreateVariableView(int statementId, EventAdapterService eventAdapterService, VariableService variableService, String variableName, StatementResultService statementResultService, int agentInstanceId) {
        this.eventAdapterService = eventAdapterService;
        this.variableName = variableName;
        this.statementResultService = statementResultService;
        reader = variableService.getReader(variableName, agentInstanceId);
        eventType = getEventType(statementId, eventAdapterService, reader.getVariableMetaData());
    }

    public static EventType getEventType(int statementId, EventAdapterService eventAdapterService, VariableMetaData variableMetaData) {
        Map<String, Object> variableTypes = new HashMap<String, Object>();
        variableTypes.put(variableMetaData.getVariableName(), variableMetaData.getType());
        String outputEventTypeName = statementId + "_outcreatevar";
        return eventAdapterService.createAnonymousMapType(outputEventTypeName, variableTypes, true);
    }

    public void update(Object newValue, Object oldValue) {
        if (statementResultService.isMakeNatural() || statementResultService.isMakeSynthetic()) {
            Map<String, Object> valuesOld = new HashMap<String, Object>();
            valuesOld.put(variableName, oldValue);
            eu.uk.ncl.pet5o.esper.client.EventBean eventOld = eventAdapterService.adapterForTypedMap(valuesOld, eventType);

            Map<String, Object> valuesNew = new HashMap<String, Object>();
            valuesNew.put(variableName, newValue);
            eu.uk.ncl.pet5o.esper.client.EventBean eventNew = eventAdapterService.adapterForTypedMap(valuesNew, eventType);

            this.updateChildren(new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventNew}, new eu.uk.ncl.pet5o.esper.client.EventBean[]{eventOld});
        }
    }

    public void update(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, eu.uk.ncl.pet5o.esper.client.EventBean[] oldData) {
        throw new UnsupportedOperationException("Update not supported");
    }

    public EventType getEventType() {
        return eventType;
    }

    public Iterator<EventBean> iterator() {
        Object value = reader.getValue();
        Map<String, Object> values = new HashMap<String, Object>();
        values.put(variableName, value);
        eu.uk.ncl.pet5o.esper.client.EventBean theEvent = eventAdapterService.adapterForTypedMap(values, eventType);
        return new SingleEventIterator(theEvent);
    }
}
