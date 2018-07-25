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
import eu.uk.ncl.pet5o.esper.client.util.CountMinSketchTopK;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMembersColumnized;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodNode;
import eu.uk.ncl.pet5o.esper.codegen.core.CodegenCtor;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAccessorForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationAgentForge;
import eu.uk.ncl.pet5o.esper.epl.agg.access.AggregationStateKey;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationMethodFactory;
import eu.uk.ncl.pet5o.esper.epl.agg.service.common.AggregationStateFactoryForge;
import eu.uk.ncl.pet5o.esper.epl.approx.*;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprForgeCodegenSymbol;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;

public class ExprAggCountMinSketchNodeFactoryUse extends ExprAggCountMinSketchNodeFactoryBase {
    private final ExprForge addOrFrequencyEvaluator;
    private final Class addOrFrequencyEvaluatorReturnType;

    public ExprAggCountMinSketchNodeFactoryUse(ExprAggCountMinSketchNode parent, ExprForge addOrFrequencyEvaluator, Class addOrFrequencyEvaluatorReturnType) {
        super(parent);
        this.addOrFrequencyEvaluator = addOrFrequencyEvaluator;
        this.addOrFrequencyEvaluatorReturnType = addOrFrequencyEvaluatorReturnType;
    }

    public Class getResultType() {
        if (parent.getAggType() == CountMinSketchAggType.ADD) {
            return null;
        } else if (parent.getAggType() == CountMinSketchAggType.FREQ) {
            return Long.class;
        } else if (parent.getAggType() == CountMinSketchAggType.TOPK) {
            return CountMinSketchTopK[].class;
        } else {
            throw new UnsupportedOperationException("Unrecognized code " + parent.getAggType());
        }
    }

    public AggregationStateKey getAggregationStateKey(boolean isMatchRecognize) {
        throw new UnsupportedOperationException();
    }

    public AggregationStateFactoryForge getAggregationStateFactory(boolean isMatchRecognize) {
        throw new UnsupportedOperationException();
    }

    public AggregationAccessorForge getAccessorForge() {
        if (parent.getAggType() == CountMinSketchAggType.ADD) {
            // modifications handled by agent
            return CountMinSketchAggAccessorDefault.INSTANCE;
        } else if (parent.getAggType() == CountMinSketchAggType.FREQ) {
            return new CountMinSketchAggAccessorFrequencyForge(addOrFrequencyEvaluator);
        } else if (parent.getAggType() == CountMinSketchAggType.TOPK) {
            return CountMinSketchAggAccessorTopk.INSTANCE;
        }
        throw new IllegalStateException("Aggregation accessor not available for this function '" + parent.getAggregationFunctionName() + "'");
    }

    public AggregationAgentForge getAggregationStateAgent(EngineImportService engineImportService, String statementName) {
        if (parent.getAggType() == CountMinSketchAggType.ADD) {
            return new CountMinSketchAggAgentAddForge(addOrFrequencyEvaluator, parent.getOptionalFilter() == null ? null : parent.getOptionalFilter().getForge());
        }
        throw new IllegalStateException("Aggregation agent not available for this function '" + parent.getAggregationFunctionName() + "'");
    }

    public void validateIntoTableCompatible(AggregationMethodFactory intoTableAgg) throws ExprValidationException {
        throw new IllegalStateException("Aggregation not compatible");
    }

    public Class getAddOrFrequencyEvaluatorReturnType() {
        return addOrFrequencyEvaluatorReturnType;
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
