/**
 * Copyright 2007 The JA-SIG Collaborative.  All rights reserved.
 * See license distributed with this file and
 * available online at http://www.uportal.org/license.html
 */
package edu.wisc.my.apilayer.impl.groups;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jasig.portal.concurrency.LockingException;
import org.jasig.portal.groups.GroupsException;

import edu.wisc.my.apilayer.groups.IEntityLock;

/**
 * @author Eric Dalquist
 * @version $Revision: 1.3 $
 */
public class GroupsWrapperProxyHandler implements InvocationHandler {
    private static final Log LOG = LogFactory.getLog(GroupsWrapperProxyHandler.class);
    
    //This will always be a org.jasig.* class
    private final Object proxiedGroupsApi;
    
    public GroupsWrapperProxyHandler(Object proxiedGroupsApi) {
        this.proxiedGroupsApi = proxiedGroupsApi;
    }

    /* (non-Javadoc)
     * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //Watch for Wrapper.getWrappedObject
        if (method.equals(Wrapper.class.getMethod("getWrappedObject"))) {
            return this.proxiedGroupsApi;
        }
        
        //Watch for Object.equals
        if (method.equals(Object.class.getMethod("equals", Object.class))) {
            return this.proxiedGroupsApi.equals(proxy);
        }
        
        //Convert parameter types from api-layer types to portal types 
        final Class<?>[] parameterTypes = method.getParameterTypes();
        final Class<?>[] unwrappedParameterTypes = (Class<?>[])GroupServiceProxyUtils.unwrap(parameterTypes);
        
        //Lookup the proxied method
        //TODO do I need a fallback here if proxiedClass is null?
        final Class<?> proxiedClass = (Class<?>)GroupServiceProxyUtils.unwrap(method.getDeclaringClass());
        final Method proxiedMethod = proxiedClass.getMethod(method.getName(), unwrappedParameterTypes);
        
        //Unwrap the arguments
        final Object[] unwrappedArgs = GroupServiceProxyUtils.unwrap(args);
        
        if (LOG.isDebugEnabled()) {
            LOG.debug("Executing Method [" + proxiedMethod + "] on [" + this.proxiedGroupsApi + "] with arguments " + toNiceString(unwrappedArgs) + " for invokation of Proxy Method [" + method + "] with arguments " + toNiceString(args));
        }
        
        //Invoke the method, catching and wrapping the exception if needed
        final Object result;
        try {
            result = proxiedMethod.invoke(this.proxiedGroupsApi, unwrappedArgs);
        }
        catch (final GroupsException ge) {
            throw new GroupsExceptionImpl(ge);
        }
        catch (final LockingException le) {
            throw new LockingExceptionImpl(le);
        }
        
        //Special handling for wrapping an int return type
        if (method.equals(IEntityLock.class.getMethod("getLockType"))) {
            return GroupServiceProxyUtils.mapLockType((Integer)result);
        }
        
        //Wrap the results before returning
        return GroupServiceProxyUtils.wrap(result);
    }

    private static String toNiceString(Object[] array) {
        if (array == null) {
            return String.valueOf(array);
        }
        
        return Arrays.asList(array).toString();
    }
}
