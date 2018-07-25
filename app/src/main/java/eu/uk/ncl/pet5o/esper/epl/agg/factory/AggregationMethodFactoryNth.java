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
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorNth;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorNthFilter;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprMethodAggUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprNthAggNode;

public class AggregationMethodFactoryNth implements AggregationMethodFactory {
    protected final ExprNthAggNode parent;
    protected final Class childType;
    protected final int size;

    public AggregationMethodFactoryNth(ExprNthAggNode parent, Class childType, int size) {
        this.parent = parent;
        this.childType = childType;
        this.size = size;
    }

    public boolean isAccessAggregation() {
        return false;
    }

    public Class getResultType() {
        return childType;
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
        AggregationMethod method;
        if (parent.getOptionalFilter() != null) {
            method = new AggregatorNthFilter(size + 1);
        } else {
            method = new AggregatorNth(size + 1);
        }
        if (!parent.isDistinct()) {
            return method;
        }
        return AggregationMethodFactoryUtil.makeDistinctAggregator(method, false);
    }

    public ExprNthAggNode getParent() {
        return parent;
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        AggregationMethodFactoryNth that = (AggregationMethodFactoryNth) intoTableAgg;
        AggregationValidationUtil.validateAggregationInputType(childType, that.childType);
        if (size != that.size) {
            throw new ExprValidationException("The size is " +
                    size +
                    " and provided is " +
                    that.size);
        }
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return null;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return ExprMethodAggUtil.getDefaultForges(parent.getPositionalParams(), join, typesPerStream);
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorNth.rowMemberCodegen(this, column, ctor, membersColumnized);
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorNth.applyEnterCodegen(this, column, method, symbols, forges, classScope);
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorNth.applyLeaveCodegen(this, column, method, symbols, forges, classScope);
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorNth.clearCodegen(this, column, method);
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorNth.getValueCodegen(this, column, method);
    }

    public int getSizeOfBuf() {
        return size + 1;
    }
}
