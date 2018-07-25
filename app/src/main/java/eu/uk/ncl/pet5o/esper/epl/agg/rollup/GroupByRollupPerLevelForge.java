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
package eu.uk.ncl.pet5o.esper.epl.agg.rollup;

import eu.uk.ncl.pet5o.esper.epl.core.orderby.OrderByElementForge;
import eu.uk.ncl.pet5o.esper.epl.core.select.SelectExprProcessorForge;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprForge;

public class GroupByRollupPerLevelForge {
    private final SelectExprProcessorForge[] selectExprProcessorForges;
    private final ExprForge[] optionalHavingForges;
    private final OrderByElementForge[][] optionalOrderByElements;

    public GroupByRollupPerLevelForge(SelectExprProcessorForge[] selectExprProcessorForges, ExprForge[] optionalHavingForges, OrderByElementForge[][] optionalOrderByElements) {
        this.selectExprProcessorForges = selectExprProcessorForges;
        this.optionalHavingForges = optionalHavingForges;
        this.optionalOrderByElements = optionalOrderByElements;
    }

    public SelectExprProcessorForge[] getSelectExprProcessorForges() {
        return selectExprProcessorForges;
    }

    public ExprForge[] getOptionalHavingForges() {
        return optionalHavingForges;
    }

    public OrderByElementForge[][] getOptionalOrderByElements() {
        return optionalOrderByElements;
    }
}
