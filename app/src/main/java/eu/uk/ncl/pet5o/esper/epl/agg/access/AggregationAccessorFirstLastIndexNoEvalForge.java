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
package eu.uk.ncl.pet5o.esper.epl.agg.access;

import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.codegen.ExprNodeCompiler;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

public class AggregationAccessorFirstLastIndexNoEvalForge implements AggregationAccessorForge {
    private final ExprForge indexNode;
    private final int constant;
    private final boolean isFirst;

    public AggregationAccessorFirstLastIndexNoEvalForge(ExprForge indexNode, int constant, boolean first) {
        this.indexNode = indexNode;
        this.constant = constant;
        isFirst = first;
    }

    public AggregationAccessor getAccessor(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator index = indexNode == null ? null : ExprNodeCompiler.allocateEvaluator(indexNode, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new AggregationAccessorFirstLastIndexNoEval(index, constant, isFirst);
    }

    public PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_ALL; // not currently applicable as table-related only
    }

    public void getValueCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableEventsCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableEventCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }

    public void getEnumerableScalarCodegen(AggregationAccessorForgeGetCodegenContext context) {
        throw new UnsupportedOperationException();
    }
}
