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
package eu.uk.ncl.pet5o.esper.epl.table.mgmt;

import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.lookup.EventTableIndexRepository;
import eu.uk.ncl.pet5o.esper.event.ObjectArrayBackedEventBean;

import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface TableStateInstanceGrouped {
    ReentrantReadWriteLock getTableLevelRWLock();

    ObjectArrayBackedEventBean getCreateRowIntoTable(Object groupByKey, ExprEvaluatorContext exprEvaluatorContext);

    void handleRowUpdated(ObjectArrayBackedEventBean row);

    ObjectArrayBackedEventBean getRowForGroupKey(Object groupKey);

    Set<Object> getGroupKeys();

    void clear();

    EventTableIndexRepository getIndexRepository();
}
