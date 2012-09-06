package org.jasig.services.persondir.support;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jasig.portal.spring.spel.IPortalSpELService;
import org.jasig.services.persondir.IPersonAttributeDao;
import org.jasig.services.persondir.IPersonAttributes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

@RunWith(MockitoJUnitRunner.class)
public class SpELGatewayPersonAttributeDaoTest {
    @InjectMocks private SpELGatewayPersonAttributeDao spELGatewayPersonAttributeDao = new SpELGatewayPersonAttributeDao();
    @Mock private IPortalSpELService portalSpELService;
    @Mock private IPersonAttributeDao targetPersonAttributeDao;
    
    @Before
    public void setup() {
        final ExpressionParser expressionParser = new SpelExpressionParser();
        
        when(portalSpELService.parseExpression(anyString())).thenAnswer(new Answer<Expression>() {
            @Override
            public Expression answer(InvocationOnMock invocation) throws Throwable {
                final String expressionSttr = (String)invocation.getArguments()[0];
                return expressionParser.parseExpression(expressionSttr);
            }
        });
    }
    
    @Test
    public void testSpELMatchExists() {
        spELGatewayPersonAttributeDao.setExpression("username != null");
        
        final Map<String, List<Object>> query = ImmutableMap.of("username", Arrays.<Object>asList("dalquist"));
        
        when(targetPersonAttributeDao.getPeopleWithMultivaluedAttributes(query)).thenReturn(ImmutableSet.<IPersonAttributes>of());
        
        final Set<IPersonAttributes> result = spELGatewayPersonAttributeDao.getPeopleWithMultivaluedAttributes(query);
        
        assertNotNull(result);
    }
    
    @Test
    public void testSpELMatchNotContains() {
        spELGatewayPersonAttributeDao.setExpression("username.?[contains('@')].size() == 0");
        
        final Map<String, List<Object>> query = ImmutableMap.of("username", Arrays.<Object>asList("dalquist", "edalquist"));
        
        when(targetPersonAttributeDao.getPeopleWithMultivaluedAttributes(query)).thenReturn(ImmutableSet.<IPersonAttributes>of());
        
        final Set<IPersonAttributes> result = spELGatewayPersonAttributeDao.getPeopleWithMultivaluedAttributes(query);
        
        assertNotNull(result);
    }
    
    @Test
    public void testSpELMatchContains() {
        spELGatewayPersonAttributeDao.setExpression("username.?[contains('@')].size() > 0");
        
        final Map<String, List<Object>> query = ImmutableMap.of("username", Arrays.<Object>asList("dalquist", "dalquist@wisc.edu"));
        
        when(targetPersonAttributeDao.getPeopleWithMultivaluedAttributes(query)).thenReturn(ImmutableSet.<IPersonAttributes>of());
        
        final Set<IPersonAttributes> result = spELGatewayPersonAttributeDao.getPeopleWithMultivaluedAttributes(query);
        
        assertNotNull(result);
    }
    
    @Test
    public void testSpELNoMatch() {
        spELGatewayPersonAttributeDao.setExpression("username != null");
        
        final Map<String, List<Object>> query = ImmutableMap.of("email", Arrays.<Object>asList("dalquist@example.com"));
        
        when(targetPersonAttributeDao.getPeopleWithMultivaluedAttributes(query)).thenReturn(ImmutableSet.<IPersonAttributes>of());
        
        final Set<IPersonAttributes> result = spELGatewayPersonAttributeDao.getPeopleWithMultivaluedAttributes(query);
        
        assertNull(result);
    }
}
