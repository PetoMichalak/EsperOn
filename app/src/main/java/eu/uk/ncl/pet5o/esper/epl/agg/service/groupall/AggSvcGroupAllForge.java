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
package eu.uk.ncl.pet5o.esper.epl.agg.service.groupall;

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenTypedParam;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.core.service.StatementContext;
import eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationCodegenRowLevelDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationRowStateForgeDesc;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceCodegenUtil;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationServiceFactoryForge;

import java.util.List;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constant;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantFalse;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.constantNull;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.ref;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.CLASSNAME_AGGREGATIONROW_TOP;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.CLASSNAME_AGGREGATIONSERVICE;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.REF_AGGVISITOR;
import static eu.uk.ncl.pet5o.esper.epl.agg.codegen.AggregationServiceCodegenNames.REF_COLUMN;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.*;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EPS;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_EXPREVALCONTEXT;
import static eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenNames.REF_ISNEWDATA;

/**
 * Aggregation service for use when only first/last/window aggregation functions are used an none other.
 */
public class AggSvcGroupAllForge implements AggregationServiceFactoryForge {
    private final static CodegenExpressionRef REF_ROW = new CodegenExpressionRef("row");

    protected final AggregationRowStateForgeDesc rowStateDesc;
    protected final boolean isJoin;

    public AggSvcGroupAllForge(AggregationRowStateForgeDesc rowStateDesc, boolean isJoin) {
        this.rowStateDesc = rowStateDesc;
        this.isJoin = isJoin;
    }

    public AggregationServiceFactory getAggregationServiceFactory(StatementContext stmtContext, boolean isFireAndForget) {
        return new AggSvcGroupAllFactory(rowStateDesc.toEval(stmtContext, isFireAndForget), isJoin);
    }

    public AggregationCodegenRowLevelDesc getRowLevelDesc() {
        return AggregationCodegenRowLevelDesc.fromTopOnly(rowStateDesc);
    }

    public void makeServiceCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(newInstanceInnerClass(CLASSNAME_AGGREGATIONSERVICE, ref("o")));
    }

    public void rowCtorCodegen(CodegenClassScope classScope, CodegenCtor rowCtor, List<CodegenTypedParam> rowMembers, CodegenNamedMethods namedMethods) {
        AggregationServiceCodegenUtil.generateRefCount(false, namedMethods, rowCtor, rowMembers, classScope);
    }

    public void ctorCodegen(CodegenCtor ctor, List<CodegenTypedParam> explicitMembers, CodegenClassScope classScope) {
        explicitMembers.add(new CodegenTypedParam(CLASSNAME_AGGREGATIONROW_TOP, REF_ROW.getRef()));
        ctor.getBlock().assignRef(REF_ROW, newInstanceInnerClass(CLASSNAME_AGGREGATIONROW_TOP, ref("o")));
    }

    public void getValueCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROW, "getValue", REF_COLUMN, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }

    public void getEventBeanCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROW, "getEventBean", REF_COLUMN, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }

    public void applyEnterCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().exprDotMethod(REF_ROW, "applyEnter", REF_EPS, REF_EXPREVALCONTEXT);
    }

    public void applyLeaveCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().exprDotMethod(REF_ROW, "applyLeave", REF_EPS, REF_EXPREVALCONTEXT);
    }

    public void stopMethodCodegen(AggregationServiceFactoryForge forge, CodegenMethodNode method) {
        // no code
    }

    public void setRemovedCallbackCodegen(CodegenMethodNode method) {
        // no code
    }

    public void setCurrentAccessCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        // no code
    }

    public void clearResultsCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().exprDotMethod(REF_ROW, "clear");
    }

    public void getCollectionScalarCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROW, "getCollectionScalar", REF_COLUMN, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }

    public void getCollectionOfEventsCodegen(CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodReturn(exprDotMethod(REF_ROW, "getCollectionOfEvents", REF_COLUMN, REF_EPS, REF_ISNEWDATA, REF_EXPREVALCONTEXT));
    }

    public void acceptCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().exprDotMethod(REF_AGGVISITOR, "visitAggregations", constant(1), REF_ROW);
    }

    public void getGroupKeysCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantNull());
    }

    public void getGroupKeyCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantNull());
    }

    public void acceptGroupDetailCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        // not implemented
    }

    public void isGroupedCodegen(CodegenMethodNode method, CodegenClassScope classScope) {
        method.getBlock().methodReturn(constantFalse());
    }
}
