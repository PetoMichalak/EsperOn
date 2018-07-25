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
package eu.uk.ncl.pet5o.esper.core.start;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.context.ContextPartitionSelector;
import eu.uk.ncl.pet5o.esper.core.service.EPPreparedQueryResult;

/**
 * Starts and provides the stop method for EPL statements.
 */
public interface EPPreparedExecuteMethod {
    public EPPreparedQueryResult execute(ContextPartitionSelector[] contextPartitionSelectors);

    public EventType getEventType();
}
