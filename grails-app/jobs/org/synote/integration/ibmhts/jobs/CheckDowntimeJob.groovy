package org.synote.integration.ibmhts.jobs

import org.synote.integration.ibmhts.IBMTransJobService
import org.synote.integration.ibmhts.exception.IBMTransJobException

import java.net.Socket
import java.net.InetAddress

class CheckDowntimeJob {
	
	def IBMTransJobService
	
	def group = "IBMHTS"
	//def name = "CheckDowntimeJob" // execute every 5 mins 
	
	static triggers = {
	//	simple name:"simpleCheckDowntimeJobTrigger", startDelay:2000, repeatInterval:30000, repeatCount:-1
	}

    def execute() {
		log.debug "CheckDowntime Job"
        if(Boolean.valueOf(IBMTransJobService.getIBMTransJobEnabled()))
		{
			log.debug "IBM HTS enabled"
			def ibmhtsHostname = IBMTransJobService.getIBMServerName()
			def ibmhtsHostport = IBMTransJobService.getIBMServerPort()
			try
			{
				boolean connected = IBMTransJobService.getConnected()
				log.debug "Connected:"+connected
				if(connected && IBMTransJobService.isWithinDowntime())
				{
					IBMTransJobService.logoutIBM()
					log.info "log out of IBM server"
					//TODO: send email: patching time
					//TODO: Record unfinished jobs and send them by email
				}
				//If the time is within the stopAddingJobs time span and it is connected and allowAddingJobs is still true, set the allowAddingJobs to false
				else if(connected && IBMTransJobService.getAllowAddingJobs() && IBMTransJobService.withinStopAddingJobsTime())
				{
					IBMTransJobService.setAllowAddingJobs(false)
					log.info "set allow adding jobs false"
				}
				//We can set the isWithinDowntime a little bit longer, so that the loginIBM()
				//won't generate too many logs.
				else if(!connected && !IBMTransJobService.isWithinDowntime())
				{
					log.info "log into IBM server"
					IBMTransJobService.loginIBM()
				}
				else if(connected)
				{
					log.debug "update jobs"
					IBMTransJobService.updateJobs()
					
				}
			}
			catch(java.net.ConnectException conex)
			{
				boolean wasConnected = IBMTransJobService.getConnected()
				IBMTransJobService.setConnected(false)
				conex.printStackTrace()
				log.error "java.net.ConnectException:"+conex.getMessage()
				if(!IBMTransJobService.isWithinDowntime() && wasConnected)
				{
					log.error "Lose connection to IBM HTS server: ${ibmhtsHostname}:${ibmhtsHostport}"
					//TODO: send email
				}
				log.error "Cannot connect to IBM HTS server: ${ibmhtsHostname}:${ibmhtsHostport}"
			}
			catch(java.io.IOException ioe)
			{
				//boolean wasConnected = IBMTransJobService.getConnected()
				//IBMTransJobService.setConnected(false)
				ioe.printStackTrace()
				log.error "java.net.IOException:"+ioe.getMessage()
				log.error "Invalid response from IBM HTS server: ${ibmhtsHostname}:${ibmhtsHostport}"
			}
			catch(java.net.UnknownHostException uhex)
			{
				//TODO: how to handle this exception, we'd better check it when the host name is set
				boolean wasConnected = IBMTransJobService.getConnected()
				IBMTransJobService.setConnected(false)
				log.error "java.net.UnknownHostException:"+uhex.getMessage()
				uhex.printStackTrace()
				if(!IBMTransJobService.isWithinDowntime() && wasConnected)
				{
					log.error "Lose connection to IBM HTS server: ${ibmhtsHostname}:${ibmhtsHostport}"
					//TODO: send email
				}
				log.error "Cannot recognize host ${ibmhtsHostname}, please check again"	
			}
			catch(IBMTransJobException ibmjex)
			{
				log.error ibmjex.getMessage()
				//TODO: send email
			}
		}
	}
}
