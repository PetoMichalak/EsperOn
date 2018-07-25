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

import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableMetadata;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.util.DestroyCallback;

public class EPStatementDestroyCallbackTableUpdStr implements DestroyCallback {
    private final TableService tableService;
    private final TableMetadata tableMetadata;
    private final String statementName;

    public EPStatementDestroyCallbackTableUpdStr(TableService tableService, TableMetadata tableMetadata, String statementName) {
        this.tableService = tableService;
        this.tableMetadata = tableMetadata;
        this.statementName = statementName;
    }

    public void destroy() {
        tableService.removeTableUpdateStrategyReceivers(tableMetadata, statementName);
    }
}
