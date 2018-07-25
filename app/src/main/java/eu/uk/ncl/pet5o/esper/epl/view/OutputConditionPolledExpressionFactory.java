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
package eu.uk.ncl.pet5o.esper.epl.view;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.visitor.ExprNodeIdentifierVisitor;
import eu.uk.ncl.pet5o.esper.epl.spec.OnTriggerSetAssignment;
import eu.uk.ncl.pet5o.esper.epl.variable.VariableReadWritePackage;
import eu.uk.ncl.pet5o.esper.event.arr.ObjectArrayEventBean;

import java.util.List;

/**
 * Output condition for output rate limiting that handles when-then expressions for controlling output.
 */
public class OutputConditionPolledExpressionFactory implements OutputConditionPolledFactory {
    private final ExprEvaluator whenExpressionNode;
    private final VariableReadWritePackage variableReadWritePackage;
    private final EventType oatypeBuiltinProperties;

    /**
     * Ctor.
     *
     * @param whenExpressionNode the expression to evaluate, returning true when to output
     * @param assignments        is the optional then-clause variable assignments, or null or empty if none
     * @param statementContext   context
     * @throws ExprValidationException when validation fails
     */
    public OutputConditionPolledExpressionFactory(ExprNode whenExpressionNode, List<OnTriggerSetAssignment> assignments, StatementContext statementContext)
            throws ExprValidationException {
        this.whenExpressionNode = ExprNodeCompiler.allocateEvaluator(whenExpressionNode.getForge(), statementContext.getEngineImportService(), this.getClass(), false, statementContext.getStatementName());

        // determine if using properties
        boolean containsBuiltinProperties = false;
        if (containsBuiltinProperties(whenExpressionNode)) {
            containsBuiltinProperties = true;
        } else {
            if (assignments != null) {
                for (OnTriggerSetAssignment assignment : assignments) {
                    if (containsBuiltinProperties(assignment.getExpression())) {
                        containsBuiltinProperties = true;
                    }
                }
            }
        }

        if (containsBuiltinProperties) {
            oatypeBuiltinProperties = statementContext.getEventAdapterService().createAnonymousObjectArrayType(OutputConditionPolledExpressionFactory.class.getName(), OutputConditionExpressionTypeUtil.TYPEINFO);
        } else {
            oatypeBuiltinProperties = null;
        }

        if (assignments != null) {
            variableReadWritePackage = new VariableReadWritePackage(assignments, statementContext.getVariableService(), statementContext.getEventAdapterService(), statementContext.getStatementName());
        } else {
            variableReadWritePackage = null;
        }
    }

    public OutputConditionPolled makeFromState(AgentInstanceContext agentInstanceContext, OutputConditionPolledState state) {
        ObjectArrayEventBean builtinProperties = null;
        if (oatypeBuiltinProperties != null) {
            builtinProperties = new ObjectArrayEventBean(OutputConditionExpressionTypeUtil.getOAPrototype(), oatypeBuiltinProperties);
        }
        OutputConditionPolledExpressionState expressionState = (OutputConditionPolledExpressionState) state;
        return new OutputConditionPolledExpression(this, expressionState, agentInstanceContext, builtinProperties);
    }

    public OutputConditionPolled makeNew(AgentInstanceContext agentInstanceContext) {
        ObjectArrayEventBean builtinProperties = null;
        Long lastOutputTimestamp = null;
        if (oatypeBuiltinProperties != null) {
            builtinProperties = new ObjectArrayEventBean(OutputConditionExpressionTypeUtil.getOAPrototype(), oatypeBuiltinProperties);
            lastOutputTimestamp = agentInstanceContext.getStatementContext().getSchedulingService().getTime();
        }
        OutputConditionPolledExpressionState state = new OutputConditionPolledExpressionState(0, 0, 0, 0, lastOutputTimestamp);
        return new OutputConditionPolledExpression(this, state, agentInstanceContext, builtinProperties);
    }

    public ExprEvaluator getWhenExpressionNode() {
        return whenExpressionNode;
    }

    public VariableReadWritePackage getVariableReadWritePackage() {
        return variableReadWritePackage;
    }

    private boolean containsBuiltinProperties(ExprNode expr) {
        ExprNodeIdentifierVisitor propertyVisitor = new ExprNodeIdentifierVisitor(false);
        expr.accept(propertyVisitor);
        return !propertyVisitor.getExprProperties().isEmpty();
    }
}
