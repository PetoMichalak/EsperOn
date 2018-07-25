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
package eu.uk.ncl.pet5o.esper.epl.agg.service.common;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationCodegenRowLevelDesc;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.publicConstValue;

/**
 * A null object implementation of the AggregationService
 * interface.
 */
public class AggregationServiceNullFactory implements AggregationServiceFactory, AggregationServiceFactoryForge {

    public final static AggregationServiceNullFactory AGGREGATION_SERVICE_NULL_FACTORY = new AggregationServiceNullFactory();

    public AggregationService makeService(AgentInstanceContext agentInstanceContext, EngineImportService engineImportService, boolean isSubquery, Integer subqueryNumber) {
        return AggregationServiceNull.INSTANCE;
    }

    public AggregationServiceFactory getAggregationServiceFactory(StatementContext stmtContext, boolean isFireAndForget) {
        return this;
    }

    public AggregationCodegenRowLevelDesc getRowLevelDesc() {
        return AggregationCodegenRowLevelDesc.EMPTY;
    }

    public void makeServiceCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(publicConstValue(AggregationServiceNull.class, "INSTANCE"));
    }

    public void ctorCodegen(CodegenCtor ctor, List<CodegenTypedParam> explicitMembers, CodegenClassScope classScope) {
    }

    public void getValueCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void getEventBeanCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void getCollectionOfEventsCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void applyEnterCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
    }

    public void applyLeaveCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
    }

    public void stopMethodCodegen(AggregationServiceFactoryForge forge, CodegenMethodNode method) {
    }

    public void rowCtorCodegen(CodegenClassScope classScope, CodegenCtor rowCtor, List<CodegenTypedParam> rowMembers, CodegenNamedMethods namedMethods) {
    }

    public void setRemovedCallbackCodegen(CodegenMethodNode method) {
    }

    public void setCurrentAccessCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void clearResultsCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void getCollectionScalarCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(constantNull());
    }

    public void acceptCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void getGroupKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantNull());
    }

    public void getGroupKeyCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantNull());
    }

    public void acceptGroupDetailCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void isGroupedCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantFalse());
    }
}
