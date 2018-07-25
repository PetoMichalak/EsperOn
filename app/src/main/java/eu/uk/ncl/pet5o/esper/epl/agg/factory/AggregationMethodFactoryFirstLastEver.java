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
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorFirstEver;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorLastEver;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprFirstLastEverNode;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprMethodAggUtil;

public class AggregationMethodFactoryFirstLastEver implements AggregationMethodFactory {
    protected final ExprFirstLastEverNode parent;
    protected final Class childType;

    public AggregationMethodFactoryFirstLastEver(ExprFirstLastEverNode parent, Class childType) {
        this.parent = parent;
        this.childType = childType;
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
        if (parent.isFirst()) {
            return AggregationMethodFactoryUtil.makeFirstEver(parent.hasFilter());
        }
        return AggregationMethodFactoryUtil.makeLastEver(parent.hasFilter());
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        AggregationMethodFactoryFirstLastEver that = (AggregationMethodFactoryFirstLastEver) intoTableAgg;
        AggregationValidationUtil.validateAggregationInputType(childType, that.childType);
        AggregationValidationUtil.validateAggregationFilter(parent.hasFilter(), that.parent.hasFilter());
        if (that.parent.isFirst() != parent.isFirst()) {
            throw new ExprValidationException("The aggregation declares " +
                    (parent.isFirst() ? "firstever" : "lastever") +
                    " and provided is " +
                    (that.parent.isFirst() ? "firstever" : "lastever"));
        }
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return null;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return ExprMethodAggUtil.getDefaultForges(parent.getPositionalParams(), join, typesPerStream);
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        if (parent.isFirst()) {
            AggregatorFirstEver.rowMemberCodegen(column, ctor, membersColumnized);
        } else {
            AggregatorLastEver.rowMemberCodegen(column, ctor, membersColumnized);
        }
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        if (parent.isFirst()) {
            AggregatorFirstEver.applyEnterCodegen(parent.hasFilter(), column, method, symbols, forges, classScope);
        } else {
            AggregatorLastEver.applyEnterCodegen(parent.hasFilter(), column, method, symbols, forges, classScope);
        }
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        // no code
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        if (parent.isFirst()) {
            AggregatorFirstEver.clearCodegen(column, method);
        } else {
            AggregatorLastEver.clearCodegen(column, method);
        }
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        if (parent.isFirst()) {
            AggregatorFirstEver.getValueCodegen(column, method);
        } else {
            AggregatorLastEver.getValueCodegen(column, method);
        }
    }
}

