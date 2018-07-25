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

import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;
import eu.uk.ncl.pet5o.esper.plugin.PlugInAggregationMultiFunctionCodegenType;

public interface AggregationAgentForge {
    AggregationAgent makeAgent(EngineImportService engineImportService, boolean isFireAndForget, String statementName);

    default PlugInAggregationMultiFunctionCodegenType getPluginCodegenType() {
        return PlugInAggregationMultiFunctionCodegenType.CODEGEN_NONE;
    }

    ExprForge getOptionalFilter();

    default CodegenExpression applyEnterCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return null;
    }

    default CodegenExpression applyLeaveCodegen(CodegenMethodScope parent, AggregationAgentCodegenSymbols symbols, CodegenClassScope classScope) {
        return null;
    }
}
