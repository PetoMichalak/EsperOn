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
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationServicePassThru;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationState;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchAggState;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchSpec;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchState;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.accessagg.ExprAggCountMinSketchNode;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;

public class AggregationStateFactoryCountMinSketch implements AggregationStateFactory, AggregationStateFactoryForge {
    protected final ExprAggCountMinSketchNode parent;
    protected final CountMinSketchSpec specification;

    public AggregationStateFactoryCountMinSketch(ExprAggCountMinSketchNode parent, CountMinSketchSpec specification) {
        this.parent = parent;
        this.specification = specification;
    }

    public AggregationStateFactory makeFactory(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        return this;
    }

    public void rowMemberCodegen(int stateNumber, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, CodegenClassScope classScope) {
    }

    public void applyEnterCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodThrowUnsupported();
    }

    public void applyLeaveCodegen(int stateNumber, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodThrowUnsupported();
    }

    public void clearCodegen(int stateNumber, CodegenMethodNode method, CodegenClassScope classScope, CodegenNamedMethods namedMethods) {
        method.getBlock().methodThrowUnsupported();
    }

    public AggregationState createAccess(int agentInstanceId, boolean join, Object groupKey, AggregationServicePassThru passThru) {
        return new CountMinSketchAggState(CountMinSketchState.makeState(specification), specification.getAgent());
    }

    public ExprNode getAggregationExpression() {
        return parent;
    }

    public CountMinSketchSpec getSpecification() {
        return specification;
    }

    public ExprAggCountMinSketchNode getParent() {
        return parent;
    }
}
