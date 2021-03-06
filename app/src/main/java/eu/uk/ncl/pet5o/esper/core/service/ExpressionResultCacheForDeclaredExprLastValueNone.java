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

public class ExpressionResultCacheForDeclaredExprLastValueNone implements ExpressionResultCacheForDeclaredExprLastValue {

    public boolean cacheEnabled() {
        return false;
    }

    public ExpressionResultCacheEntryEventBeanArrayAndObj getDeclaredExpressionLastValue(Object node, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream) {
        return null;
    }

    public void saveDeclaredExpressionLastValue(Object node, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, Object result) {
    }
}
