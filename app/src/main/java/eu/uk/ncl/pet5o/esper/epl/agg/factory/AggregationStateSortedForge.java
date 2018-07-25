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
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateSortedCodegen;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateSortedSpec;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateSortedSpecForge;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggMultiFunctionSortedMinMaxByNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

public class AggregationStateSortedForge implements AggregationStateFactoryForge, AggregationStateSortedCodegen {

    protected final ExprAggMultiFunctionSortedMinMaxByNode expr;
    protected final AggregationStateSortedSpecForge spec;

    public AggregationStateSortedForge(ExprAggMultiFunctionSortedMinMaxByNode expr, AggregationStateSortedSpecForge spec) {
        this.expr = expr;
        this.spec = spec;
    }

    public AggregationStateFactory makeFactory(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        AggregationStateSortedSpec def = spec.toEvaluator(engineImportService, isFireAndForget, statementName);
        return new AggregationStateSortedFactory(expr, def);
    }

    public void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope) {
        AggregationStateSortedFactory.rowMemberCodegen(this, stateNumber, ctor, membersColumnized, classScope);
    }

    public void applyEnterCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateSortedFactory.applyEnterCodegen(this, stateNumber, method, symbols, classScope, namedMethods);
    }

    public void applyLeaveCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateSortedFactory.applyLeaveCodegen(this, stateNumber, method, symbols, classScope, namedMethods);
    }

    public void clearCodegen(int stateNumber, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateSortedFactory.clearCodegen(this, stateNumber, method);
    }

    public CodegenExpression sizeCodegen(int slot) {
        return AggregationStateSortedFactory.sizeCodegen(this, slot);
    }

    public CodegenExpression getReverseIteratorCodegen(int slot) {
        return AggregationStateSortedFactory.getReverseIteratorCodegen(this, slot);
    }

    public CodegenExpression iteratorCodegen(int slot) {
        return AggregationStateSortedFactory.iteratorCodegen(this, slot);
    }

    public AggregationStateSortedSpecForge getSpec() {
        return spec;
    }

    public CodegenExpression getLastValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parent) {
        return AggregationStateSortedFactory.getLastValueCodegen(this, slot, classScope, parent);
    }

    public CodegenExpression getFirstValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode parent) {
        return AggregationStateSortedFactory.getFirstValueCodegen(this, slot, classScope, parent);
    }

    public ExprAggMultiFunctionSortedMinMaxByNode getExpr() {
        return expr;
    }

    public CodegenExpression collectionReadOnlyCodegen(int column) {
        return AggregationStateSortedFactory.collectionReadOnlyCodegen(column);
    }
}
