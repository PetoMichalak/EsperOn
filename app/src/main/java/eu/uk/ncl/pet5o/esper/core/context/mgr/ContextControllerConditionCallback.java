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
package eu.uk.ncl.pet5o.esper.core.context.mgr;

import java.util.Map;

public interface ContextControllerConditionCallback {
    public void rangeNotification(Map<String, Object> builtinProperties, ContextControllerCondition originEndpoint, com.espertech.esper.client.EventBean optionalTriggeringEvent, Map<String, Object> optionalTriggeringPattern, com.espertech.esper.client.EventBean optionalTriggeringEventPattern, ContextInternalFilterAddendum filterAddendum);
}
