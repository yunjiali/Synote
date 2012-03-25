package org.synote.integration.entermedia

import org.apache.commons.logging.LogFactory;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Iterator;

import junit.framework.TestCase;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

class EnterMediaService {

    static transactional = true
	
	private HttpClient fieldClient;
	private String key;
	//private static String catalogid= "/media/catalogs/synote"
	//private static String sourcepath = "users/synote/"
	
	private static final log = LogFactory.getLog(this)
	def configurationService
	

   /**
	 * The web services API require a client to log first. 
	 * The login is the same as one used within the EnterMedia usermanager
	 * There are two Cookies that need to be passed in on subsequent requests
	 * 1. JSESSIONID - This is used by resin or similar Java container. Enables short term sessions on the server
	 * 2. entermedia.key - This allows the user to be auto-logged in. Useful for long term connections. 
	 * 	  If the web server is restarted then clients don't need to log in again
	 * @throws Exception
	 */
	public HttpClient getClient() throws Exception
	{
		
		if (fieldClient == null)
		{
			fieldClient = new HttpClient();
			//post url:
			//org.synote.integration.entermedia.host+org.synote.integration.entermedia.restapi+org.synote.integration.entermedia.login
			String postURL = getEntermediaHost()+getRestAPI()+getEntermediaLogin()
			PostMethod method = new PostMethod(postURL); //"http://localhost:8081/media/services/rest/login.xml");
			method.addParameter(new NameValuePair("accountname", getEntermediaUsername()));
			method.addParameter(new NameValuePair("password", getEntermediaPassword()));
			method.addParameter(new NameValuePair("catalogid",getCatalogId()));

			int statusCode = fieldClient.executeMethod(method);
			
			if(200 == statusCode)
			{
				log.debug("login successful")
				
			}
			else
			{
				log.debug("login failed!")
			}
			Element root = getXml(method.getResponseBodyAsStream())
			log.debug("login xml:"+root)

			String ok = root.attributeValue("stat");
			Element keyElem = (Element)root.elementIterator().next();
			key = keyElem.getText()
		}
		return fieldClient;
	}
	
	/**
	* Do a single file upload using MultiPart HTTP upload
	* The asset that is created will be returned with an asset "id"
	* @throws Exception
	*/
	public String uploadFile(File f, String title) throws Exception
	{
		//Upload the file
		//post url: org.synote.integration.entermedia.host+org.synote.integration.entermedia.restapi+org.synote.integration.entermedia.uploadFile+?catalogid=+org.synote.integration.entermedia.catalogid
		String postURL = getEntermediaHost()+getRestAPI()+getUploadFile()+"?catalogid="+getCatalogId()
		PostMethod method = 
			new PostMethod(postURL);
		
		method.addRequestHeader("Cookie", "entermedia.key=" + key);
	   
		Part[] parts = [ new FilePart("file", f.getName(), f), 
			new StringPart("sourcepath", getSourcePath())];
	   
	   	method.setRequestEntity( new MultipartRequestEntity(parts, method.getParams()) );
		   
		int statusCode = getClient().executeMethod(method);
		if(200==statusCode)
		{
			log.debug("uploading successful")	
		}
		
		/*sample return:
		 * <rsp stat="ok">
				<asset id="107" sourcepath="users/synote/0588e818-3cb1-49b6-8a81-d99e0310c224synotedemo.wmv"/>
			</rsp>
			So you can get sourcepath from this return
		 */
	    /*def inputStream = method.getResponseBodyAsStream()
		StringWriter writer = new StringWriter();
		IOUtils.copy(inputStream, writer, null);
		String theString = writer.toString();
		println "upload return:"+theString*/
		
		//return url:
		//org.synote.integration.entermedia.host+org.synote.integration.entermedia.catalogid+org.synote.integration.entermedia.fileDownload+org.synote.integration.entermedia.sourcepath/f.getName()
		return getEntermediaHost()+getCatalogId()+"/"+getFileDownload()+"/"+getSourcePath()+f.getName()+"/"+f.getName()
		//"http://localhost:8081/media/catalogs/synote/downloads/originals/users/synote/"+f.getName()
   }
	
	protected Element getXml(InputStream inXmlReader)
	{
		try
		{
			SAXReader reader = new SAXReader();
			reader.setEncoding("UTF-8");
			Document document = reader.read(new InputStreamReader(inXmlReader,"UTF-8"));
			Element root = document.getRootElement();
			return root;
		}
		catch (Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
	
	public String getEntermediaEnabled()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.enabled")
	}
	
	public String getEntermediaUsername()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.username")
	}
	public String getEntermediaPassword()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.password")
	}
	public String getCatalog()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.catelog")
	}
	public String getEntermediaHost()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.host")
	}
	
	public String getRestAPI()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.restapi")
	}
	public String getCatalogId()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.catalogid")
	}
	public String getSourcePath()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.sourcepath")
	}
	public String getUploadFile()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.uploadFile")
	}
	public String getEntermediaLogin()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.login")
	}
	public String getFileDownload()
	{
		return configurationService.getConfigValue("org.synote.integration.entermedia.fileDownload")
	}
}
