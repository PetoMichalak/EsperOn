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
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.*;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprCountEverNode;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprMethodAggUtil;

public class AggregationMethodFactoryCountEver implements AggregationMethodFactory {
    protected final ExprCountEverNode parent;
    protected final boolean ignoreNulls;

    public AggregationMethodFactoryCountEver(ExprCountEverNode parent, boolean ignoreNulls) {
        this.parent = parent;
        this.ignoreNulls = ignoreNulls;
    }

    public boolean isAccessAggregation() {
        return false;
    }

    public Class getResultType() {
        return long.class;
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
        return makeCountEverValueAggregator(parent.hasFilter(), ignoreNulls);
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
        AggregationMethodFactoryCountEver that = (AggregationMethodFactoryCountEver) intoTableAgg;
        if (that.ignoreNulls != ignoreNulls) {
            throw new ExprValidationException("The aggregation declares " +
                    (ignoreNulls ? "ignore-nulls" : "no-ignore-nulls") +
                    " and provided is " +
                    (that.ignoreNulls ? "ignore-nulls" : "no-ignore-nulls"));
        }
        AggregationValidationUtil.validateAggregationFilter(parent.hasFilter(), that.parent.hasFilter());
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return null;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return ExprMethodAggUtil.getDefaultForges(parent.getPositionalParams(), join, typesPerStream);
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorCount.rowMemberCodegen(parent.isDistinct(), column, ctor, membersColumnized);
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        AggregatorCount.applyEnterCodegen(parent.isDistinct(), parent.hasFilter(), column, method, forges, symbols, classScope);
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        // no leave
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorCount.clearCodegen(parent.isDistinct(), column, method);
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        AggregatorCount.getValueCodegen(column, method);
    }

    private AggregationMethod makeCountEverValueAggregator(boolean hasFilter, boolean ignoreNulls) {
        if (!hasFilter) {
            if (ignoreNulls) {
                return new AggregatorCountEverNonNull();
            }
            return new AggregatorCountEver();
        } else {
            if (ignoreNulls) {
                return new AggregatorCountEverNonNullFilter();
            }
            return new AggregatorCountEverFilter();
        }
    }
}

