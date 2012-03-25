package org.synote.user;

import org.apache.log4j.Logger;
import org.springframework.security.providers.ldap.authenticator.BindAuthenticator;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ldap.SpringSecurityContextSource;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.SpringSecurityLdapTemplate;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.dao.DataAccessException;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.DirContextOperations;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.util.Assert;

import org.codehaus.groovy.grails.plugins.springsecurity.AuthorizeTools;
import javax.naming.directory.DirContext;

import org.apache.directory.groovyldap.*;

import org.synote.config.exception.SynoteLdapConfigException;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.text.MessageFormat;
import java.util.Map;

/**
 * 
 * @author Rabbit
 * Provide ldap authentication function for synote. This class is specifically
 * customized for ldap users who need anonymous bind, which acegi plugin 0.5.3
 * doesn't support
 */
class SynoteLdapAuthenticator extends BindAuthenticator{
	
	/*
	 * Add logger in log4j
	 */
	private Logger log = Logger.getLogger(getClass());
	
	public SynoteLdapAuthenticator(SpringSecurityContextSource contextSource) {
		super(contextSource);
	}
	
	public DirContextOperations authenticate(Authentication authentication) {
	    log.debug("Authentication starts");

	    Map conf = (Map)AuthorizeTools.getSecurityConfig().get("security");
	    log.debug("conf class name:"+conf.getClass().getName());
		String ldapSearchFilter =(String)conf.get("ldapSearchFilter");
		String ldapSearchBase = (String)conf.get("ldapSearchBase");
		String ldapServer = (String)conf.get("ldapServer");
		log.debug("ldapSearchFilter:"+ldapSearchFilter);
		log.debug("ldapSearchBase:"+ldapSearchBase);
		log.debug("ldapServer:"+ldapServer);
		//if any one of them is empty through exceptions
		
	    DirContextOperations user = null;
	    Assert.isInstanceOf(UsernamePasswordAuthenticationToken.class, authentication,
	         "Can only process UsernamePasswordAuthenticationToken objects");

	    String username = authentication.getName();
	       
	    String password = (String)authentication.getCredentials();
	    log.debug("username:"+username);
	    log.debug("password:"+password);
	    log.debug("principal:"+ authentication.getPrincipal());

	    // If DN patterns are configured, try authenticating with them directly
	    Iterator dns = getUserDns(username).iterator();
		log.debug("has full user dn in config?"+dns.hasNext());
	    while (dns.hasNext() && user == null) {
	    	String dnsStr = (String)dns.next();
	        log.debug("userDN:"+dnsStr);
	        user = bindWithDn(dnsStr, username, password);
	    }

	    // Otherwise use the configured locator to find the user
	    // and authenticate with the returned DN.
	    if (user == null && getUserSearch() != null) {
	    	//((FilterBasedLdapUserSearch)getUserSearch()).setDerefLinkFlag(true);
	        log.debug("LdapUserSearch:"+getUserSearch().toString());
        	LDAP ldap = getLDAPConnection(ldapServer);
			Object[] names = new String[]{username};
        	String searchCn = MessageFormat.format(ldapSearchFilter, names);
        	log.debug("final search cn:"+searchCn);
        	try
        	{
	            List resultList = ldap.search(searchCn,ldapSearchBase, SearchScope.SUB);
	            log.debug(resultList.size()+" user(s) with username "+username+" are found");
					
				//In case many users share the same username
				Iterator<HashMap> results = resultList.iterator();
				while(results.hasNext() && user==null) {
					String userDn = results.next().get("dn").toString();
					user=bindWithDn(userDn, username, password);
				}
        	}
        	catch(javax.naming.NamingException nex)
        	{
        		SynoteLdapConfigException ex =  new SynoteLdapConfigException(
        				"naming exceptions for [username:"+username+
        				",searchFileter:"+ldapSearchFilter+",searchBase:"+ldapSearchBase+"] nested message:"
        				+nex.getMessage());
        	}
	    }
	    if (user == null) {
	       log.debug(messages.getMessage("BindAuthenticator.badCredentials", "Bad credentials"));
	       throw new BadCredentialsException(messages.getMessage("BindAuthenticator.badCredentials", "Bad credentials"));
	    }

	        return user;
	}

	private LDAP getLDAPConnection(String ldapServer)
	{
		LDAP conn = LDAP.newInstance(ldapServer);
		if(conn==null)
			throw new SynoteLdapConfigException("Cannot connect to ldap server:"+ldapServer);
		return conn;
	}
	private DirContextOperations bindWithDn(String userDn, String username, String password) {
	        SpringSecurityLdapTemplate template = new SpringSecurityLdapTemplate(
	        		new BindWithSpecificDnContextSource((SpringSecurityContextSource) getContextSource(), userDn, password));

	        try {
	        	if(getUserAttributes()!=null)
	        	{
		        	for(String attr : getUserAttributes())
		        	{
		        		System.out.println("bindWithDn, userAttr:"+attr);
		        	}
	        	}
	        	DirContextOperations dir = template.retrieveEntry(userDn, getUserAttributes());
	        	return dir;

	        } catch (BadCredentialsException e) {
	            // This will be thrown if an invalid user name is used and the method may
	            // be called multiple times to try different names, so we trap the exception
	            // unless a subclass wishes to implement more specialized behaviour.
	            handleBindException(userDn, username, e.getCause());
	        }

	        System.out.println("return null?");
	        return null;
	    }

	/**
	 * Allows subclasses to inspect the exception thrown by an attempt to bind with a particular DN.
	 * The default implementation just reports the failure to the debug log.
	 */
	protected void handleBindException(String userDn, String username, Throwable cause) {
	   log.debug("Failed to bind as " + userDn + ": " + cause);
	}
	
	private class BindWithSpecificDnContextSource implements ContextSource {
	    private SpringSecurityContextSource ctxFactory;
	    DistinguishedName userDn;
	    private String password;

		public BindWithSpecificDnContextSource(SpringSecurityContextSource ctxFactory, String userDn, String password) {
		        this.ctxFactory = ctxFactory;
		        this.userDn = new DistinguishedName(userDn);
		        this.userDn.prepend(ctxFactory.getBaseLdapPath());
		        this.password = password;
		}

	    public DirContext getReadOnlyContext() throws DataAccessException {
	        return ctxFactory.getReadWriteContext(userDn.toString(), password);
	    }

	    public DirContext getReadWriteContext() throws DataAccessException {
	        return getReadOnlyContext();
	    }
	}
}
