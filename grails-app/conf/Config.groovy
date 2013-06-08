// locations to search for config files that get merged into the main config
// config files can either be Java properties files or ConfigSlurper scripts

// grails.config.locations = [ "classpath:${appName}-config.properties",
//                             "classpath:${appName}-config.groovy",
//                             "file:${userHome}/.grails/${appName}-config.properties",
//                             "file:${userHome}/.grails/${appName}-config.groovy"]

// if(System.properties["${appName}.config.location"]) {
//    grails.config.locations << "file:" + System.properties["${appName}.config.location"]
// }
grails.mime.file.extensions = true // enables the parsing of file extensions from URLs into the request format
grails.mime.use.accept.header = true
grails.mime.types = [ html: ['text/html','application/xhtml+xml'],
                      xml: ['text/xml', 'application/xml'],
                      text: 'text/plain',
                      js: 'text/javascript',
                      rss: 'application/rss+xml',
                      atom: 'application/atom+xml',
                      css: 'text/css',
                      csv: 'text/csv',
					  vtt: 'text/vtt',
                      all: '*/*',
                      json: ['application/json','text/json'],
					  rdf: ['application/rdf+xml','rdf/xml'],
                      form: 'application/x-www-form-urlencoded',
                      multipartForm: 'multipart/form-data'
                    ]
// The default codec used to encode data with ${}
grails.views.default.codec="none" // none, html, base64
grails.views.gsp.encoding="UTF-8"
grails.converters.encoding="UTF-8"
// enable Sitemesh preprocessing of GSP pages
grails.views.gsp.sitemesh.preprocess = false
// scaffolding templates configuration
grails.scaffolding.templates.domainSuffix = 'Instance'

// Set to false to use the new Grails 1.2 JSONBuilder in the render method
grails.json.legacy.builder=false
// enabled native2ascii conversion of i18n properties files
grails.enable.native2ascii = true
// whether to install the java.util.logging bridge for sl4j. Disable fo AppEngine!
grails.logging.jul.usebridge = true
// packages to include in Spring bean scanning
grails.spring.bean.packages = []

//Grails Database Migration plugin configuration
grails.plugin.databasemigration.changelogFileName = "changelog.xml"
// set per-environment serverURL stem for creating absolute links
environments {
    production {
        grails.serverURL = "http://linkeddata.synote.org"
		jena { //deprecated
			enabled = false
			sdb{
				assembler = "sdb-mysql-innodb-prod.ttl"
				checkFormattedOnStartUp = true
				emptyOnStartUp = false
			}
        }
    }
    development {
        grails.serverURL = "http://localhost:8080/${appName}"
		jena { //deprecated
			enabled = false
			sdb{
				assembler = "sdb-mysql-innodb-dev.ttl"
				checkFormattedOnStartUp = false //Just check! Not reformat!
				emptyOnStartUp = false //empty the triple store on start up
			}
		}
    }
    test {
        grails.serverURL = "http://localhost:8080/${appName}"
		//Used for tests that need existing user
		synote.test.account.userName = "test"
		synote.test.account.password = "a"
		synote.test.account.role = "ROLE_NORMAL"
		synote.test.account.userId = "7"
		//Used for creating new account
		synote.test.create.user.userName = "test12349012934test" //In case the user name has been taken
		synote.test.create.user.password = "a" //In case we add password constrain in the future
		
		synote.test.admin.userName="admin"
		synote.test.admin.password="a"
		synote.test.admin.role="ROLE_ADMIN"
		synote.test.admin.userId = "1"
    }

}

// log4j configuration
log4j = {
    // Example of changing the log pattern for the default console
    // appender:
    //
	development{
		
		appenders {
			console name:'stdout',layout:pattern(conversionPattern:'%p %c{5} %m%n')
		}
		error   'org.codehaus.groovy.grails.web.pages', //  GSP
				'org.codehaus.groovy.grails.web.sitemesh', //  layouts
				'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				'org.codehaus.groovy.grails.web.mapping', // URL mapping
				'org.codehaus.groovy.grails.commons', // core / classloading
				'org.codehaus.groovy.grails.plugins', // plugins
				'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
				'org.springframework',
				'org.hibernate',
				'net.sf.ehcache.hibernate',
				'org.synote.permission.PermService'
				//,'org.codehaus.groovy.grails.web.servlet',  //  controllers
		warn   'org.mortbay.log' 
		//info 	'org.synote.integration.ibmhts'
		debug   'grails.app.services.org.synote.user.SecurityService',
				'grails.app.services.org.synote.linkeddata.LinkedDataService',
				//'org.synote.user.SynoteLdapAuthenticator',
			   //'groovy.grails.plugins.springsecurity.GrailsDaoImpl',
		       //'org.synote.user.SynoteLdapAuthenticationProcessingFilter',
			   //'org.synote.user.AutoCreateLdapUserDetailsMapper',
			   'org.synote.user.SynoteAuthenticationProvider',
			   'org.synote.user.SynoteAPITokenFilter',
			   'org.synote.user.SynoteAPILoginFilter',
			   'org.synote.search.GoogleCrawlFilter',
		       'grails.app.services.org.synote.player.server.PlayerService',
			   'grails.app.services.org.synote.resource.ResourceService',
		       'grails.app.services.org.synote.search.resource.ResourceSearchService',
		       'grails.app.services.org.synote.integration.ibmhts.IBMTransJobService',
			   'grails.app.services.org.synote.search.sitemap.SitemapService',
			   'grails.app.services.org.synote.linkeddata.DataDumpService',
			   'grails.app.controllers.org.synote.resource.compound.MultimediaController',
			   'grails.app.controllers.org.synote.linkeddata.LinkedDataController',
			   'grails.app.controllers.org.synote.user.admin.AdminController',
			   'grails.app.controllers.org.synote.api.ApiController',
		       'grails.app.task'
	}
	
	production{
		def catalinaBase = System.properties.getProperty('catalina.base')
		if (!catalinaBase) catalinaBase = '.'   // just in case
		def logDirectory = "${catalinaBase}/logs"
		
	    appenders {
			//appender new org.apache.log4j.DailyRollingFileAppender(name:'synote_info', file:'logs/synote_info.log', threshold: org.apache.log4j.Level.INFO,datePattern:"'.'yyyy-MM-dd", layout:pattern(conversionPattern:'[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
			appender new org.apache.log4j.DailyRollingFileAppender(name:'synote_log', file:"${logDirectory}/synote.log".toString(), threshold: org.apache.log4j.Level.INFO,datePattern:"'.'yyyy-MM-dd", layout:pattern(conversionPattern:'[%d{yyyy-MM-dd hh:mm:ss.SSS}] %p %c{5} %m%n'))
			file name:'synote_stacktrace', file:"${logDirectory}/synote_stacktrace.log", append:false
			'null' name:'stacktrace'			
	    }

	    error  synote_log:'org.codehaus.groovy.grails.web.servlet'  //  controllers
		error  synote_log:'org.codehaus.groovy.grails.web.pages' //  GSP
		error  synote_log:'org.codehaus.groovy.grails.web.sitemesh' //  layouts
		error  synote_log:'org.codehaus.groovy.grails.web.mapping.filter' // URL mapping
		error  synote_log:'org.codehaus.groovy.grails.web.mapping' // URL mapping
		error  synote_log:'org.codehaus.groovy.grails.commons' // core / classloading
		error  synote_log: 'org.codehaus.groovy.grails.plugins' // plugins
		error  synote_log:'org.codehaus.groovy.grails.orm.hibernate' // hibernate integration
		error  synote_log:'org.springframework'
		error  synote_log:'org.hibernate'
	    error  synote_log:'net.sf.ehcache.hibernate'
		error  synote_log:'grails.app.task'
		error  synote_stacktrace:'StackTrace'
	    warn   synote_log:'org.mortbay.log'
		info   synote_log:'grails.app'
		/*root {
			warn 'synote_log',stdout
			//debug 'synote_debug',stdout
			additivity=true
		}*/
	}
	
	test {
		error  'org.codehaus.groovy.grails.web.servlet',  //  controllers
				'org.codehaus.groovy.grails.web.pages', //  GSP
				'org.codehaus.groovy.grails.web.sitemesh', //  layouts
				'org.codehaus.groovy.grails.web.mapping.filter', // URL mapping
				'org.codehaus.groovy.grails.web.mapping', // URL mapping
				'org.codehaus.groovy.grails.commons', // core / classloading
				'org.codehaus.groovy.grails.plugins', // plugins
				'org.codehaus.groovy.grails.orm.hibernate', // hibernate integration
				'org.springframework',
				'org.hibernate',
				'net.sf.ehcache.hibernate'
		
		warn   'org.mortbay.log'
	}
}

//log4j.logger.org.springframework.security='off,stdout'

//spring security configuration
grails.plugins.springsecurity.securityConfigType = "Annotation"
grails.plugins.springsecurity.userLookup.userDomainClassName = "org.synote.user.User"
grails.plugins.springsecurity.userLookup.usernamePropertyName='userName'
grails.plugins.springsecurity.userLookup.passwordPropertyName = 'password'
grails.plugins.springsecurity.authority.className = "org.synote.user.UserRole"
grails.plugins.springsecurity.userLookup.authorityJoinClassName = 'org.synote.user.UserRolePeople'
grails.plugins.springsecurity.ui.register.defaultRoleNames=['ROLE_NORMAL']
grails.plugins.springsecurity.useSecurityEventListener = true
grails.plugins.springsecurity.password.algorithm='SHA-1'

//TODO: securityService passwordEncoder
//@Secure annotation

//useLdap = true
//ldapRetrieveDatabaseRoles = true
//ldapRetrieveGroupRoles = false
//ldapServer = 'ldaps://nlbldap.soton.ac.uk:636/'
//ldapSearchBase='ou=user,dc=soton,dc=ac,dc=uk'
//ldapSearchFilter='(sAMAccountName={0})'
//ldapUsePassword=false

grails.plugins.springsecurity.providerNames=['synoteAuthenticationProvider',
			   'anonymousAuthenticationProvider',
			   'rememberMeAuthenticationProvider']

grails.plugins.springsecurity.successHandler.defaultTargetUrl='/login/authSuccess'
grails.plugins.springsecurity.successHandler.alwaysUseDefaultTargetUrl=true
grails.plugins.springsecurity.logout.afterLogoutUrl='/login/index'

     

