package org.synote.user;

import javax.servlet.http.HttpServletRequest 
import org.apache.log4j.Logger

import org.codehaus.groovy.grails.plugins.springsecurity.SecurityRequestHolder 
import org.codehaus.groovy.grails.plugins.springsecurity.ldap.GrailsLdapUserDetailsMapper 
import org.springframework.ldap.core.DirContextOperations 
import org.springframework.security.GrantedAuthority 
import org.springframework.security.userdetails.UserDetails 
import org.springframework.security.userdetails.UsernameNotFoundException 

class AutoCreateLdapUserDetailsMapper extends GrailsLdapUserDetailsMapper {
	static final String LDAP_AUTOCREATE_CURRENT_AUTHORITIES = 'LDAP_AUTOCREATE_CURRENT_AUTHORITIES'
	static final String LDAP_AUTOCREATE_CURRENT_CTX = 'LDAP_AUTOCREATE_CURRENT_CTX'
	private Logger log = Logger.getLogger(getClass());
	
	@Override 
	UserDetails mapUserFromContext(DirContextOperations ctx, String username, GrantedAuthority... authorities) { 
		try {
			log.debug "Authorities:"+authorities
			log.debug "ctx Dn:"+ctx.getDn()
			log.debug "ctx attribute size:"+ctx.getAttributes().size()
			//ctx.getAttributes().getIDs.each{ id->
			//	log.debug id+":"+ctx.getStringAttribute(id).toString()
			//}
			log.debug "email:"+ctx.getObjectAttribute("mail")
			log.debug "sn:"+ctx.getObjectAttribute("sn")
			log.debug "givenName:"+ctx.getObjectAttribute("givenName")
			log.debug "memberOf:"+ctx.getObjectAttribute("memberOf")
			
			return super.mapUserFromContext(ctx, username, authorities) 
		} 
		catch (UsernameNotFoundException e) { 
			HttpServletRequest request = SecurityRequestHolder.request 
			request.session[LDAP_AUTOCREATE_CURRENT_AUTHORITIES] = authorities
			request.session[LDAP_AUTOCREATE_CURRENT_CTX] = ctx
			throw e 
		} 
	} 
}
