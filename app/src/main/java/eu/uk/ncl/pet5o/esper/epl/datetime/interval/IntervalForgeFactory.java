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
package eu.uk.ncl.pet5o.esper.epl.datetime.interval;

import eu.uk.ncl.pet5o.esper.epl.core.streamtype.StreamTypeService;
import eu.uk.ncl.pet5o.esper.epl.datetime.eval.DatetimeMethodEnum;
import eu.uk.ncl.pet5o.esper.epl.datetime.eval.ForgeFactory;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprNode;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprValidationException;
import eu.uk.ncl.pet5o.esper.epl.expression.time.TimeAbacus;

import java.util.List;
import java.util.TimeZone;

public class IntervalForgeFactory implements ForgeFactory {
    public IntervalForge getForge(StreamTypeService streamTypeService, DatetimeMethodEnum method, String methodNameUsed, List<ExprNode> parameters, TimeZone timeZone, TimeAbacus timeAbacus)
            throws ExprValidationException {
        return new IntervalForgeImpl(method, methodNameUsed, streamTypeService, parameters, timeZone, timeAbacus);
    }

}
