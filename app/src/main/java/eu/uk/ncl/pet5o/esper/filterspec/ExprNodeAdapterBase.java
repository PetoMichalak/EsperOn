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
package eu.uk.ncl.pet5o.esper.filterspec;

import eu.uk.ncl.pet5o.esper.client.EPException;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExprNodeAdapterBase {
    private static final Logger log = LoggerFactory.getLogger(ExprNodeAdapterBase.class);

    private final int filterSpecId;
    private final int filterSpecParamPathNum;
    protected final ExprNode exprNode;
    protected final ExprEvaluator exprNodeEval;
    protected final ExprEvaluatorContext evaluatorContext;

    public ExprNodeAdapterBase(int filterSpecId, int filterSpecParamPathNum, ExprNode exprNode, ExprEvaluator exprEvaluator, ExprEvaluatorContext evaluatorContext, EngineImportService engineImportService) {
        this.filterSpecId = filterSpecId;
        this.filterSpecParamPathNum = filterSpecParamPathNum;
        this.exprNode = exprNode;
        this.exprNodeEval = exprEvaluator;
        this.evaluatorContext = evaluatorContext;
    }

    /**
     * Evaluate the boolean expression given the event as a stream zero event.
     *
     * @param theEvent is the stream zero event (current event)
     * @return boolean result of the expression
     */
    public boolean evaluate(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        return evaluatePerStream(new eu.uk.ncl.pet5o.esper.client.EventBean[]{theEvent});
    }

    protected boolean evaluatePerStream(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream) {
        try {
            Boolean result = (Boolean) exprNodeEval.evaluate(eventsPerStream, true, this.evaluatorContext);
            if (result == null) {
                return false;
            }
            return result;
        } catch (RuntimeException ex) {
            String message = "Error evaluating expression '" + ExprNodeUtilityCore.toExpressionStringMinPrecedenceSafe(exprNode) + "' statement '" + getStatementName() + "': " + ex.getMessage();
            log.error(message, ex);
            throw new EPException(message, ex);
        }
    }

    public String getStatementName() {
        return evaluatorContext.getStatementName();
    }

    public int getStatementId() {
        return evaluatorContext.getStatementId();
    }

    public ExprNode getExprNode() {
        return exprNode;
    }

    public int getFilterSpecId() {
        return filterSpecId;
    }

    public int getFilterSpecParamPathNum() {
        return filterSpecParamPathNum;
    }

    public ExprEvaluatorContext getEvaluatorContext() {
        return evaluatorContext;
    }
}
