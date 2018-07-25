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

import eu.uk.ncl.pet5o.esper.core.service.EPServicesContext;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.table.ExprTableAccessNode;

public class EPPreparedExecuteIUDSingleStreamExecInsert implements EPPreparedExecuteIUDSingleStreamExec {
    private final ExprEvaluatorContext exprEvaluatorContext;
    private final SelectExprProcessor insertHelper;
    private final ExprTableAccessNode[] optionalTableNodes;
    private final EPServicesContext services;

    public EPPreparedExecuteIUDSingleStreamExecInsert(ExprEvaluatorContext exprEvaluatorContext, SelectExprProcessor insertHelper, ExprTableAccessNode[] optionalTableNodes, EPServicesContext services) {
        this.exprEvaluatorContext = exprEvaluatorContext;
        this.insertHelper = insertHelper;
        this.optionalTableNodes = optionalTableNodes;
        this.services = services;
    }

    public eu.uk.ncl.pet5o.esper.client.EventBean[] execute(FireAndForgetInstance fireAndForgetProcessorInstance) {
        return fireAndForgetProcessorInstance.processInsert(this);
    }

    public ExprEvaluatorContext getExprEvaluatorContext() {
        return exprEvaluatorContext;
    }

    public SelectExprProcessor getInsertHelper() {
        return insertHelper;
    }

    public ExprTableAccessNode[] getOptionalTableNodes() {
        return optionalTableNodes;
    }

    public EPServicesContext getServices() {
        return services;
    }
}
