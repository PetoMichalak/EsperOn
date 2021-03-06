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
package eu.uk.ncl.pet5o.esper.core.context.stmt;

import eu.uk.ncl.pet5o.esper.epl.expression.prior.ExprPriorEvalStrategy;

public interface AIRegistryPrior extends ExprPriorEvalStrategy {
    public void assignService(int num, ExprPriorEvalStrategy value);

    public void deassignService(int num);

    public int getAgentInstanceCount();
}
