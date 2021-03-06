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
package eu.uk.ncl.pet5o.esper.epl.core.resultset.handthru;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenInstanceAux;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationService;
import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryFactory;
import eu.uk.ncl.pet5o.esper.epl.core.resultset.core.ResultSetProcessorFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessor;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorCompiler;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;

/**
 * Result set processor prototye for the hand-through case:
 * no aggregation functions used in the select clause, and no group-by, no having and ordering.
 */
public class ResultSetProcessorHandThroughFactoryForge implements ResultSetProcessorFactoryForge {
    private final EventType resultEventType;
    private final SelectExprProcessorForge selectExprProcessorForge;
    private final boolean isSelectRStream;

    public ResultSetProcessorHandThroughFactoryForge(EventType resultEventType, SelectExprProcessorForge selectExprProcessorForge, boolean selectRStream) {
        this.resultEventType = resultEventType;
        this.selectExprProcessorForge = selectExprProcessorForge;
        this.isSelectRStream = selectRStream;
    }

    public EventType getResultEventType() {
        return resultEventType;
    }

    public boolean isSelectRStream() {
        return isSelectRStream;
    }

    public ResultSetProcessorFactory getResultSetProcessorFactory(StatementContext stmtContext, boolean isFireAndForget) {
        SelectExprProcessor selectExprProcessor = SelectExprProcessorCompiler.allocateSelectExprEvaluator(stmtContext.getEventAdapterService(), selectExprProcessorForge, stmtContext.getEngineImportService(), ResultSetProcessorFactoryFactory.class, isFireAndForget, stmtContext.getStatementName());
        return new ResultSetProcessorFactory() {

            public ResultSetProcessor instantiate(OrderByProcessor orderByProcessor, AggregationService aggregationService, AgentInstanceContext agentInstanceContext) {
                return new ResultSetProcessorHandThrough(ResultSetProcessorHandThroughFactoryForge.this, selectExprProcessor, agentInstanceContext);
            }

        };
    }

    public Class getInterfaceClass() {
        return ResultSetProcessor.class;
    }

    public void instanceCodegen(CodegenInstanceAux instance, CodegenClassScope classScope, CodegenCtor factoryCtor, List<CodegenTypedParam> factoryMembers) {
    }

    public void processViewResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorHandThrough.processViewResultCodegen(this, method);
    }

    public void processJoinResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorHandThrough.processJoinResultCodegen(this, method);
    }

    public void getIteratorViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorHandThrough.getIteratorViewCodegen(method);
    }

    public void getIteratorJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        ResultSetProcessorHandThrough.getIteratorJoinCodegen(method);
    }

    public void processOutputLimitedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().methodReturn(constantNull());
    }

    public void processOutputLimitedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().methodReturn(constantNull());
    }

    public void applyViewResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void applyJoinResultCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void continueOutputLimitedLastAllNonBufferedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().methodReturn(constantNull());
    }

    public void continueOutputLimitedLastAllNonBufferedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
        method.getBlock().methodReturn(constantNull());
    }

    public void processOutputLimitedLastAllNonBufferedViewCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void processOutputLimitedLastAllNonBufferedJoinCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void acceptHelperVisitorCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void stopMethodCodegen(CodegenClassScope classScope, CodegenMethodNode method, CodegenInstanceAux instance) {
    }

    public void clearMethodCodegen(CodegenClassScope classScope, CodegenMethodNode method) {
    }
}
