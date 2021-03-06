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
package eu.uk.ncl.pet5o.esper.epl.join.table;

import eu.uk.ncl.pet5o.esper.core.service.StatementContext;

public class EventTableFactoryTableIdentStmt implements EventTableFactoryTableIdent {
    private final StatementContext statementContext;

    public EventTableFactoryTableIdentStmt(StatementContext statementContext) {
        this.statementContext = statementContext;
    }

    public StatementContext getStatementContext() {
        return statementContext;
    }
}
