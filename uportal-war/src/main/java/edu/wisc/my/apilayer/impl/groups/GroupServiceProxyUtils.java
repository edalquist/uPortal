/**
 * Copyright 2001 The JA-SIG Collaborative.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. Redistributions of any form whatsoever must retain the following
 *    acknowledgment:
 *    "This product includes software developed by the JA-SIG Collaborative
 *    (http://www.jasig.org/)."
 *
 * THIS SOFTWARE IS PROVIDED BY THE JA-SIG COLLABORATIVE "AS IS" AND ANY
 * EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE JA-SIG COLLABORATIVE OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 */
package edu.wisc.my.apilayer.impl.groups;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.apache.commons.collections.bidimap.UnmodifiableBidiMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.EntityIdentifier;
import org.jasig.portal.IBasicEntity;
import org.jasig.portal.concurrency.IEntityLockService;
import org.jasig.portal.concurrency.LockingException;
import org.jasig.portal.groups.CompositeEntityIdentifier;
import org.jasig.portal.groups.GroupsException;
import org.jasig.portal.groups.IComponentGroupService;
import org.jasig.portal.groups.ICompositeGroupService;
import org.jasig.portal.groups.IEntity;
import org.jasig.portal.groups.IEntityGroup;
import org.jasig.portal.groups.IEntityGroupStore;
import org.jasig.portal.groups.IGroupConstants;
import org.jasig.portal.groups.IGroupMember;
import org.jasig.portal.groups.IGroupService;
import org.jasig.portal.groups.IIndividualGroupService;
import org.jasig.portal.groups.ILockableEntityGroup;
import org.jasig.portal.security.IPerson;

import edu.wisc.my.apilayer.groups.IEntityIdentifier;
import edu.wisc.my.apilayer.groups.LockType;
import edu.wisc.my.apilayer.groups.SearchMethod;


/**
 * Utilty methods used by the group service proxy classes.
 * 
 * TODO special handling for Class, LockType and SearchMethod
 * 
 * @author Eric Dalquist <a href="mailto:edalquist@unicon.net">edalquist@unicon.net</a>
 * @version $Revision: 1.4 $
 * @since 1.2
 */
@SuppressWarnings("deprecation")
public final class GroupServiceProxyUtils {
    private static final Log LOG = LogFactory.getLog(GroupServiceProxyUtils.class);
    
    private static final BidiMap UP_TO_API_WRAPPER_MAPPINGS;
    private static final BidiMap TYPE_MAPPINGS;
    
    static {
        //Setup the uPortal to api wrapper mappings
        final BidiMap upToApiWrapperMappingBuilder = new DualHashBidiMap();
        
        //Concrete wrappers
        upToApiWrapperMappingBuilder.put(CompositeEntityIdentifier.class, CompositeEntityIdentifierWrapper.class);
        upToApiWrapperMappingBuilder.put(EntityIdentifier.class, EntityIdentifierWrapper.class);
        upToApiWrapperMappingBuilder.put(GroupsException.class, GroupsExceptionImpl.class);
        upToApiWrapperMappingBuilder.put(LockingException.class, LockingExceptionImpl.class);
        upToApiWrapperMappingBuilder.put(IPerson.class, edu.wisc.my.apilayer.impl.person.PersonImpl.class);
        
        //Wrapped interfaces
        upToApiWrapperMappingBuilder.put(IBasicEntity.class, edu.wisc.my.apilayer.groups.IBasicEntity.class);
        upToApiWrapperMappingBuilder.put(IComponentGroupService.class, edu.wisc.my.apilayer.groups.IComponentGroupService.class);
        upToApiWrapperMappingBuilder.put(ICompositeGroupService.class, edu.wisc.my.apilayer.groups.ICompositeGroupService.class);
        upToApiWrapperMappingBuilder.put(IEntityGroupStore.class, edu.wisc.my.apilayer.groups.IEntityGroupStore.class);
        upToApiWrapperMappingBuilder.put(IEntityGroup.class, edu.wisc.my.apilayer.groups.IEntityGroup.class);
        upToApiWrapperMappingBuilder.put(IEntity.class, edu.wisc.my.apilayer.groups.IEntity.class);
        upToApiWrapperMappingBuilder.put(IGroupMember.class, edu.wisc.my.apilayer.groups.IGroupMember.class);
        upToApiWrapperMappingBuilder.put(IGroupService.class, edu.wisc.my.apilayer.groups.IGroupService.class);
        upToApiWrapperMappingBuilder.put(IIndividualGroupService.class, edu.wisc.my.apilayer.groups.IIndividualGroupService.class);
        upToApiWrapperMappingBuilder.put(ILockableEntityGroup.class, edu.wisc.my.apilayer.groups.ILockableEntityGroup.class);
        
        //Collection mappings, same interface but must be wrapped
        upToApiWrapperMappingBuilder.put(Collection.class, Collection.class);
        upToApiWrapperMappingBuilder.put(Iterator.class, Iterator.class);
        upToApiWrapperMappingBuilder.put(Map.class, Map.class);
        upToApiWrapperMappingBuilder.put(Set.class, Set.class);
        
        UP_TO_API_WRAPPER_MAPPINGS = UnmodifiableBidiMap.decorate(upToApiWrapperMappingBuilder);
        

        //Setup the uPortal to api type mappings
        final BidiMap typeMappingBuilder = new DualHashBidiMap();
        
        typeMappingBuilder.put(IBasicEntity.class, edu.wisc.my.apilayer.groups.IBasicEntity.class);
        typeMappingBuilder.put(IGroupMember.class, edu.wisc.my.apilayer.groups.IGroupMember.class);
        typeMappingBuilder.put(IEntity.class, edu.wisc.my.apilayer.groups.IEntity.class);
        typeMappingBuilder.put(IEntityGroup.class, edu.wisc.my.apilayer.groups.IEntityGroup.class);
        typeMappingBuilder.put(ILockableEntityGroup.class, edu.wisc.my.apilayer.groups.ILockableEntityGroup.class);
        typeMappingBuilder.put(IPerson.class, edu.wisc.my.apilayer.person.IPerson.class);
        typeMappingBuilder.put(EntityIdentifier.class, IEntityIdentifier.class);
        
        TYPE_MAPPINGS = UnmodifiableBidiMap.decorate(typeMappingBuilder);
        
    }
    
    /** Statis class, no public constructor */
    private GroupServiceProxyUtils() { }
    

    
    /**
     * Wraps a uPortal specific object with it's API layer counterpart.
     * 
     * @param realObj The object to attempt to wrap.
     * @return The wrapped object, or the same object that was passed in if no wrapper is setup.
     */
    public static Object wrap(final Object realObj) {
        if (realObj == null) {
            return null;
        }
        
        //Get the class type of the object to wrap
        final Class<? extends Object> realClass = realObj.getClass();
        
        if (realClass.isArray()) {
            return wrap((Object[])realObj);
        }
        
        if (Class.class.isAssignableFrom(realClass)) {
            return getWrappedType((Class<?>)realObj);
        }
        
        
        final Map<Class<?>, Class<?>> interfaceMatches = new HashMap<Class<?>, Class<?>>();
        Class<?> bestConcreteMatch = null;
        
        //Step through each mapped type
        for (final Map.Entry<Class<?>, Class<?>> mappedClassEntry : ((Map<Class<?>, Class<?>>)UP_TO_API_WRAPPER_MAPPINGS).entrySet()) {
            final Class<?> mappedClass = mappedClassEntry.getKey();
            
            //If the source class implements or extends the class from the map
            if (mappedClass.isAssignableFrom(realClass)) {
                //If the mapped class is an interface add it to the interface matches map with the wrapper class
                if (mappedClass.isInterface()) {
                    interfaceMatches.put(mappedClass, mappedClassEntry.getValue());
                }
                //If the mapped class is concrete and there is no match or a better match than the current match
                else if (bestConcreteMatch == null || bestConcreteMatch.isAssignableFrom(mappedClass)) {               
                    bestConcreteMatch = mappedClass;
                }
            }
        }
        
        //If a concrete wrapper was found use that to wrap the object
        if (bestConcreteMatch != null) {
            final Class<?> wrapperClass = (Class<?>)UP_TO_API_WRAPPER_MAPPINGS.get(bestConcreteMatch);
            
            try {
                final Class<?>[] cnstrArgTypes = { bestConcreteMatch };
                final Constructor<?> cnstr = wrapperClass.getConstructor(cnstrArgTypes);
                
                //Create an instance of the wrapper and pass in the real object
                final Object[] cnstrArgs = { realObj };
                final Object wrapper = cnstr.newInstance(cnstrArgs);
                
                return wrapper;
            }
            catch (final Exception e) {
                LOG.error("Error creating wrapper class '" + wrapperClass + "' for argument '" + realObj + "' of type '" + realClass + "'.", e);
                return realObj;
            }
        }
        
        //No concrete match and no wrapped interfaces, just return the object.
        if (interfaceMatches.size() == 0) {
            return realObj;
        }
        
        //No concrete match, and found wrapped interfaces, create a dynamic proxy
        
        //Set of all interfaces to proxy for the wrapped class
        final Set<Class<?>> proxyInterfaces = new HashSet<Class<?>>(interfaceMatches.values());
        proxyInterfaces.add(Wrapper.class);
        
        //Determine if there are any interfaces the class implements but are not mapped
        final Class<?>[] realInterfaces = realClass.getInterfaces();
        for (final Class<?> realInterface : realInterfaces) {
            boolean matchFound = false;
            
            for (final Class<?> matchingInterface :  interfaceMatches.keySet()) {
                if (realInterface.isAssignableFrom(matchingInterface)) {
                    matchFound = true;
                    break;
                }
            }
            
            if (!matchFound) {
                proxyInterfaces.add(realInterface);
            }
        }
        
        
        try {
            final GroupsWrapperProxyHandler groupsWrapperProxyHandler = new GroupsWrapperProxyHandler(realObj);
            final Class<?>[] wrapperInterfaceArray = proxyInterfaces.toArray(new Class<?>[proxyInterfaces.size()]);
            final Object proxy = Proxy.newProxyInstance(GroupServiceProxyUtils.class.getClassLoader(), wrapperInterfaceArray, groupsWrapperProxyHandler);
            return proxy;
        }
        catch (final Exception e) {
            LOG.error("Error creating wrapper proxy with interfaces '" + proxyInterfaces + "' for argument '" + realObj + "' of type '" + realClass + "'.", e);
            return realObj;
        }
    }
    
    
    /**
     * Takes an Object[] and returns a wrapped copy of it. The original array
     * is not modified. Uses the {@link #wrap(Object)} method to wrap the
     * members of the array.
     * 
     * @param realObjs The array to wrap.
     * @return A wrapped array.
     * @see #wrap(Object)
     */
    public static Object[] wrap(final Object[] realObjs) {
        if (realObjs == null) {
            return null;
        }
        
        Class<?> wrappedArrayType = null;
        
        final List<Object> wrappedObjs = new ArrayList<Object>(realObjs.length);
        
        for (final Object realObj : realObjs) {
            final Object wrappedObj = wrap(realObj);
            wrappedObjs.add(wrappedObj);
            
            if (wrappedArrayType == null && wrappedObj != null) {
                wrappedArrayType = wrappedObj.getClass();
            }
        }
        
        //Fallback to determine the real Class to create the Array for
        if (wrappedArrayType == null) {
            final Class<?> arrayType = realObjs.getClass().getComponentType();
            wrappedArrayType = getWrappedType(arrayType);
        }
        
        final Object[] wrappedObjArray = (Object[])Array.newInstance(wrappedArrayType, realObjs.length);
        return wrappedObjs.toArray(wrappedObjArray);
    }
    


    
    /**
     * Takes an object and attempts to use the {@link Wrapper#getWrappedObject()}
     * method to get the wrapped object. If the argument does not implement the
     * {@link Wrapper} interface it is returned as is.
     * 
     * @param wrappedObj The object to attempt to unwrap.
     * @return The unwrapped object or the passed object if it is not a Wrapper.
     */
    public static Object unwrap(final Object wrappedObj) {
        if (wrappedObj == null) {
            return null;
        }
        
        final Class<? extends Object> wrappedClass = wrappedObj.getClass();
        
        if (wrappedClass.isArray()) {
            return unwrap((Object[])wrappedObj);
        }
        
        if (Class.class.isAssignableFrom(wrappedClass)) {
            return getPortalType((Class<?>)wrappedObj);
        }
        
        if (LockType.class.isAssignableFrom(wrappedClass)) {
            return mapLockType((LockType)wrappedObj);
        }
        
        if (SearchMethod.class.isAssignableFrom(wrappedClass)) {
            return mapSearchMethod((SearchMethod)wrappedObj);
        }
        
        if (Wrapper.class.isAssignableFrom(wrappedClass)) {
            return ((Wrapper)wrappedObj).getWrappedObject();
        }

        return wrappedObj;
    }
    
    /**
     * Takes an Object[] and returns a unwrapped copy of it. The original array
     * is not modified.
     * 
     * @param wrappedObjs The array to unwrap.
     * @return An unwrapped array.
     * @see #unwrap(Object)
     */
    public static Object[] unwrap(final Object[] wrappedObjs) {
        if (wrappedObjs == null) {
            return null;
        }
        
        Class<? extends Object> realArrayType = null;
        
        //Unwrap the objects
        final List<Object> realObjs = new ArrayList<Object>(wrappedObjs.length);
        for (final Object wrappedObj : wrappedObjs) {
            final Object realObj = unwrap(wrappedObj);
            realObjs.add(realObj);
            
            if (realArrayType == null && realObj != null) {
                realArrayType = realObj.getClass();
            }
        }
        
        //Fallback to determine the real Class to create the Array for
        if (realArrayType == null) {
            final Class<?> arrayType = wrappedObjs.getClass().getComponentType();
            realArrayType = getPortalType(arrayType);
        }
        
        //Create the real array and put the unwrapped objects in it
        final Object[] realObjArray = (Object[])Array.newInstance(realArrayType, wrappedObjs.length);
        return realObjs.toArray(realObjArray);
    }
    
    /**
     * Maps a uPortal lock type <code>int</code> defined in {@link IEntityLockService}
     * to an API layer {@link LockType}.
     *  
     * @param typeNum The lock type number to map from.
     * @return The corresponding API layer lock type.
     */
    public static LockType mapLockType(final int typeNum) {
        final LockType type;
        
        if (IEntityLockService.READ_LOCK == typeNum) {
            type = LockType.READ_LOCK;
        }
        else if (IEntityLockService.WRITE_LOCK == typeNum) {
            type = LockType.WRITE_LOCK;
        }
        else {
            LOG.error("Unknown LockType number=" + typeNum);
            throw new IllegalArgumentException("Unknown LockType number=" + typeNum);
        }
        
        return type;
    }
   
    /**
     * Maps an API layer {@link SearchMethod} to a uPortal search method <code>int</code>
     * defined in {@link IGroupConstants}.
     * 
     * @param method The search method to map from.
     * @return The corresponding uPortal search method identifier.
     */
    public static int mapSearchMethod(final SearchMethod method) {
        final int methodNum;
        
        if (SearchMethod.IS.equals(method)) {
            methodNum = IGroupConstants.IS;
        }
        else if (SearchMethod.STARTS_WITH.equals(method)) {
            methodNum = IGroupConstants.STARTS_WITH;
        }
        else if (SearchMethod.ENDS_WITH.equals(method)) {
            methodNum = IGroupConstants.ENDS_WITH;
        }
        else if (SearchMethod.CONTAINS.equals(method)) {
            methodNum = IGroupConstants.CONTAINS;
        }
        else {
            LOG.error("Unknown SearchMethod=" + method);
            throw new IllegalArgumentException("Unknown SearchMethod=" + method);
        }
        
        return methodNum;
    }
    
    /**
     * Maps an API layer {@link LockType} to uPortal lock type <code>int</code>
     * defined in {@link IEntityLockService}.
     * 
     * @param type The lock type to map from.
     * @return The corresponding uPortal lock type identifier.
     */
    private static int mapLockType(final LockType type) {
        final int typeNum;
        
        if (LockType.READ_LOCK.equals(type)) {
            typeNum = IEntityLockService.READ_LOCK;
        }
        else if (LockType.WRITE_LOCK.equals(type)) {
            typeNum = IEntityLockService.WRITE_LOCK;
        }
        else {
            LOG.error("Unknown LockType=" + type);
            throw new IllegalArgumentException("Unknown LockType=" + type);
        }
        
        return typeNum;
    }
    
    private static Class<?> getPortalType(final Class<?> wrapperType) {
        final Class<?> portalType = doTypeWrapping(wrapperType, TYPE_MAPPINGS.inverseBidiMap());
        if (portalType != null) {
            return portalType;
        }
        
        return doTypeWrapping(wrapperType, UP_TO_API_WRAPPER_MAPPINGS.inverseBidiMap());
    }
    
    private static Class<?> getWrappedType(final Class<?> portalType) {
        final Class<?> wrappedType = doTypeWrapping(portalType, TYPE_MAPPINGS);
        if (wrappedType != null) {
            return wrappedType;
        }
        
        return doTypeWrapping(portalType, UP_TO_API_WRAPPER_MAPPINGS);
    }
    
    private static Class<?> doTypeWrapping(final Class<?> fromType, Map<Class<?>, Class<?>> typeMapping) {
        if (fromType == null) {
            return null;
        }
        
        //Attempt to resolve a mapped class
        final Class<?> baseClass = findBestMatch(fromType, typeMapping.keySet());
        final Class<?> mappedClass = typeMapping.get(baseClass);
        if (mappedClass == null) {
            //Log if a mapping wasn't found and return the source
            if (LOG.isDebugEnabled()) {
                LOG.debug("No Class mapped for: " + fromType);
            }
            return fromType;
        }
     
        //Log if a mapping was found and return the mapped class
        if (LOG.isDebugEnabled()) {
            LOG.debug("Mapped Class " + mappedClass + " found for Class " + fromType);
        }
        return mappedClass;
    }

    /**
     * Iterates over all of the classes in the Set and finds the closest match to the
     * source class via {@link Class#isAssignableFrom(Class)}.  
     */
    private static <T> Class<? extends T> findBestMatch(final Class<? extends T> srcClass, final Collection<Class<? extends T>> matchingClasses) {
        if (srcClass == null) {
            return null;
        }
        
        Class<? extends T> baseClass = null;
        
        //Step through each mapped type
        for (final Class<? extends T> mappedClass : matchingClasses) {
            //If the source class implements or extends the class from the map
            if (mappedClass.isAssignableFrom(srcClass)) {
                //check if we found a base already and
                //if the new match is not implemented or extended by the current match 
                if (baseClass == null || baseClass.isAssignableFrom(mappedClass)) {               
                    baseClass = mappedClass;
                }
            }
        }
        
        return baseClass;
    }
}
