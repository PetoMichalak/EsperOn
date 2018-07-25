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
package eu.uk.ncl.pet5o.esper.filter;

import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.table.mgmt.TableService;
import eu.uk.ncl.pet5o.esper.filterspec.ExprNodeAdapterBase;

import java.lang.annotation.Annotation;

public class ExprNodeAdapterBaseWTableAccess extends ExprNodeAdapterBase {
    private final ExprNodeAdapterBase evalBase;
    private final TableService tableService;

    public ExprNodeAdapterBaseWTableAccess(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext evaluatorContext, ExprNodeAdapterBase evalBase, TableService tableService, EngineImportService engineImportService, Annotation[] annotations) {
        super(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, evaluatorContext, engineImportService);
        this.evalBase = evalBase;
        this.tableService = tableService;
    }

    @Override
    public boolean evaluate(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        try {
            return evalBase.evaluate(theEvent);
        } finally {
            tableService.getTableExprEvaluatorContext().releaseAcquiredLocks();
        }
    }
}
