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
package eu.uk.ncl.pet5o.esper.epl.datetime.eval;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.collection.Pair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprIdentNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNodeUtilityCore;
import eu.uk.ncl.pet5o.esper.epl.join.plan.FilterExprAnalyzerAffector;
import eu.uk.ncl.pet5o.esper.epl.join.plan.QueryGraph;
import eu.uk.ncl.pet5o.esper.epl.join.util.RangeFilterAnalyzer;
import eu.uk.ncl.pet5o.esper.epl.lookup.AdvancedIndexConfigContextPartition;

import java.util.List;

public class FilterExprAnalyzerDTBetweenAffector implements FilterExprAnalyzerAffector {
    private final EventType[] typesPerStream;
    private final int targetStreamNum;
    private final String targetPropertyName;
    private final ExprNode start;
    private final ExprNode end;
    private final boolean includeLow;
    private final boolean includeHigh;

    public FilterExprAnalyzerDTBetweenAffector(EventType[] typesPerStream, int targetStreamNum, String targetPropertyName, ExprNode start, ExprNode end, boolean includeLow, boolean includeHigh) {
        this.typesPerStream = typesPerStream;
        this.targetStreamNum = targetStreamNum;
        this.targetPropertyName = targetPropertyName;
        this.start = start;
        this.end = end;
        this.includeLow = includeLow;
        this.includeHigh = includeHigh;
    }

    public void apply(QueryGraph queryGraph) {
        ExprIdentNode targetExpr = ExprNodeUtilityCore.getExprIdentNode(typesPerStream, targetStreamNum, targetPropertyName);
        RangeFilterAnalyzer.apply(targetExpr, start, end, includeLow, includeHigh, false, queryGraph);
    }

    public ExprNode[] getIndexExpressions() {
        return null;
    }

    public List<Pair<ExprNode, int[]>> getKeyExpressions() {
        return null;
    }

    public AdvancedIndexConfigContextPartition getOptionalIndexSpec() {
        return null;
    }

    public String getOptionalIndexName() {
        return null;
    }
}

