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
package eu.uk.ncl.pet5o.esper.epl.named;

import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandle;
import eu.uk.ncl.pet5o.esper.core.context.util.EPStatementAgentInstanceHandleComparator;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class NamedWindowUtil {
    protected static Map<EPStatementAgentInstanceHandle, List<NamedWindowConsumerView>> createConsumerMap(boolean isPrioritized) {
        if (!isPrioritized) {
            return new LinkedHashMap<EPStatementAgentInstanceHandle, List<NamedWindowConsumerView>>();
        } else {
            return new TreeMap<EPStatementAgentInstanceHandle, List<NamedWindowConsumerView>>(EPStatementAgentInstanceHandleComparator.INSTANCE);
        }
    }
}
