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
package eu.uk.ncl.pet5o.esper.view.window;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceFactoryCompiler;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactoryDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactoryFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceForgeDesc;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeServiceImpl;
import eu.uk.ncl.pet5o.esper.epl.declexpr.ExprDeclaredNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNode;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeGroupKey;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.visitor.ExprNodeSummaryVisitor;
import eu.uk.ncl.pet5o.esper.epl.expression.visitor.ExprNodeVariableVisitor;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;
import eu.uk.ncl.pet5o.esper.view.*;

import java.util.*;

/**
 * Base factory for expression-based window and batch view.
 */
public abstract class ExpressionViewFactoryBase implements DataWindowViewFactory, DataWindowViewWithPrevious {
    private EventType eventType;
    protected ExprNode expiryExpression;
    protected Set<String> variableNames;
    protected AggregationServiceFactoryDesc aggregationServiceFactoryDesc;
    protected EventType builtinMapType;
    protected ExprEvaluator expiryExpressionEvaluator;

    public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
        this.eventType = parentEventType;

        // define built-in fields
        Map<String, Object> builtinTypeDef = ExpressionViewOAFieldEnum.asMapOfTypes(eventType);
        builtinMapType = statementContext.getEventAdapterService().createAnonymousObjectArrayType(statementContext.getStatementId() + "_exprview", builtinTypeDef);
        StreamTypeService streamTypeService = new StreamTypeServiceImpl(new EventType[]{eventType, builtinMapType}, new String[2], new boolean[2], statementContext.getEngineURI(), false, false);

        // validate expression
        expiryExpression = ViewFactorySupport.validateExpr(getViewName(), statementContext, expiryExpression, streamTypeService, 0);

        ExprNodeSummaryVisitor summaryVisitor = new ExprNodeSummaryVisitor();
        expiryExpression.accept(summaryVisitor);
        if (summaryVisitor.isHasSubselect() || summaryVisitor.isHasStreamSelect() || summaryVisitor.isHasPreviousPrior()) {
            throw new ViewParameterException("Invalid expiry expression: Sub-select, previous or prior functions are not supported in this context");
        }
        expiryExpressionEvaluator = ExprNodeCompiler.allocateEvaluator(expiryExpression.getForge(), statementContext.getEngineImportService(), ExpressionViewFactoryBase.class, false, statementContext.getStatementName());

        Class returnType = expiryExpression.getForge().getEvaluationType();
        if (JavaClassHelper.getBoxedType(returnType) != Boolean.class) {
            throw new ViewParameterException("Invalid return value for expiry expression, expected a boolean return value but received " + JavaClassHelper.getParameterAsString(returnType));
        }

        // determine variables used, if any
        ExprNodeVariableVisitor visitor = new ExprNodeVariableVisitor(statementContext.getVariableService());
        expiryExpression.accept(visitor);
        variableNames = visitor.getVariableNames();

        // determine aggregation nodes, if any
        List<ExprAggregateNode> aggregateNodes = new ArrayList<ExprAggregateNode>();
        ExprAggregateNodeUtil.getAggregatesBottomUp(expiryExpression, aggregateNodes);
        if (!aggregateNodes.isEmpty()) {
            try {
                AggregationServiceForgeDesc forge = AggregationServiceFactoryFactory.getService(Collections.<ExprAggregateNode>emptyList(), Collections.<ExprNode, String>emptyMap(), Collections.<ExprDeclaredNode>emptyList(), null, aggregateNodes, Collections.<ExprAggregateNode>emptyList(), Collections.<ExprAggregateNodeGroupKey>emptyList(), false, statementContext.getAnnotations(), statementContext.getVariableService(), false, false, null, null, statementContext.getAggregationServiceFactoryService(), streamTypeService.getEventTypes(), null, statementContext.getContextName(), null, null, false, false, false, statementContext.getEngineImportService(), statementContext.getStatementName(), statementContext.getTimeAbacus());
                AggregationServiceFactory factory = AggregationServiceFactoryCompiler.allocate(forge.getAggregationServiceFactoryForge(), statementContext, false);
                aggregationServiceFactoryDesc = new AggregationServiceFactoryDesc(factory, forge.getExpressions(), forge.getGroupKeyExpressions());
            } catch (ExprValidationException ex) {
                throw new ViewParameterException(ex.getMessage(), ex);
            }
        }
    }


    public EventType getEventType() {
        return eventType;
    }

    public boolean canReuse(View view, AgentInstanceContext agentInstanceContext) {
        return false;
    }

    public EventType getBuiltinMapType() {
        return builtinMapType;
    }

    public ExprNode getExpiryExpression() {
        return expiryExpression;
    }

    public Set<String> getVariableNames() {
        return variableNames;
    }

    public AggregationServiceFactoryDesc getAggregationServiceFactoryDesc() {
        return aggregationServiceFactoryDesc;
    }

    public ExprEvaluator getExpiryExpressionEvaluator() {
        return expiryExpressionEvaluator;
    }
}
