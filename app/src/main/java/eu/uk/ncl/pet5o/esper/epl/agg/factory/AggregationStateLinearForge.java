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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenNamedMethods;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionRef;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggMultiFunctionLinearAccessNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

public class AggregationStateLinearForge implements AggregationStateFactoryForge {

    protected final ExprAggMultiFunctionLinearAccessNode expr;
    protected final int streamNum;
    protected final ExprForge optionalFilter;
    protected final boolean join;

    public AggregationStateLinearForge(ExprAggMultiFunctionLinearAccessNode expr, int streamNum, ExprForge optionalFilter, boolean join) {
        this.expr = expr;
        this.streamNum = streamNum;
        this.optionalFilter = optionalFilter;
        this.join = join;
    }

    public AggregationStateFactory makeFactory(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator optionalFilterEval = optionalFilter == null ? null : ExprNodeCompiler.allocateEvaluator(optionalFilter, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new AggregationStateLinearFactory(expr, streamNum, optionalFilterEval);
    }

    public void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope) {
        AggregationStateLinearFactory.rowMemberCodegen(this, stateNumber, ctor, membersColumnized, classScope);
    }

    public void applyEnterCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateLinearFactory.applyEnterCodegen(this, stateNumber, method, symbols, classScope);
    }

    public void applyLeaveCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateLinearFactory.applyLeaveCodegen(this, stateNumber, method, symbols, classScope);
    }

    public void clearCodegen(int stateNumber, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateLinearFactory.clearCodegen(this, stateNumber, method);
    }

    public CodegenExpression getFirstValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod) {
        return AggregationStateLinearFactory.getFirstValueCodegen(this, slot, classScope, parentMethod);
    }

    public CodegenExpression sizeCodegen(int slot) {
        return AggregationStateLinearFactory.sizeCodegen(this, slot);
    }

    public CodegenExpression iteratorCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parent, CodegenNamedMethods namedMethods) {
        return AggregationStateLinearFactory.iteratorCodegen(this, slot, classScope, parent, namedMethods);
    }

    public CodegenExpression getLastValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parentMethod, CodegenNamedMethods namedMethods) {
        return AggregationStateLinearFactory.getLastValueCodegen(this, slot, classScope, parentMethod, namedMethods);
    }

    public CodegenExpression getFirstNthValueCodegen(CodegenExpressionRef index, int slot, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        return AggregationStateLinearFactory.getFirstNthValueCodegen(this, index, slot, classScope, method, namedMethods);
    }

    public CodegenExpression getLastNthValueCodegen(CodegenExpressionRef index, int slot, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        return AggregationStateLinearFactory.getLastNthValueCodegen(this, index, slot, classScope, method, namedMethods);
    }

    public CodegenExpression collectionReadOnlyCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        return AggregationStateLinearFactory.collectionReadOnlyCodegen(this, column, method, classScope, namedMethods);
    }

    public int getStreamNum() {
        return streamNum;
    }

    public ExprForge getOptionalFilter() {
        return optionalFilter;
    }

    public boolean isJoin() {
        return join;
    }
}
