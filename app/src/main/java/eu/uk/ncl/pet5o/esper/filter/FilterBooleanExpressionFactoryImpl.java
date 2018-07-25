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

import eu.uk.ncl.pet5o.esper.core.service.ExprEvaluatorContextWTableAccess;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableService;
import eu.uk.ncl.pet5o.esper.filterspec.ExprNodeAdapterBase;
import eu.uk.ncl.pet5o.esper.filterspec.FilterBooleanExpressionFactory;
import eu.uk.ncl.pet5o.esper.filterspec.FilterSpecParamExprNode;

import java.lang.annotation.Annotation;

public class FilterBooleanExpressionFactoryImpl implements FilterBooleanExpressionFactory {

    public ExprNodeAdapterBase make(FilterSpecParamExprNode node, eu.uk.ncl.pet5o.esper.client.EventBean[] events, ExprEvaluatorContext exprEvaluatorContext, int agentInstanceId, EngineImportService engineImportService, Annotation[] annotations) {

        int filterSpecId = node.getFilterSpecId();
        int filterSpecParamPathNum = node.getFilterSpecParamPathNum();
        ExprNode exprNode = node.getExprNode();
        VariableService variableService = node.getVariableService();

        // handle table evaluator context
        if (node.isHasTableAccess()) {
            exprEvaluatorContext = new ExprEvaluatorContextWTableAccess(exprEvaluatorContext, node.getTableService());
        }

        // non-pattern case
        ExprNodeAdapterBase adapter;
        ExprEvaluator exprEvaluator = exprNode.getForge().getExprEvaluator();

        if (events == null) {

            // if a subquery is present in a filter stream acquire the agent instance lock
            if (node.isHasFilterStreamSubquery()) {
                adapter = getLockableSingle(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableService, engineImportService, annotations, agentInstanceId);
            } else if (!node.isHasVariable()) {
                // no-variable no-prior event evaluation
                adapter = new ExprNodeAdapterBase(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, engineImportService);
            } else {
                // with-variable no-prior event evaluation
                adapter = new ExprNodeAdapterBaseVariables(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableService, engineImportService, annotations);
            }
        } else {
            // pattern cases
            VariableService variableServiceToUse = node.isHasVariable() ? variableService : null;
            if (node.isUseLargeThreadingProfile()) {
                // no-threadlocal evaluation
                // if a subquery is present in a pattern filter acquire the agent instance lock
                if (node.isHasFilterStreamSubquery()) {
                    adapter = getLockableMultiStreamNoTL(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
                } else {
                    adapter = new ExprNodeAdapterMultiStreamNoTL(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
                }
            } else {
                if (node.isHasFilterStreamSubquery()) {
                    adapter = getLockableMultiStream(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
                } else {
                    // evaluation with threadlocal cache
                    adapter = new ExprNodeAdapterMultiStream(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
                }
            }
        }

        if (!node.isHasTableAccess()) {
            return adapter;
        }

        // handle table
        return new ExprNodeAdapterBaseWTableAccess(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, adapter, node.getTableService(), engineImportService, annotations);
    }

    protected ExprNodeAdapterBase getLockableSingle(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext exprEvaluatorContext, VariableService variableService, EngineImportService engineImportService, Annotation[] annotations, int agentInstanceId) {
        return new ExprNodeAdapterBaseStmtLock(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableService, engineImportService, annotations);
    }

    protected ExprNodeAdapterBase getLockableMultiStreamNoTL(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext exprEvaluatorContext, VariableService variableServiceToUse, EngineImportService engineImportService, eu.uk.ncl.pet5o.esper.client.EventBean[] events, Annotation[] annotations) {
        return new ExprNodeAdapterMultiStreamNoTLStmtLock(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
    }

    protected ExprNodeAdapterBase getLockableMultiStream(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext exprEvaluatorContext, VariableService variableServiceToUse, EngineImportService engineImportService, eu.uk.ncl.pet5o.esper.client.EventBean[] events, Annotation[] annotations) {
        return new ExprNodeAdapterMultiStreamStmtLock(filterSpecId, filterSpecParamPathNum, exprNode, exprEvaluator, exprEvaluatorContext, variableServiceToUse, engineImportService, events, annotations);
    }
}
