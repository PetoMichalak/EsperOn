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
package eu.uk.ncl.pet5o.esper.epl.expression.accessagg;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.factory.AggregationStateFactoryCountMinSketch;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchAggAccessorDefault;
import eu.uk.ncl.pet5o.esper.epl.approx.CountMinSketchAggType;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.util.Arrays;

public class ExprAggCountMinSketchNodeFactoryState extends ExprAggCountMinSketchNodeFactoryBase {
    private final AggregationStateFactoryCountMinSketch stateFactory;

    public ExprAggCountMinSketchNodeFactoryState(AggregationStateFactoryCountMinSketch stateFactory) {
        super(stateFactory.getParent());
        this.stateFactory = stateFactory;
    }

    public Class getResultType() {
        return null;
    }

    public AggregationAccessorForge getAccessorForge() {
        return CountMinSketchAggAccessorDefault.INSTANCE;
    }

    public AggregationStateFactoryForge getAggregationStateFactory(boolean isMatchRecognize) {
        // For match-recognize we don't allow
        if (isMatchRecognize) {
            throw new IllegalStateException("Count-min-sketch is not supported for match-recognize");
        }
        return stateFactory;
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        throw new UnsupportedOperationException();
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        ExprAggCountMinSketchNodeFactoryUse use = (ExprAggCountMinSketchNodeFactoryUse) intoTableAgg;
        CountMinSketchAggType aggType = use.getParent().getAggType();
        if (aggType == CountMinSketchAggType.FREQ || aggType == CountMinSketchAggType.ADD) {
            Class clazz = use.getAddOrFrequencyEvaluatorReturnType();
            boolean foundMatch = false;
            for (Class allowed : stateFactory.getSpecification().getAgent().getAcceptableValueTypes()) {
                if (JavaClassHelper.isSubclassOrImplementsInterface(clazz, allowed)) {
                    foundMatch = true;
                }
            }
            if (!foundMatch) {
                throw new ExprValidationException("Mismatching parameter return type, expected any of " + Arrays.toString(stateFactory.getSpecification().getAgent().getAcceptableValueTypes()) + " but received " + JavaClassHelper.getClassNameFullyQualPretty(clazz));
            }
        }
    }

    public ExprForge[] getMethodAggregationForge(boolean join, EventType[] typesPerStream) throws ExprValidationException {
        return null;
    }

    public void rowMemberCodegen(int column, CodegenCtor ctor, CodegenMembersColumnized membersColumnized, ExprForge[] forges, CodegenClassScope classScope) {
    }

    public void applyEnterCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
    }

    public void applyLeaveCodegen(int column, CodegenMethodNode method, ExprForgeCodegenSymbol symbols, ExprForge[] forges, CodegenClassScope classScope) {
    }

    public void clearCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
    }

    public void getValueCodegen(int column, CodegenMethodNode method, CodegenClassScope classScope) {
    }
}
