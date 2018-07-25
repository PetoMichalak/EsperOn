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
package eu.uk.ncl.pet5o.esper.epl.expression.accessagg;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.core.service.StatementExtensionSvcContext;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateKey;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationFactoryFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionAgentContext;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionHandler;

public class ExprPlugInAggMultiFunctionNodeFactory implements AggregationMethodFactory {
    private final ExprPlugInAggMultiFunctionNode parent;
    private final PlugInAggregationMultiFunctionHandler handlerPlugin;
    private final AggregationFactoryFactory aggregationFactoryFactory;
    private final StatementExtensionSvcContext statementExtensionSvcContext;
    private EPType returnType;

    public ExprPlugInAggMultiFunctionNodeFactory(ExprPlugInAggMultiFunctionNode parent, PlugInAggregationMultiFunctionHandler handlerPlugin, AggregationFactoryFactory aggregationFactoryFactory, StatementExtensionSvcContext statementExtensionSvcContext) {
        this.handlerPlugin = handlerPlugin;
        this.parent = parent;
        this.aggregationFactoryFactory = aggregationFactoryFactory;
        this.statementExtensionSvcContext = statementExtensionSvcContext;
    }

    public boolean isAccessAggregation() {
        return true;
    }

    public AggregationMethod make() {
        return null;
    }

    public AggregationStateKey getAggregationStateKey(boolean isMatchRecognize) {
        return handlerPlugin.getAggregationStateUniqueKey();
    }

    public AggregationStateFactoryForge getAggregationStateFactory(boolean isMatchRecognize) {
        return aggregationFactoryFactory.makePlugInAccess(statementExtensionSvcContext, this);
    }

    public AggregationAccessorForge getAccessorForge() {
        return handlerPlugin.getAccessorForge();
    }

    public Class getResultType() {
        obtainReturnType();
        return EPTypeHelper.getNormalizedClass(returnType);
    }

    public PlugInAggregationMultiFunctionHandler getHandlerPlugin() {
        return handlerPlugin;
    }

    public Class getComponentTypeCollection() {
        obtainReturnType();
        return EPTypeHelper.getClassMultiValued(returnType);
    }

    public EventType getEventTypeSingle() {
        obtainReturnType();
        return EPTypeHelper.getEventTypeSingleValued(returnType);
    }

    public EventType getEventTypeCollection() {
        obtainReturnType();
        return EPTypeHelper.getEventTypeMultiValued(returnType);
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    private void obtainReturnType() {
        if (returnType == null) {
            returnType = handlerPlugin.getReturnType();
        }
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        ExprPlugInAggMultiFunctionNodeFactory that = (ExprPlugInAggMultiFunctionNodeFactory) intoTableAgg;
        if (!getAggregationStateKey(false).equals(that.getAggregationStateKey(false))) {
            throw new ExprValidationException("Mismatched state key");
        }
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        PlugInAggregationMultiFunctionAgentContext ctx = new PlugInAggregationMultiFunctionAgentContext(parent.getChildNodes(), parent.getOptionalFilter());
        return handlerPlugin.getAggregationAgent(ctx);
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return null;
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        // handled by AggregationMethodFactoryPlugIn
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
    }
}
