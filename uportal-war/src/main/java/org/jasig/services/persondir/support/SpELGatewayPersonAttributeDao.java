package org.jasig.services.persondir.support;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.portal.spring.spel.IPortalSpELService;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.AccessException;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.PropertyAccessor;
import org.springframework.expression.TypedValue;
import org.springframework.expression.spel.support.StandardEvaluationContext;

/**
 * Gateway DAO that uses a SpEL expression to determine if the target DAO should be executed
 * 
 * @author Eric Dalquist
 */
public class SpELGatewayPersonAttributeDao extends AbstractDefaultAttributePersonAttributeDao {
    private IPortalSpELService portalSpELService;
    private String expression;
    private IPersonAttributeDao targetPersonAttributeDao = null;
    
    @Autowired
    public void setPortalSpELService(IPortalSpELService portalSpELService) {
        this.portalSpELService = portalSpELService;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public void setTargetPersonAttributeDao(IPersonAttributeDao targetPersonAttributeDao) {
        this.targetPersonAttributeDao = targetPersonAttributeDao;
    }

    @Override
    public Set<IPersonAttributes> getPeopleWithMultivaluedAttributes(Map<String, List<Object>> query) {
        final Expression spel = this.portalSpELService.parseExpression(this.expression);

        final StandardEvaluationContext context = new StandardEvaluationContext(query);
        context.addPropertyAccessor(GreedyMapAccessor.INSTANCE);
        final Boolean result = spel.getValue(context, Boolean.class);
        
        if (result != null && result) {
            if (logger.isDebugEnabled()) {
                logger.debug("Expression " + expression + " matched against query: " + query);
            }
            
            return this.targetPersonAttributeDao.getPeopleWithMultivaluedAttributes(query);
        }
        
        
        if (logger.isDebugEnabled()) {
            logger.debug("Expression " + expression + " did not match against query: " + query);
        }
        return null;
    }

    @Override
    public Set<String> getPossibleUserAttributeNames() {
        return this.targetPersonAttributeDao.getPossibleUserAttributeNames();
    }

    @Override
    public Set<String> getAvailableQueryAttributes() {
        return this.targetPersonAttributeDao.getAvailableQueryAttributes();
    }
    
    private static class GreedyMapAccessor implements PropertyAccessor {
        private static final GreedyMapAccessor INSTANCE = new GreedyMapAccessor();
        
        public boolean canRead(EvaluationContext context, Object target, String name) throws AccessException {
            return true;
        }

        public TypedValue read(EvaluationContext context, Object target, String name) throws AccessException {
            Map<?, ?> map = (Map<?, ?>) target;
            Object value = map.get(name);
            return new TypedValue(value);
        }

        public boolean canWrite(EvaluationContext context, Object target, String name) throws AccessException {
            return true;
        }

        public void write(EvaluationContext context, Object target, String name, Object newValue) throws AccessException {
            @SuppressWarnings("unchecked")
            Map<String, Object> map = (Map<String, Object>) target;
            map.put(name, newValue);
        }

        @SuppressWarnings("rawtypes")
        public Class[] getSpecificTargetClasses() {
            return new Class[] {Map.class};
        }
    }
}
