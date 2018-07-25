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

/**
 * Cache entry bean-to-collection-of-bean.
 */
public class ExpressionResultCacheEntryEventBeanArrayAndObj {
    private eu.uk.ncl.pet5o.esper.client.EventBean[] reference;
    private Object result;

    public ExpressionResultCacheEntryEventBeanArrayAndObj(eu.uk.ncl.pet5o.esper.client.EventBean[] reference, Object result) {
        this.reference = reference;
        this.result = result;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] getReference() {
        return reference;
    }

    public void setReference(eu.uk.ncl.pet5o.esper.client.EventBean[] reference) {
        this.reference = reference;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
