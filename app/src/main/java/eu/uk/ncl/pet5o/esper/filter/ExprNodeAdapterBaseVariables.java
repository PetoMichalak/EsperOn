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

import com.espertech.esper.epl.core.engineimport.EngineImportService;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprEvaluatorContext;
import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.epl.variable.VariableService;
import com.espertech.esper.filterspec.ExprNodeAdapterBase;

import java.lang.annotation.Annotation;

public class ExprNodeAdapterBaseVariables extends ExprNodeAdapterBase {
    protected final VariableService variableService;

    public ExprNodeAdapterBaseVariables(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext evaluatorContext, VariableService variableService, EngineImportService engineImportService, Annotation[] annotations) {
        super(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, evaluatorContext, engineImportService);
        this.variableService = variableService;
    }

    @Override
    public boolean evaluate(com.espertech.esper.client.EventBean theEvent) {
        variableService.setLocalVersion();
        return evaluatePerStream(new com.espertech.esper.client.EventBean[]{theEvent});
    }
}
