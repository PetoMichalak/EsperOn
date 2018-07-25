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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.EPStatement;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ResultDeliveryStrategyTypeArrWStmt extends ResultDeliveryStrategyTypeArr {
    private final static Logger log = LoggerFactory.getLogger(ResultDeliveryStrategyTypeArrWStmt.class);

    public ResultDeliveryStrategyTypeArrWStmt(EPStatement statement, Object subscriber, Method method, Class componentType, EngineImportService engineImportService) {
        super(statement, subscriber, method, componentType, engineImportService);
    }

    @Override
    public void execute(UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> result) {
        Object newData;
        Object oldData;

        if (result == null) {
            newData = null;
            oldData = null;
        } else {
            newData = convert(result.getFirst());
            oldData = convert(result.getSecond());
        }

        Object[] parameters = new Object[]{statement, newData, oldData};
        try {
            fastMethod.invoke(subscriber, parameters);
        } catch (InvocationTargetException e) {
            ResultDeliveryStrategyImpl.handle(statement.getName(), log, e, parameters, subscriber, fastMethod);
        }
    }
}
