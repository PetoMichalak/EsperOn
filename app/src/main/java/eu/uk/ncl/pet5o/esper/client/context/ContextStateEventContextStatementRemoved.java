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
package eu.uk.ncl.pet5o.esper.client.context;

/**
 * Context partition state event indicating a statement removed.
 */
public class ContextStateEventContextStatementRemoved extends ContextStateEvent {

    private final String statementName;

    /**
     * Ctor.
     * @param engineURI engine URI
     * @param contextName context name
     * @param statementName statement name
     */
    public ContextStateEventContextStatementRemoved(String engineURI, String contextName, String statementName) {
        super(engineURI, contextName);
        this.statementName = statementName;
    }

    /**
     * Returns the statement name.
     * @return name
     */
    public String getStatementName() {
        return statementName;
    }
}
