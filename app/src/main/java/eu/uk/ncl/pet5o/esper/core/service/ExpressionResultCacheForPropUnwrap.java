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
package eu.uk.ncl.pet5o.esper.core.service;

import eu.uk.ncl.pet5o.esper.client.EventBean;

import java.util.Collection;

/**
 * On the level of indexed event properties: Properties that are contained in EventBean instances, such as for Enumeration Methods, get wrapped only once for the same event.
 * The cache is keyed by property-name and EventBean reference and maintains a Collection&lt;EventBean&gt;.
 * <p>
 * NOTE: ExpressionResultCacheForPropUnwrap should not be held onto since the instance returned can be reused.
 */
public interface ExpressionResultCacheForPropUnwrap {

    ExpressionResultCacheEntryBeanAndCollBean getPropertyColl(String propertyNameFullyQualified, eu.uk.ncl.pet5o.esper.client.EventBean reference);

    void savePropertyColl(String propertyNameFullyQualified, eu.uk.ncl.pet5o.esper.client.EventBean reference, Collection<EventBean> events);
}
