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
 * Interface for statement result callbacks.
 */
public interface StatementResultListener {
    /**
     * Provide statement result.
     *
     * @param newEvents         insert stream
     * @param oldEvents         remove stream
     * @param statementName     stmt name
     * @param statement         stmt
     * @param epServiceProvider engine
     */
    public void update(eu.uk.ncl.pet5o.esper.client.EventBean[] newEvents, eu.uk.ncl.pet5o.esper.client.EventBean[] oldEvents, String statementName, EPStatementSPI statement, EPServiceProviderSPI epServiceProvider);
}
