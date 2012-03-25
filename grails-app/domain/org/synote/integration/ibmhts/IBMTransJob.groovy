package org.synote.integration.ibmhts

import org.synote.user.User
import org.synote.resource.Resource

class IBMTransJob {

	static belongsTo = [owner: User, resource: Resource ]
	
	String title
	String jobId
	String url
	int status
	String transcript
	String vsxmlTranscript
	String xmlTranscript
	String editUrl
	Date dateCreated
	Date lastUpdated
	boolean saved
	
	static mapping = {
		transcript type: 'text'
		vsxmlTranscript type: 'text'
		xmlTranscript type:'text'
	}
	
	static constraints = {
		title(blank: false, size:1..255)
		jobId(blank: false, size:1..255)
		url(blank: false, size:1..255)
		status(nullable: false)
		transcript(nullable: true)
		vsxmlTranscript(nullable:true)
		xmlTranscript(nullable:true)
		editUrl(blank: false,size:1..255)
		saved(nullable:false)
	}
}
