package org.synote.config

import java.util.Date;

import org.synote.user.User

class Configuration {
	
    /* 
     * Name of the configuration
     * e.g. org.synote.IBMTransServer.protocol
     * The name should be used in format of package arrangement
     */
	static mapping = {
		val:'text'	
	}
	String name
	
	/*
	 * Value of the configuration as String
	 */
	String val
	
	/*
	 * Value of the description string
	 */
	String description
	
	Date dateCreated 
	Date lastUpdated
	
    static constraints = {
		name(blank:false,unique:true,nullable:false,size:1..255)
		val(nullable:false,size:0..65536)
		description(nullable:true,size:0..255)	
    }
}
