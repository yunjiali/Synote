<html>
<head>
<title>List Named Entities</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<script type="text/javascript">
$(document).ready(function(){
	$(".approve").click(function(){
		var review_url = g.createLink({controller:'nerd',action:'saveReviewAjax'});
		var rating = 0;
		var idex = "";
		if($(this).attr("title") == "approve")
		{
			rating = 1;
			idex = $(this).attr("id").replace("btn_approve_",""); //get the extraction id
		}
		else
			idex = $(this).attr("id").replace("btn_reject_","");

		$.ajax({
			   type: "POST",
			   url: review_url,
			   data: {rating:rating,idex:idex}, //default language is English
			   timeout:60000, // the call will be queued on the server-side, so we need to set it for a longer time
			   dataType: "json",
			   //Yunjia: Add a beforeSend function to display the loading message
			   beforeSend:function(jqXHR, settings)
			   {
					//disable the button
					$("#btn_approve_"+idex).attr("disabled","disabled");
					$("#btn_reject_"+idex).attr("disabled","disabled");
			   },
			   success:function(data,textStatus, jqXHR)
			   {
				   	if(data.error !== undefined)
					{
						//Show error messages
						$("#msg_"+idex).html("<span class='alert alert-error'>"+data.error.description+"</span>");
						$("#btn_approve_"+idex).removeAttr("disabled");
						$("#btn_reject_"+idex).removeAttr("disabled");
					}
				   	else
					{
						if(rating==0)
						{
							$("#btn_approve_"+idex).removeClass("btn-success").removeAttr("disabled");
				   			$("#btn_reject_"+idex).addClass("btn-danger").attr("disabled","disabled");
						}
						else
						{
							$("#btn_approve_"+idex).addClass("btn-success").attr("disabled","disabled");
							$("#btn_reject_"+idex).removeClass("btn-danger").removeAttr("disabled");
						}

						return false;
					}
			   },
			   error:function(jqXHR,textStatus,errorThrown)
			   {
				   	$("#msg_"+idex).html("<span class='alert alert-error'>"+data.error.description+"</span>");
				   	$("#btn_approve_"+idex).removeAttr("disabled");
					$("#btn_reject_"+idex).removeAttr("disabled");
			   },
			   complete:function(jqXHR,textStatus)
			   {
					
			   }
		});	
	});
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="heading-inline">List Named Entities</h2>
			<hr/>
			<h3>Content Preview</h3>
			<div>
				<syn:printFullTextFromResource resource="${res}"/>
			</div>
			<br/>
			<div>
				<div>
					<table class="table table-bordered table-striped">
						<colgroup>
							<col class="span2"/><!-- image -->
							<col class="span7"/><!-- named entity & entity type -->
						</colgroup>
						<thead>
							<tr>
								<th>Extractor</th>
								<th>Extracted Named Entities</th>
							</tr>
						</thead>
						<tbody>
							<g:each in="${nes}" var="extractor" status="i">
							<tr id="tr_${extractor.key}">
								<td>
									<img width="120" height="48" alt="${extractor.key}" src="${resource(dir: 'images/nerd', file:extractor.key+'.png')}"/>
								</td>
								<td style="vertical-align:middle" id="ne_td_${i}">
									
									<g:if test="${extractor.value.size()==0}">
										<span class='alert'>No entity has been extracted.</span>
									</g:if>
									<g:else>
									<g:each in="${extractor.value}" var="n" status="j">
										<div style="margin-bottom:20px;">
											<div id="msg_${n.idex.value}"></div>
											<div id="approve_${n.idex.value}" class="btn-group btn-group-nerd">
												<g:if test="${n.rating == null}">
													<button class="btn approve" title="approve" data-loading-text="Sending Review..." id="btn_approve_${n.idex.value}"><i class="icon-thumbs-up"></i></button>
													<button class="btn approve" title="reject" id="btn_reject_${n.idex.value}"><i class="icon-thumbs-down"></i></button>
												</g:if>
												<g:elseif test="${n.rating.value == '1'}">
													<button class="btn btn-success approve" disabled="disabled" title="approve" data-loading-text="Sending Review..." id="btn_approve_${n.idex.value}"><i class="icon-thumbs-up"></i></button>
													<button class="btn approve" title="reject" id="btn_reject_${n.idex.value}"><i class="icon-thumbs-down"></i></button>
												</g:elseif>
												<g:elseif test="${n.rating.value == '0'}">
													<button class="btn approve" title="approve" data-loading-text="Sending Review..." id="btn_approve_${n.idex.value}"><i class="icon-thumbs-up"></i></button>
													<button class="btn btn-danger approve" disabled="disabled" title="reject" id="btn_reject_${n.idex.value}"><i class="icon-thumbs-down"></i></button>
												</g:elseif>
											</div>
											<div style="display:inline;padding-top:5px;">
											<g:if test="${n.ne.type!='uri'}">
												${n.entity.value}
											</g:if>
											<g:else>
												
												<a href="${n.ne.value}" target="_blank" title="${n.entity.value}">
													${n.entity.value}
													<i class='icon-link-small'></i>
												</a>
												is a(n) ${n.nerdtype.value == ''?'Thing':n.nerdtype.value?.split('#')[n.nerdtype.value?.split('#')?.size()-1]} 
											</g:else>
											</div>
										</div>
									</g:each>
									</g:else>
								</td>
							</tr>
							</g:each>
						</tbody>
					</table>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>
