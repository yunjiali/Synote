package org.synote.search

import com.gargoylesoftware.htmlunit.WebClient
import com.gargoylesoftware.htmlunit.html.HtmlPage
import com.gargoylesoftware.htmlunit.BrowserVersion
import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import org.apache.log4j.Logger;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.web.filter.GenericFilterBean;


class GoogleCrawlFilter extends GenericFilterBean implements InitializingBean {

	private Logger log = Logger.getLogger(getClass())
	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException 
	{
		
		HttpServletRequest request = (HttpServletRequest)req
		HttpServletResponse response = (HttpServletResponse)res
		String reqUrl = req.getRequestURL().toString()
		String queryString = req.getQueryString();
		log.debug("queryString:"+queryString)
		if ((queryString!=null) && (queryString.contains("_escaped_fragment_=")) && reqUrl.indexOf("/recording/snapshot/") == -1)
		{
			//request for whole page, redirect to recording/snapshot/1?_escaped_fragment_=t=0
			PrintWriter out = response.getWriter()
			String fragValue = request.getParameter("_escaped_fragment_");
			if(!fragValue) //the request has no media fragment attached, so we redirect it to recording/replay/id page
			{
				queryString = queryString.replace("_escaped_fragment_=","_escaped_fragment_=t=0")
				String snapshotURL = reqUrl.replaceFirst("recording/replay/","recording/snapshot/")+"?"+queryString //replace the ugly url with empty string

				log.debug("return whole page:"+snapshotURL)
				final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3)
				webClient.setJavaScriptEnabled(true)
				webClient.setThrowExceptionOnScriptError(false)
				
				HtmlPage page = webClient.getPage(snapshotURL)
				//webClient.waitForBackgroundJavaScript(5000) //Set this up in configuration?
				page.cleanUp()
				out.print(page.asXml())
				webClient.closeAllWindows()
			}
			else//the request is looking for media fragments, so we redirect it to recording/snapshot/id#t=xx,xx page
			{
				String snapshotURL = reqUrl.replaceFirst("recording/replay/","recording/snapshot/")+"?"+queryString
				log.debug("return short snapshot page:"+snapshotURL)
				final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_3)
				webClient.setJavaScriptEnabled(true)
				webClient.setThrowExceptionOnScriptError(true)
				
				HtmlPage page = webClient.getPage(snapshotURL)
				//webClient.waitForBackgroundJavaScript(5000) //Set this up in configuration?
				out.print(page.asXml())
				webClient.closeAllWindows()
			}
		}
		else
		{
			try
			{
				chain.doFilter(req,res)	
			}
			catch(ServletException e)
			{
				System.err.println("Servlet exception caught: " + e);
				e.printStackTrace();
			}
		}
	}
	
	int getOrder() {
		return FilterChainOrder.REMEMBER_ME_FILTER
	}
	
	void afterPropertiesSet() {
		return
	}

}
