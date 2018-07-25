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
package eu.uk.ncl.pet5o.esper.event;

import eu.uk.ncl.pet5o.esper.client.EventBean;
import eu.uk.ncl.pet5o.esper.client.EventPropertyGetter;
import eu.uk.ncl.pet5o.esper.client.EventType;
import eu.uk.ncl.pet5o.esper.client.FragmentEventType;
import eu.uk.ncl.pet5o.esper.collection.MultiKey;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntyped;
import eu.uk.ncl.pet5o.esper.collection.MultiKeyUntypedEventPair;
import eu.uk.ncl.pet5o.esper.collection.UniformPair;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluator;
import eu.uk.ncl.pet5o.esper.epl.expression.core.ExprEvaluatorContext;
import eu.uk.ncl.pet5o.esper.util.EventBeanSummarizer;
import eu.uk.ncl.pet5o.esper.util.JavaClassHelper;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Method to getSelectListEvents events in collections to other collections or other event types.
 */
public class EventBeanUtility {
    public final static String METHOD_FLATTENBATCHJOIN = "flattenBatchJoin";
    public final static String METHOD_FLATTENBATCHSTREAM = "flattenBatchStream";

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] allocatePerStreamShift(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] evalEvents = new eu.uk.ncl.pet5o.esper.client.EventBean[eventsPerStream.length + 1];
        System.arraycopy(eventsPerStream, 0, evalEvents, 1, eventsPerStream.length);
        return evalEvents;
    }

    public static Object getNonemptyFirstEventUnderlying(Collection<EventBean> matchingEvents) {
        eu.uk.ncl.pet5o.esper.client.EventBean event = getNonemptyFirstEvent(matchingEvents);
        return event.getUnderlying();
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * @param matchingEvents events
     * @return event
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean getNonemptyFirstEvent(Collection<EventBean> matchingEvents) {
        if (matchingEvents instanceof List) {
            return ((List<EventBean>) matchingEvents).get(0);
        }
        if (matchingEvents instanceof Deque) {
            return ((Deque<EventBean>) matchingEvents).getFirst();
        }
        return matchingEvents.iterator().next();
    }

    public static EventPropertyGetter getAssertPropertyGetter(EventType type, String propertyName) {
        EventPropertyGetter getter = type.getGetter(propertyName);
        if (getter == null) {
            throw new IllegalStateException("Property " + propertyName + " not found in type " + type.getName());
        }
        return getter;
    }

    public static EventPropertyGetter getAssertPropertyGetter(EventType[] eventTypes, int keyStreamNum, String property) {
        return getAssertPropertyGetter(eventTypes[keyStreamNum], property);
    }

    /**
     * Resizes an array of events to a new size.
     * <p>
     * Returns the same array reference if the size is the same.
     *
     * @param oldArray array to resize
     * @param newSize  new array size
     * @return resized array
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] resizeArray(eu.uk.ncl.pet5o.esper.client.EventBean[] oldArray, int newSize) {
        if (oldArray == null) {
            return null;
        }
        if (oldArray.length == newSize) {
            return oldArray;
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] newArray = new eu.uk.ncl.pet5o.esper.client.EventBean[newSize];
        int preserveLength = Math.min(oldArray.length, newSize);
        if (preserveLength > 0) {
            System.arraycopy(oldArray, 0, newArray, 0, preserveLength);
        }
        return newArray;
    }

    /**
     * Flatten the vector of arrays to an array. Return null if an empty vector was passed, else
     * return an array containing all the events.
     *
     * @param eventVector vector
     * @return array with all events
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> flattenList(ArrayDeque<UniformPair<EventBean[]>> eventVector) {
        if (eventVector.isEmpty()) {
            return null;
        }

        if (eventVector.size() == 1) {
            return eventVector.getFirst();
        }

        int totalNew = 0;
        int totalOld = 0;
        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : eventVector) {
            if (pair != null) {
                if (pair.getFirst() != null) {
                    totalNew += pair.getFirst().length;
                }
                if (pair.getSecond() != null) {
                    totalOld += pair.getSecond().length;
                }
            }
        }

        if ((totalNew + totalOld) == 0) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] resultNew = null;
        if (totalNew > 0) {
            resultNew = new eu.uk.ncl.pet5o.esper.client.EventBean[totalNew];
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] resultOld = null;
        if (totalOld > 0) {
            resultOld = new eu.uk.ncl.pet5o.esper.client.EventBean[totalOld];
        }

        int destPosNew = 0;
        int destPosOld = 0;
        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : eventVector) {
            if (pair != null) {
                if (pair.getFirst() != null) {
                    System.arraycopy(pair.getFirst(), 0, resultNew, destPosNew, pair.getFirst().length);
                    destPosNew += pair.getFirst().length;
                }
                if (pair.getSecond() != null) {
                    System.arraycopy(pair.getSecond(), 0, resultOld, destPosOld, pair.getSecond().length);
                    destPosOld += pair.getSecond().length;
                }
            }
        }

        return new UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]>(resultNew, resultOld);
    }

    /**
     * Flatten the vector of arrays to an array. Return null if an empty vector was passed, else
     * return an array containing all the events.
     *
     * @param eventVector vector
     * @return array with all events
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] flatten(ArrayDeque<EventBean[]> eventVector) {
        if (eventVector.isEmpty()) {
            return null;
        }

        if (eventVector.size() == 1) {
            return eventVector.getFirst();
        }

        int totalElements = 0;
        for (eu.uk.ncl.pet5o.esper.client.EventBean[] arr : eventVector) {
            if (arr != null) {
                totalElements += arr.length;
            }
        }

        if (totalElements == 0) {
            return null;
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[totalElements];
        int destPos = 0;
        for (eu.uk.ncl.pet5o.esper.client.EventBean[] arr : eventVector) {
            if (arr != null) {
                System.arraycopy(arr, 0, result, destPos, arr.length);
                destPos += arr.length;
            }
        }

        return result;
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * Flatten the vector of arrays to an array. Return null if an empty vector was passed, else
     * return an array containing all the events.
     *
     * @param updateVector is a list of updates of old and new events
     * @return array with all events
     */
    public static UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> flattenBatchStream(List<UniformPair<EventBean[]>> updateVector) {
        if (updateVector.isEmpty()) {
            return new UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]>(null, null);
        }

        if (updateVector.size() == 1) {
            return new UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]>(updateVector.get(0).getFirst(), updateVector.get(0).getSecond());
        }

        int totalNewEvents = 0;
        int totalOldEvents = 0;
        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : updateVector) {
            if (pair.getFirst() != null) {
                totalNewEvents += pair.getFirst().length;
            }
            if (pair.getSecond() != null) {
                totalOldEvents += pair.getSecond().length;
            }
        }

        if ((totalNewEvents == 0) && (totalOldEvents == 0)) {
            return new UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]>(null, null);
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] newEvents = null;
        eu.uk.ncl.pet5o.esper.client.EventBean[] oldEvents = null;
        if (totalNewEvents != 0) {
            newEvents = new eu.uk.ncl.pet5o.esper.client.EventBean[totalNewEvents];
        }
        if (totalOldEvents != 0) {
            oldEvents = new eu.uk.ncl.pet5o.esper.client.EventBean[totalOldEvents];
        }

        int destPosNew = 0;
        int destPosOld = 0;
        for (UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]> pair : updateVector) {
            eu.uk.ncl.pet5o.esper.client.EventBean[] newData = pair.getFirst();
            eu.uk.ncl.pet5o.esper.client.EventBean[] oldData = pair.getSecond();

            if (newData != null) {
                int newDataLen = newData.length;
                System.arraycopy(newData, 0, newEvents, destPosNew, newDataLen);
                destPosNew += newDataLen;
            }
            if (oldData != null) {
                int oldDataLen = oldData.length;
                System.arraycopy(oldData, 0, oldEvents, destPosOld, oldDataLen);
                destPosOld += oldDataLen;
            }
        }

        return new UniformPair<eu.uk.ncl.pet5o.esper.client.EventBean[]>(newEvents, oldEvents);
    }

    /**
     * Append arrays.
     *
     * @param source array
     * @param append array
     * @return appended array
     */
    protected static eu.uk.ncl.pet5o.esper.client.EventBean[] append(eu.uk.ncl.pet5o.esper.client.EventBean[] source, eu.uk.ncl.pet5o.esper.client.EventBean[] append) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[source.length + append.length];
        System.arraycopy(source, 0, result, 0, source.length);
        System.arraycopy(append, 0, result, source.length, append.length);
        return result;
    }

    /**
     * Convert list of events to array, returning null for empty or null lists.
     *
     * @param eventList is a list of events to convert
     * @return array of events
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] toArray(Collection<EventBean> eventList) {
        if ((eventList == null) || (eventList.isEmpty())) {
            return null;
        }
        return eventList.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[eventList.size()]);
    }

    /**
     * Returns object array containing property values of given properties, retrieved via EventPropertyGetter
     * instances.
     *
     * @param theEvent        - event to get property values from
     * @param propertyGetters - getters to use for getting property values
     * @return object array with property values
     */
    public static Object[] getPropertyArray(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EventPropertyGetter[] propertyGetters) {
        Object[] keyValues = new Object[propertyGetters.length];
        for (int i = 0; i < propertyGetters.length; i++) {
            keyValues[i] = propertyGetters[i].get(theEvent);
        }
        return keyValues;
    }

    public static Object[] getPropertyArray(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, EventPropertyGetter[] propertyGetters, int[] streamNums) {
        Object[] keyValues = new Object[propertyGetters.length];
        for (int i = 0; i < propertyGetters.length; i++) {
            keyValues[i] = propertyGetters[i].get(eventsPerStream[streamNums[i]]);
        }
        return keyValues;
    }

    /**
     * Returns Multikey instance for given event and getters.
     *
     * @param theEvent        - event to get property values from
     * @param propertyGetters - getters for access to properties
     * @return MultiKey with property values
     */
    public static MultiKeyUntyped getMultiKey(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EventPropertyGetter[] propertyGetters) {
        Object[] keyValues = getPropertyArray(theEvent, propertyGetters);
        return new MultiKeyUntyped(keyValues);
    }

    public static MultiKeyUntyped getMultiKey(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, EventPropertyGetter[] propertyGetters, Class[] coercionTypes) {
        Object[] keyValues = getPropertyArray(theEvent, propertyGetters);
        if (coercionTypes == null) {
            return new MultiKeyUntyped(keyValues);
        }
        for (int i = 0; i < coercionTypes.length; i++) {
            Object key = keyValues[i];
            if ((key != null) && (!key.getClass().equals(coercionTypes[i]))) {
                if (key instanceof Number) {
                    key = JavaClassHelper.coerceBoxed((Number) key, coercionTypes[i]);
                    keyValues[i] = key;
                }
            }
        }
        return new MultiKeyUntyped(keyValues);
    }

    public static MultiKeyUntyped getMultiKey(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluator[] evaluators, ExprEvaluatorContext context, Class[] coercionTypes) {
        Object[] keyValues = getPropertyArray(eventsPerStream, evaluators, context);
        if (coercionTypes == null) {
            return new MultiKeyUntyped(keyValues);
        }
        for (int i = 0; i < coercionTypes.length; i++) {
            Object key = keyValues[i];
            if ((key != null) && (!key.getClass().equals(coercionTypes[i]))) {
                if (key instanceof Number) {
                    key = JavaClassHelper.coerceBoxed((Number) key, coercionTypes[i]);
                    keyValues[i] = key;
                }
            }
        }
        return new MultiKeyUntyped(keyValues);
    }

    private static Object[] getPropertyArray(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, ExprEvaluator[] evaluators, ExprEvaluatorContext context) {
        Object[] keys = new Object[evaluators.length];
        for (int i = 0; i < keys.length; i++) {
            keys[i] = evaluators[i].evaluate(eventsPerStream, true, context);
        }
        return keys;
    }

    public static Object coerce(Object target, Class coercionType) {
        if (coercionType == null) {
            return target;
        }
        if (target != null && !target.getClass().equals(coercionType)) {
            if (target instanceof Number) {
                return JavaClassHelper.coerceBoxed((Number) target, coercionType);
            }
        }
        return target;
    }

    /**
     * Format the event and return a string representation.
     *
     * @param theEvent is the event to format.
     * @return string representation of event
     */
    public static String printEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        StringWriter writer = new StringWriter();
        PrintWriter buf = new PrintWriter(writer);
        printEvent(buf, theEvent);
        return writer.toString();
    }

    public static String printEvents(eu.uk.ncl.pet5o.esper.client.EventBean[] events) {
        StringWriter writer = new StringWriter();
        PrintWriter buf = new PrintWriter(writer);
        int count = 0;
        for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
            count++;
            buf.println("Event " + String.format("%6d:", count));
            printEvent(buf, theEvent);
        }
        return writer.toString();
    }

    private static void printEvent(PrintWriter writer, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        String[] properties = theEvent.getEventType().getPropertyNames();
        for (int i = 0; i < properties.length; i++) {
            String propName = properties[i];
            Object property = theEvent.get(propName);
            String printProperty;
            if (property == null) {
                printProperty = "null";
            } else if (property instanceof Object[]) {
                printProperty = "Array :" + Arrays.toString((Object[]) property);
            } else if (property.getClass().isArray()) {
                printProperty = "Array :" + printArray(property);
            } else {
                printProperty = property.toString();
            }
            writer.println("#" + i + "  " + propName + " = " + printProperty);
        }
    }

    private static String printArray(Object array) {
        Object[] objects = new Object[Array.getLength(array)];
        for (int i = 0; i < Array.getLength(array); i++) {
            objects[i] = Array.get(array, i);
        }
        return Arrays.toString(objects);
    }

    public static void appendEvent(StringWriter writer, eu.uk.ncl.pet5o.esper.client.EventBean theEvent) {
        String[] properties = theEvent.getEventType().getPropertyNames();
        String delimiter = "";
        for (int i = 0; i < properties.length; i++) {
            String propName = properties[i];
            Object property = theEvent.get(propName);
            String printProperty;
            if (property == null) {
                printProperty = "null";
            } else if (property.getClass().isArray()) {
                printProperty = "Array :" + Arrays.toString((Object[]) property);
            } else {
                printProperty = property.toString();
            }
            writer.append(delimiter);
            writer.append(propName);
            writer.append("=");
            writer.append(printProperty);
            delimiter = ",";
        }
    }

    /**
     * NOTE: Code-generation-invoked method, method name and parameter order matters
     * Flattens a list of pairs of join result sets.
     *
     * @param joinPostings is the list
     * @return is the consolidate sets
     */
    public static UniformPair<Set<MultiKey<EventBean>>> flattenBatchJoin(List<UniformPair<Set<MultiKey<EventBean>>>> joinPostings) {
        if (joinPostings.isEmpty()) {
            return new UniformPair<>(null, null);
        }

        if (joinPostings.size() == 1) {
            return new UniformPair<>(joinPostings.get(0).getFirst(), joinPostings.get(0).getSecond());
        }

        Set<MultiKey<EventBean>> newEvents = new LinkedHashSet<MultiKey<EventBean>>();
        Set<MultiKey<EventBean>> oldEvents = new LinkedHashSet<MultiKey<EventBean>>();

        for (UniformPair<Set<MultiKey<EventBean>>> pair : joinPostings) {
            Set<MultiKey<EventBean>> newData = pair.getFirst();
            Set<MultiKey<EventBean>> oldData = pair.getSecond();

            if (newData != null) {
                newEvents.addAll(newData);
            }
            if (oldData != null) {
                oldEvents.addAll(oldData);
            }
        }

        return new UniformPair<>(newEvents, oldEvents);
    }

    /**
     * Expand the array passed in by the single element to add.
     *
     * @param array      to expand
     * @param eventToAdd element to add
     * @return resized array
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] addToArray(eu.uk.ncl.pet5o.esper.client.EventBean[] array, eu.uk.ncl.pet5o.esper.client.EventBean eventToAdd) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] newArray = new eu.uk.ncl.pet5o.esper.client.EventBean[array.length + 1];
        System.arraycopy(array, 0, newArray, 0, array.length);
        newArray[newArray.length - 1] = eventToAdd;
        return newArray;
    }

    /**
     * Expand the array passed in by the multiple elements to add.
     *
     * @param array       to expand
     * @param eventsToAdd elements to add
     * @return resized array
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] addToArray(eu.uk.ncl.pet5o.esper.client.EventBean[] array, Collection<EventBean> eventsToAdd) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] newArray = new eu.uk.ncl.pet5o.esper.client.EventBean[array.length + eventsToAdd.size()];
        System.arraycopy(array, 0, newArray, 0, array.length);

        int counter = array.length;
        for (eu.uk.ncl.pet5o.esper.client.EventBean eventToAdd : eventsToAdd) {
            newArray[counter++] = eventToAdd;
        }
        return newArray;
    }

    /**
     * Create a fragment event type.
     *
     * @param propertyType        property return type
     * @param genericType         property generic type parameter, or null if none
     * @param eventAdapterService for event types
     * @return fragment type
     */
    public static FragmentEventType createNativeFragmentType(Class propertyType, Class genericType, EventAdapterService eventAdapterService) {
        boolean isIndexed = false;

        if (propertyType.isArray()) {
            isIndexed = true;
            propertyType = propertyType.getComponentType();
        } else if (JavaClassHelper.isImplementsInterface(propertyType, Iterable.class)) {
            isIndexed = true;
            if (genericType == null) {
                return null;
            }
            propertyType = genericType;
        }

        if (!JavaClassHelper.isFragmentableType(propertyType)) {
            return null;
        }

        EventType type = eventAdapterService.getBeanEventTypeFactory().createBeanType(propertyType.getName(), propertyType, false, false, false);
        return new FragmentEventType(type, isIndexed, true);
    }

    /**
     * Returns the distinct events by properties.
     *
     * @param events to inspect
     * @param reader for retrieving properties
     * @return distinct events
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getDistinctByProp(ArrayDeque<EventBean> events, EventBeanReader reader) {
        if (events == null || events.isEmpty()) {
            return new eu.uk.ncl.pet5o.esper.client.EventBean[0];
        }
        if (events.size() < 2) {
            return events.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()]);
        }

        Set<MultiKeyUntypedEventPair> set = new LinkedHashSet<MultiKeyUntypedEventPair>();
        if (events.getFirst() instanceof NaturalEventBean) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
                eu.uk.ncl.pet5o.esper.client.EventBean inner = ((NaturalEventBean) theEvent).getOptionalSynthetic();
                Object[] keys = reader.read(inner);
                MultiKeyUntypedEventPair pair = new MultiKeyUntypedEventPair(keys, theEvent);
                set.add(pair);
            }
        } else {
            for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
                Object[] keys = reader.read(theEvent);
                MultiKeyUntypedEventPair pair = new MultiKeyUntypedEventPair(keys, theEvent);
                set.add(pair);
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[set.size()];
        int count = 0;
        for (MultiKeyUntypedEventPair row : set) {
            result[count++] = row.getEventBean();
        }
        return result;
    }

    /**
     * Returns the distinct events by properties.
     *
     * @param events to inspect
     * @param reader for retrieving properties
     * @return distinct events
     */
    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getDistinctByProp(eu.uk.ncl.pet5o.esper.client.EventBean[] events, EventBeanReader reader) {
        if ((events == null) || (events.length < 2)) {
            return events;
        }

        Set<MultiKeyUntypedEventPair> set = new LinkedHashSet<MultiKeyUntypedEventPair>();
        if (events[0] instanceof NaturalEventBean) {
            for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
                eu.uk.ncl.pet5o.esper.client.EventBean inner = ((NaturalEventBean) theEvent).getOptionalSynthetic();
                Object[] keys = reader.read(inner);
                MultiKeyUntypedEventPair pair = new MultiKeyUntypedEventPair(keys, theEvent);
                set.add(pair);
            }
        } else {
            for (eu.uk.ncl.pet5o.esper.client.EventBean theEvent : events) {
                Object[] keys = reader.read(theEvent);
                MultiKeyUntypedEventPair pair = new MultiKeyUntypedEventPair(keys, theEvent);
                set.add(pair);
            }
        }

        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[set.size()];
        int count = 0;
        for (MultiKeyUntypedEventPair row : set) {
            result[count++] = row.getEventBean();
        }
        return result;
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] denaturalize(eu.uk.ncl.pet5o.esper.client.EventBean[] naturals) {
        if (naturals == null || naturals.length == 0) {
            return null;
        }
        if (!(naturals[0] instanceof NaturalEventBean)) {
            return naturals;
        }
        if (naturals.length == 1) {
            return new eu.uk.ncl.pet5o.esper.client.EventBean[]{((NaturalEventBean) naturals[0]).getOptionalSynthetic()};
        }
        eu.uk.ncl.pet5o.esper.client.EventBean[] result = new eu.uk.ncl.pet5o.esper.client.EventBean[naturals.length];
        for (int i = 0; i < naturals.length; i++) {
            result[i] = ((NaturalEventBean) naturals[i]).getOptionalSynthetic();
        }
        return result;
    }

    public static boolean eventsAreEqualsAllowNull(eu.uk.ncl.pet5o.esper.client.EventBean first, eu.uk.ncl.pet5o.esper.client.EventBean second) {
        if (first == null) {
            return second == null;
        }
        return second != null && first.equals(second);
    }


    public static void safeArrayCopy(eu.uk.ncl.pet5o.esper.client.EventBean[] eventsPerStream, eu.uk.ncl.pet5o.esper.client.EventBean[] eventsLambda) {
        if (eventsPerStream.length <= eventsLambda.length) {
            System.arraycopy(eventsPerStream, 0, eventsLambda, 0, eventsPerStream.length);
        } else {
            System.arraycopy(eventsPerStream, 0, eventsLambda, 0, eventsLambda.length);
        }
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getNewDataNonRemoved(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, HashSet<EventBean> removedEvents) {
        boolean filter = false;
        for (int i = 0; i < newData.length; i++) {
            if (removedEvents.contains(newData[i])) {
                filter = true;
            }
        }
        if (!filter) {
            return newData;
        }
        if (newData.length == 1) {
            return null;
        }
        ArrayDeque<EventBean> events = new ArrayDeque<EventBean>(newData.length - 1);
        for (int i = 0; i < newData.length; i++) {
            if (!removedEvents.contains(newData[i])) {
                events.add(newData[i]);
            }
        }
        if (events.isEmpty()) {
            return null;
        }
        return events.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()]);
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] getNewDataNonRemoved(eu.uk.ncl.pet5o.esper.client.EventBean[] newData, HashSet<EventBean> removedEvents, eu.uk.ncl.pet5o.esper.client.EventBean[][] newEventsPerView) {
        if (newData == null || newData.length == 0) {
            return null;
        }
        if (newData.length == 1) {
            if (removedEvents.contains(newData[0])) {
                return null;
            }
            boolean pass = findEvent(newData[0], newEventsPerView);
            return pass ? newData : null;
        }
        ArrayDeque<EventBean> events = new ArrayDeque<EventBean>(newData.length - 1);
        for (int i = 0; i < newData.length; i++) {
            if (!removedEvents.contains(newData[i])) {
                boolean pass = findEvent(newData[i], newEventsPerView);
                if (pass) {
                    events.add(newData[i]);
                }
            }
        }
        if (events.isEmpty()) {
            return null;
        }
        return events.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()]);
    }

    /**
     * Renders a map of elements, in which elements can be events or event arrays interspersed with other objects,
     *
     * @param map to render
     * @return comma-separated list of map entry name-value pairs
     */
    public static String toString(Map<String, Object> map) {
        if (map == null) {
            return "null";
        }
        if (map.isEmpty()) {
            return "";
        }
        StringBuilder buf = new StringBuilder();
        String delimiter = "";
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            buf.append(delimiter);
            buf.append(entry.getKey());
            buf.append("=");
            if (entry.getValue() instanceof eu.uk.ncl.pet5o.esper.client.EventBean) {
                buf.append(EventBeanSummarizer.summarize((eu.uk.ncl.pet5o.esper.client.EventBean) entry.getValue()));
            } else if (entry.getValue() instanceof eu.uk.ncl.pet5o.esper.client.EventBean[]) {
                buf.append(EventBeanSummarizer.summarize((eu.uk.ncl.pet5o.esper.client.EventBean[]) entry.getValue()));
            } else if (entry.getValue() == null) {
                buf.append("null");
            } else {
                buf.append(entry.getValue().toString());
            }
            delimiter = ", ";
        }
        return buf.toString();
    }

    public static void addToCollection(eu.uk.ncl.pet5o.esper.client.EventBean[] toAdd, Collection<EventBean> events) {
        if (toAdd == null) {
            return;
        }
        Collections.addAll(events, toAdd);
    }

    public static void addToCollection(Set<MultiKey<EventBean>> toAdd, Collection<MultiKey<EventBean>> events) {
        if (toAdd == null) {
            return;
        }
        events.addAll(toAdd);
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] toArrayNullIfEmpty(Collection<EventBean> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        return events.toArray(new eu.uk.ncl.pet5o.esper.client.EventBean[events.size()]);
    }

    public static Set<MultiKey<EventBean>> toLinkedHashSetNullIfEmpty(Collection<MultiKey<EventBean>> events) {
        if (events == null || events.isEmpty()) {
            return null;
        }
        return new LinkedHashSet<MultiKey<EventBean>>(events);
    }

    public static Set<MultiKey<EventBean>> toSingletonSetIfNotNull(MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> row) {
        if (row == null) {
            return null;
        }
        return Collections.singleton(row);
    }

    public static MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> getLastInSet(Set<MultiKey<EventBean>> events) {
        if (events.isEmpty()) {
            return null;
        }
        int count = 0;
        for (MultiKey<eu.uk.ncl.pet5o.esper.client.EventBean> row : events) {
            count++;
            if (count == events.size()) {
                return row;
            }
        }
        throw new IllegalStateException("Cannot get last on empty collection");
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] toArrayIfNotNull(eu.uk.ncl.pet5o.esper.client.EventBean optionalEvent) {
        if (optionalEvent == null) {
            return null;
        }
        return new eu.uk.ncl.pet5o.esper.client.EventBean[]{optionalEvent};
    }

    public static boolean compareEventReferences(eu.uk.ncl.pet5o.esper.client.EventBean[] firstNonNull, eu.uk.ncl.pet5o.esper.client.EventBean[] secondNonNull) {
        if (firstNonNull.length != secondNonNull.length) {
            return false;
        }
        for (int i = 0; i < firstNonNull.length; i++) {
            if (firstNonNull[i] != secondNonNull[i]) {
                return false;
            }
        }
        return true;
    }

    public static eu.uk.ncl.pet5o.esper.client.EventBean[] copyArray(eu.uk.ncl.pet5o.esper.client.EventBean[] events) {
        eu.uk.ncl.pet5o.esper.client.EventBean[] copy = new eu.uk.ncl.pet5o.esper.client.EventBean[events.length];
        System.arraycopy(events, 0, copy, 0, copy.length);
        return copy;
    }

    private static boolean findEvent(eu.uk.ncl.pet5o.esper.client.EventBean theEvent, eu.uk.ncl.pet5o.esper.client.EventBean[][] eventsPerView) {
        for (int i = 0; i < eventsPerView.length; i++) {
            if (eventsPerView[i] == null) {
                continue;
            }
            for (int j = 0; j < eventsPerView[i].length; j++) {
                if (eventsPerView[i][j] == theEvent) {
                    return true;
                }
            }
        }
        return false;
    }
}
