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
import eu.uk.ncl.pet5o.esper.client.hook.*;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.collection.RefCountedSet;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateKey;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregationMethod;
import eu.uk.ncl.pet5o.esper.epl.agg.aggregator.AggregatorCodegenUtil;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationValidationUtil;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.baseagg.ExprAggregateNodeBase;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprMethodAggUtil;
import eu.uk.ncl.pet5o.esper.epl.expression.methodagg.ExprPlugInAggNode;

import static eu.uk.ncl.pet5o.esper.client.hook.AggregationFunctionFactoryCodegenType.*;
import static eu.uk.ncl.pet5o.esper.client.hook.AggregationFunctionFactoryCodegenType.CODEGEN_MANAGED;
import static eu.uk.ncl.pet5o.esper.client.hook.AggregationFunctionFactoryCodegenType.CODEGEN_NONE;
import static eu.uk.ncl.pet5o.esper.client.hook.AggregationFunctionFactoryCodegenType.CODEGEN_UNMANAGED;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newArrayByLength;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.not;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.refCol;

public class AggregationMethodFactoryPlugIn implements AggregationMethodFactory {
    protected final ExprPlugInAggNode parent;
    protected final AggregationFunctionFactory aggregationFunctionFactory;
    protected final Class aggregatedValueType;

    public AggregationMethodFactoryPlugIn(ExprPlugInAggNode parent, AggregationFunctionFactory aggregationFunctionFactory, Class aggregatedValueType) {
        this.parent = parent;
        this.aggregationFunctionFactory = aggregationFunctionFactory;
        this.aggregatedValueType = aggregatedValueType;
    }

    public Class getResultType() {
        return aggregationFunctionFactory.getValueType();
    }

    public boolean isAccessAggregation() {
        return false;
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
        AggregationMethod method = aggregationFunctionFactory.newAggregator();
        if (!parent.isDistinct()) {
            return method;
        }
        return AggregationMethodFactoryUtil.makeDistinctAggregator(method, false);
    }

    public ExprAggregateNodeBase getAggregationExpression() {
        return parent;
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        AggregationValidationUtil.validateAggregationType(this, intoTableAgg);
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        return null;
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return ExprMethodAggUtil.getDefaultForges(parent.getPositionalParams(), join, typesPerStream);
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
        AggregationFunctionFactoryCodegenRowMemberContext ctx = new AggregationFunctionFactoryCodegenRowMemberContext(parent, column, ctor, membersColumnized);
        CodegenMember factory = classScope.makeAddMember(AggregationFunctionFactory.class, aggregationFunctionFactory);
        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_NONE) {
            membersColumnized.addMember(column, AggregationMethod.class, "method");
            ctor.getBlock().assignRef(refCol("method", column), exprDotMethod(member(factory.getMemberId()), "newAggregator"));
            return;
        }

        aggregationFunctionFactory.rowMemberCodegen(ctx);
        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_MANAGED && forges.length != 0) {
            membersColumnized.addMember(column, RefCountedSet.class, "distinctSet");
            ctor.getBlock().assignRef(refCol("distinctSet", column), newInstance(RefCountedSet.class));
        }
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        applyCodegen(true, column, method, symbols, forges, classScope);
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        applyCodegen(false, column, method, symbols, forges, classScope);
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_NONE) {
            method.getBlock().exprDotMethod(refCol("method", column), "clear");
            return;
        }
        AggregationFunctionFactoryCodegenRowClearContext ctx = new AggregationFunctionFactoryCodegenRowClearContext(parent, column, method, classScope);
        aggregationFunctionFactory.clearCodegen(ctx);
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_NONE) {
            method.getBlock().methodReturn(exprDotMethod(refCol("method", column), "getValue"));
            return;
        }
        AggregationFunctionFactoryCodegenRowGetValueContext ctx = new AggregationFunctionFactoryCodegenRowGetValueContext(parent, column, method, classScope);
        aggregationFunctionFactory.getValueCodegen(ctx);
    }

    private void applyCodegen(boolean enter, int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_UNMANAGED) {
            AggregationFunctionFactoryCodegenRowApplyContextUnmanaged ctx = new AggregationFunctionFactoryCodegenRowApplyContextUnmanaged(parent, column, method, symbols, forges, classScope);
            if (enter) {
                aggregationFunctionFactory.applyEnterCodegenUnmanaged(ctx);
            } else {
                aggregationFunctionFactory.applyLeaveCodegenUnmanaged(ctx);
            }
            return;
        }

        if (parent.getOptionalFilter() != null) {
            AggregatorCodegenUtil.prefixWithFilterCheck(parent.getOptionalFilter().getForge(), method, symbols, classScope);
        }

        CodegenMethodNode childMethod = method.makeChild(void.class, AggregationMethodFactoryPlugIn.class, classScope).addParam(Object.class, "value");
        if (forges.length == 0) {
            method.getBlock().localMethod(childMethod, constantNull());
        } else {
            CodegenExpression expr = forges[0].evaluateCodegen(long.class, method, symbols, classScope);
            Class type = forges[0].getEvaluationType();
            method.getBlock().declareVar(type, "value", expr);
            if (parent.isDistinct()) {
                method.getBlock().ifCondition(not(exprDotMethod(refCol("distinctSet", column), enter ? "add" : "remove", ref("value")))).blockReturnNoValue();
            }

            if (forges.length == 1) {
                method.getBlock().localMethod(childMethod, ref("value"));
            } else {
                method.getBlock().declareVar(Object[].class, "params", newArrayByLength(Object.class, constant(forges.length)))
                        .assignArrayElement(ref("params"), constant(0), ref("value"));
                for (int i = 1; i < forges.length; i++) {
                    method.getBlock().assignArrayElement(ref("params"), constant(i), forges[i].evaluateCodegen(Object.class, method, symbols, classScope));
                }
                method.getBlock().localMethod(childMethod, ref("params"));
            }
        }

        if (aggregationFunctionFactory.getCodegenType() == CODEGEN_NONE) {
            childMethod.getBlock().exprDotMethod(refCol("method", column), enter ? "enter" : "leave", ref("value"));
        } else {
            AggregationFunctionFactoryCodegenRowApplyContextManaged ctx = new AggregationFunctionFactoryCodegenRowApplyContextManaged(parent, column, childMethod, classScope);
            if (enter) {
                aggregationFunctionFactory.applyEnterCodegenManaged(ctx);
            } else {
                aggregationFunctionFactory.applyLeaveCodegenManaged(ctx);
            }
        }
    }
}
