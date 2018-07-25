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
package eu.uk.ncl.pet5o.esper.core.context.activator;

import eu.uk.ncl.pet5o.esper.core.context.util.AgentInstanceContext;
import eu.uk.ncl.pet5o.esper.epl.core.engineimport.EngineImportService;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowConsumerDesc;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowConsumerView;
import eu.uk.ncl.pet5o.esper.epl.named.NamedWindowProcessor;
import eu.uk.ncl.pet5o.esper.epl.util.ExprNodeUtilityRich;
import eu.uk.ncl.pet5o.esper.filterspec.PropertyEvaluator;

import java.util.List;

public class ViewableActivatorNamedWindow implements ViewableActivator {

    private final NamedWindowProcessor processor;
    private final ExprNode[] filterExpressions;
    private final ExprEvaluator[] filterEvaluators;
    private final PropertyEvaluator optPropertyEvaluator;

    public ViewableActivatorNamedWindow(NamedWindowProcessor processor, List<ExprNode> filterExpressions, PropertyEvaluator optPropertyEvaluator, EngineImportService engineImportService, String statementName) {
        this.processor = processor;
        this.filterExpressions = filterExpressions.toArray(new ExprNode[filterExpressions.size()]);
        this.filterEvaluators = ExprNodeUtilityRich.getEvaluatorsMayCompile(filterExpressions, engineImportService, this.getClass(), false, statementName);
        this.optPropertyEvaluator = optPropertyEvaluator;
    }

    public ViewableActivationResult activate(AgentInstanceContext agentInstanceContext, boolean isSubselect, boolean isRecoveringResilient) {
        NamedWindowConsumerDesc consumerDesc = new NamedWindowConsumerDesc(filterExpressions, filterEvaluators, optPropertyEvaluator, agentInstanceContext);
        NamedWindowConsumerView consumerView = processor.addConsumer(consumerDesc, isSubselect);
        return new ViewableActivationResult(consumerView, consumerView, null, null, null, false, false, null);
    }
}
