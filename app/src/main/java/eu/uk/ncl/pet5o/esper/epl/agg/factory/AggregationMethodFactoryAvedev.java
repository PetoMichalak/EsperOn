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
package eu.uk.ncl.pet5o.esper.epl.agg.factory;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateKey;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorAvedev;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorAvedevFilter;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprAvedevNode;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprMethodAggUtil;

public class AggregationMethodFactoryAvedev implements AggregationMethodFactory {
    protected final ExprAvedevNode parent;
    protected final Class aggregatedValueType;
    protected final ExprNode[] positionalParameters;

    public AggregationMethodFactoryAvedev(ExprAvedevNode parent, Class aggregatedValueType, ExprNode[] positionalParameters) {
        this.parent = parent;
        this.aggregatedValueType = aggregatedValueType;
        this.positionalParameters = positionalParameters;
    }

    public boolean isAccessAggregation() {
        return false;
    }

    public Class getResultType() {
        return Double.class;
    }

    public AggregationStateKey getAggregationStateKey(boolean isMatchRecognize) {
        throw new IllegalStateException("Not an access aggregation function");
    }

    public AggregationStateFactoryForge getAggregationStateFactory(boolean isMatchRecognize) {
        throw new IllegalStateException("Not an access aggregation function");
    }

    public AggregationAccessorForge getAccessorForge() {
        throw new IllegalStateException("Not an access aggregation function");
    }

    public AggregationMethod make() {
        AggregationMethod method = makeAvedevAggregator(parent.isHasFilter());
        if (!parent.isDistinct()) {
            return method;
        }
        return AggregationMethodFactoryUtil.makeDistinctAggregator(method, parent.isHasFilter());
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        AggregationMethodFactoryAvedev that = (AggregationMethodFactoryAvedev) intoTableAgg;
        AggregationValidationUtil.validateAggregationInputType(aggregatedValueType, that.aggregatedValueType);
        AggregationValidationUtil.validateAggregationFilter(parent.isHasFilter(), that.parent.isHasFilter());
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return null;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return ExprMethodAggUtil.getDefaultForges(parent.getPositionalParams(), join, typesPerStream);
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorAvedev.rowMemberCodegen(parent.isDistinct(), column, ctor, membersColumnized);
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorAvedev.applyEnterCodegen(parent.isDistinct(), parent.isHasFilter(), column, method, symbols, forges, classScope);
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorAvedev.applyLeaveCodegen(parent.isDistinct(), parent.isHasFilter(), column, method, symbols, forges, classScope);
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorAvedev.clearCodegen(parent.isDistinct(), column, method);
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorAvedev.getValueCodegen(column, method);
    }

    private AggregationMethod makeAvedevAggregator(boolean hasFilter) {
        if (!hasFilter) {
            return new AggregatorAvedev();
        } else {
            return new AggregatorAvedevFilter();
        }
    }
}
