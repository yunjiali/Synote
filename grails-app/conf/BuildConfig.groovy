grails.project.class.dir = "target/classes"
grails.project.test.class.dir = "target/test-classes"
grails.project.test.reports.dir	= "target/test-reports"
//grails.project.war.file = "target/${appName}-${appVersion}.war"

//ivy cache location
grails.dependency.cache.dir = "${userHome}/.grails/ivy-cache/${grailsVersion}" 

//copy the jena configuration files to the war
//grails.war.resources = { stagingDir, args ->
//	copy(file: "sdb-mysql-innodb-prod.ttl", tofile: "${stagingDir}/sdb-mysql-innodb-prod.ttl")
//}

grails.project.dependency.resolution = {
    // inherit Grails' default dependencies
    inherits( "global" ) {
        // uncomment to disable ehcache
        // excludes 'ehcache'
		
		/*Add the following excludes because after installing jena dependecies, there are some libary crash:
		 * 
		 * Error executing script Clean: loader constraint violation: when resolving overridden method 
		 * "org.apache.xerces.jaxp.SAXParserImpl.getParser()Lorg/xml/sax/Parser;" 
		 * the class loader (instance of org/codehaus/groovy/grails/cli/support/GrailsRootLoader) of the current class, 
		 * org/apache/xerces/jaxp/SAXParserImpl, and its superclass loader (instance of <bootloader>), 
		 * have different Class objects for the type org/xml/sax/Parser used in the signature
		 */
		excludes "xml-apis","xmlparserAPIs"
    }
    log "warn" // log level of Ivy resolver, either 'error', 'warn', 'info', 'debug' or 'verbose'
    repositories {        
        grailsPlugins()
        grailsHome()
		grailsCentral()

        // uncomment the below to enable remote dependency resolution
        // from public Maven repositories
        //mavenLocal()
        mavenCentral()
		//mavenRepo "http://mvnrepository.com"
        mavenRepo "http://snapshots.repository.codehaus.org"
        //mavenRepo "http://repository.codehaus.org"
        //mavenRepo "http://download.java.net/maven/2/"
        mavenRepo "http://repository.jboss.com/maven2/"
    }
    dependencies {
        // specify dependencies here under either 'build', 'compile', 'runtime', 'test' or 'provided' scopes eg.

        // runtime 'mysql:mysql-connector-java:5.1.5'
		//runtime 'com.hp.hpl.jena:jena:2.5.5'
		runtime ('com.hp.hpl.jena:jena:2.6.4')
		runtime ('com.hp.hpl.jena:arq:2.8.7'){
			excludes "lucene-core"	//searchable plugin has higher version lucene 2.4 than this dependency lucene 2.3.1. In 2.3.1 Field.Index.NOT_ANALYZED is not defined
		}
		runtime ('com.hp.hpl.jena:sdb:1.3.4')
		runtime "org.codehaus.gpars:gpars:0.11"
		runtime 'net.sf.jtidy:jtidy:r938'
		runtime 'net.sourceforge.htmlunit:htmlunit:2.9'
		compile('org.codehaus.groovy.modules.http-builder:http-builder:0.5.2') {
			excludes "commons-logging", "xml-apis", "groovy"
		}
    }

}
