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
package eu.uk.ncl.pet5o.esper.epl.enummethod.dot;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenClassScope;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMember;
import eu.uk.ncl.pet5o.esper.codegen.base.CodegenMethodScope;
import eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpression;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPType;
import eu.uk.ncl.pet5o.esper.epl.rettype.EPTypeHelper;
import eu.uk.ncl.pet5o.esper.event.EventAdapterService;
import eu.uk.ncl.pet5o.esper.event.bean.BeanEventType;

import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;

import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.*;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.exprDotMethod;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.member;
import static eu.uk.ncl.pet5o.esper.codegen.model.expression.CodegenExpressionBuilder.newInstance;

public class ExprDotStaticMethodWrapIterableEvents implements ExprDotStaticMethodWrap {
    private EventAdapterService eventAdapterService;
    private BeanEventType type;

    public ExprDotStaticMethodWrapIterableEvents(EventAdapterService eventAdapterService, BeanEventType type) {
        this.eventAdapterService = eventAdapterService;
        this.type = type;
    }

    public EPType getTypeInfo() {
        return EPTypeHelper.collectionOfEvents(type);
    }

    public Collection convertNonNull(Object result) {
        // there is a need to read the iterator to the cache since if it's iterated twice, the iterator is already exhausted
        return new WrappingCollection(eventAdapterService, type, ((Iterable) result).iterator());
    }


    public CodegenExpression codegenConvertNonNull(CodegenExpression result, CodegenMethodScope codegenMethodScope, CodegenClassScope codegenClassScope) {
        CodegenMember eventSvcMember = codegenClassScope.makeAddMember(EventAdapterService.class, eventAdapterService);
        CodegenMember typeMember = codegenClassScope.makeAddMember(BeanEventType.class, type);
        return newInstance(WrappingCollection.class, member(eventSvcMember.getMemberId()), member(typeMember.getMemberId()), exprDotMethod(result, "iterator"));
    }

    public static class WrappingCollection implements Collection<EventBean> {
        private EventAdapterService eventAdapterService;
        private BeanEventType type;
        private Iterator inner;
        private Object[] cache = null;

        public WrappingCollection(EventAdapterService eventAdapterService, BeanEventType type, Iterator inner) {
            this.eventAdapterService = eventAdapterService;
            this.type = type;
            this.inner = inner;
        }

        public int size() {
            if (cache == null) {
                init();
            }
            return cache.length;
        }

        public boolean isEmpty() {
            if (cache == null) {
                init();
            }
            return cache.length == 0;
        }

        public Iterator<EventBean> iterator() {
            if (cache == null) {
                init();
            }
            return new ExprDotStaticMethodWrapArrayEvents.WrappingIterator(eventAdapterService, type, cache);
        }

        private void init() {
            Deque<Object> q = new ArrayDeque<Object>();
            for (; inner.hasNext(); ) {
                q.add(inner.next());
            }
            cache = q.toArray();
        }

        public boolean contains(Object o) {
            throw new UnsupportedOperationException("Partial implementation");
        }

        public Object[] toArray() {
            throw new UnsupportedOperationException("Partial implementation");
        }

        public <T> T[] toArray(T[] a) {
            throw new UnsupportedOperationException("Partial implementation");
        }

        public boolean add(EventBean eventBean) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public boolean containsAll(Collection<?> c) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public boolean addAll(Collection<? extends EventBean> c) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException("Read-only implementation");
        }

        public void clear() {
            throw new UnsupportedOperationException("Read-only implementation");
        }
    }
}
