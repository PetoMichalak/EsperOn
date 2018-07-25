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
package eu.uk.ncl.pet5o.esper.core.context.subselect;

import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.epl.expression.subquery.ExprSubselectNode;
import eu.uk.ncl.pet5o.esper.view.ViewFactoryChain;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds stream information for subqueries.
 */
public class SubSelectActivationCollection {
    private Map<ExprSubselectNode, SubSelectActivationHolder> subqueries;

    /**
     * Ctor.
     */
    public SubSelectActivationCollection() {
        subqueries = new HashMap<ExprSubselectNode, SubSelectActivationHolder>();
    }

    /**
     * Add lookup.
     *
     * @param subselectNode is the subselect expression node
     * @param holder        information holder for subselects
     */
    public void add(ExprSubselectNode subselectNode, SubSelectActivationHolder holder) {
        subqueries.put(subselectNode, holder);
    }

    public SubSelectActivationHolder getSubSelectHolder(ExprSubselectNode subselectNode) {
        return subqueries.get(subselectNode);
    }

    /**
     * Returns stream number.
     *
     * @param subqueryNode is the lookup node's stream number
     * @return number of stream
     */
    public int getStreamNumber(ExprSubselectNode subqueryNode) {
        return subqueries.get(subqueryNode).getStreamNumber();
    }

    /**
     * Returns the lookup viewable, child-most view.
     *
     * @param subqueryNode is the expression node to get this for
     * @return child viewable
     */
    public EventType getRootViewableType(ExprSubselectNode subqueryNode) {
        return subqueries.get(subqueryNode).getViewableType();
    }

    /**
     * Returns the lookup's view factory chain.
     *
     * @param subqueryNode is the node to look for
     * @return view factory chain
     */
    public ViewFactoryChain getViewFactoryChain(ExprSubselectNode subqueryNode) {
        return subqueries.get(subqueryNode).getViewFactoryChain();
    }

    public Map<ExprSubselectNode, SubSelectActivationHolder> getSubqueries() {
        return subqueries;
    }
}
