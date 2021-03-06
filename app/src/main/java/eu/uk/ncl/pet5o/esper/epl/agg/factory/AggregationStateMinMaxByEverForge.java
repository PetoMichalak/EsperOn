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
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateMinMaxByEverSpec;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateMinMaxByEverSpecForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateSortedCodegen;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggMultiFunctionSortedMinMaxByNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;

public class AggregationStateMinMaxByEverForge implements AggregationStateFactoryForge, AggregationStateSortedCodegen {

    protected final ExprAggMultiFunctionSortedMinMaxByNode expr;
    protected final AggregationStateMinMaxByEverSpecForge spec;

    public AggregationStateMinMaxByEverForge(ExprAggMultiFunctionSortedMinMaxByNode expr, AggregationStateMinMaxByEverSpecForge spec) {
        this.expr = expr;
        this.spec = spec;
    }

    public AggregationStateFactory makeFactory(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        AggregationStateMinMaxByEverSpec def = spec.toEvaluators(engineImportService, isFireAndForget, statementName);
        return new AggregationStateMinMaxByEverFactory(expr, def);
    }

    public void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope) {
        AggregationStateMinMaxByEverFactory.rowMemberCodegen(this, stateNumber, ctor, membersColumnized, classScope);
    }

    public void applyEnterCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateMinMaxByEverFactory.applyEnterCodegen(this, stateNumber, method, symbols, classScope, namedMethods);
    }

    public void applyLeaveCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateMinMaxByEverFactory.applyLeaveCodege();
    }

    public void clearCodegen(int stateNumber, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        AggregationStateMinMaxByEverFactory.clearCodegen(this, stateNumber, method);
    }

    public CodegenExpression getLastValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode method) {
        return AggregationStateMinMaxByEverFactory.getLastValueCodegen(this, slot, classScope, method);
    }

    public CodegenExpression getFirstValueCodegen(int slot, CodegenClassScope classScope, CodegenMethodNode method) {
        return AggregationStateMinMaxByEverFactory.getFirstValueCodegen(this, slot, classScope, method);
    }

    public AggregationStateMinMaxByEverSpecForge getSpec() {
        return spec;
    }
}
