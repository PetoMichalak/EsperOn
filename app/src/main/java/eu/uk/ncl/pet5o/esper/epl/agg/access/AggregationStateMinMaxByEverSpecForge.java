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
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;

import java.util.Comparator;

public class AggregationStateMinMaxByEverSpecForge {
    protected final int streamId;
    private final ExprNode[] criteria;
    private final boolean max;
    private final Comparator<Object> comparator;
    private Object criteriaKeyBinding;
    private final ExprForge optionalFilter;

    public AggregationStateMinMaxByEverSpecForge(int streamId, ExprNode[] criteria, boolean max, Comparator<Object> comparator, Object criteriaKeyBinding, ExprForge optionalFilter) {
        this.streamId = streamId;
        this.criteria = criteria;
        this.max = max;
        this.criteriaKeyBinding = criteriaKeyBinding;
        this.optionalFilter = optionalFilter;
        this.comparator = comparator;
    }

    public int getStreamId() {
        return streamId;
    }

    public boolean isMax() {
        return max;
    }

    public Object getCriteriaKeyBinding() {
        return criteriaKeyBinding;
    }

    public void setCriteriaKeyBinding(Object criteriaKeyBinding) {
        this.criteriaKeyBinding = criteriaKeyBinding;
    }

    public ExprForge getOptionalFilter() {
        return optionalFilter;
    }

    public ExprNode[] getCriteria() {
        return criteria;
    }

    public Comparator<Object> getComparator() {
        return comparator;
    }

    public AggregationStateMinMaxByEverSpec toEvaluators(EngineImportService engineImportService, boolean isFireAndForget, String statementName) {
        ExprEvaluator[] criteriaExpressionEvals = ExprNodeUtilityRich.getEvaluatorsMayCompile(criteria, engineImportService, this.getClass(), isFireAndForget, statementName);
        Class[] criteriaTypes = ExprNodeUtilityCore.getExprResultTypes(criteria);
        ExprEvaluator optionalFilterEval = optionalFilter == null ? null : ExprNodeCompiler.allocateEvaluator(optionalFilter, engineImportService, this.getClass(), isFireAndForget, statementName);
        return new AggregationStateMinMaxByEverSpec(streamId, criteriaExpressionEvals, criteriaTypes, max, comparator, criteriaKeyBinding, optionalFilterEval);
    }
}
