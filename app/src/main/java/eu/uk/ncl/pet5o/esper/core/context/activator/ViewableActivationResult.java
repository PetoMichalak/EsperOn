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

import eu.uk.ncl.pet5o.esper.core.service.StatementAgentInstanceLock;
import eu.uk.ncl.pet5o.esper.pattern.EvalRootMatchRemover;
import eu.uk.ncl.pet5o.esper.pattern.EvalRootState;
import eu.uk.ncl.pet5o.esper.util.StopCallback;
import eu.uk.ncl.pet5o.esper.view.Viewable;

public class ViewableActivationResult {
    private final Viewable viewable;
    private final StopCallback stopCallback;
    private final StatementAgentInstanceLock optionalLock;
    private final EvalRootState optionalPatternRoot;
    private final EvalRootMatchRemover optEvalRootMatchRemover;
    private final boolean suppressSameEventMatches;
    private final boolean discardPartialsOnMatch;
    private ViewableActivationResultExtension viewableActivationResultExtension;

    public ViewableActivationResult(Viewable viewable, StopCallback stopCallback, StatementAgentInstanceLock optionalLock, EvalRootState optionalPatternRoot, EvalRootMatchRemover optEvalRootMatchRemover, boolean suppressSameEventMatches, boolean discardPartialsOnMatch, ViewableActivationResultExtension viewableActivationResultExtension) {
        this.viewable = viewable;
        this.stopCallback = stopCallback;
        this.optionalLock = optionalLock;
        this.optionalPatternRoot = optionalPatternRoot;
        this.optEvalRootMatchRemover = optEvalRootMatchRemover;
        this.suppressSameEventMatches = suppressSameEventMatches;
        this.discardPartialsOnMatch = discardPartialsOnMatch;
        this.viewableActivationResultExtension = viewableActivationResultExtension;
    }

    public StopCallback getStopCallback() {
        return stopCallback;
    }

    public Viewable getViewable() {
        return viewable;
    }

    public StatementAgentInstanceLock getOptionalLock() {
        return optionalLock;
    }

    public EvalRootState getOptionalPatternRoot() {
        return optionalPatternRoot;
    }

    public boolean isSuppressSameEventMatches() {
        return suppressSameEventMatches;
    }

    public boolean isDiscardPartialsOnMatch() {
        return discardPartialsOnMatch;
    }

    public ViewableActivationResultExtension getViewableActivationResultExtension() {
        return viewableActivationResultExtension;
    }

    public void setViewableActivationResultExtension(ViewableActivationResultExtension viewableActivationResultExtension) {
        this.viewableActivationResultExtension = viewableActivationResultExtension;
    }

    public EvalRootMatchRemover getOptEvalRootMatchRemover() {
        return optEvalRootMatchRemover;
    }
}
