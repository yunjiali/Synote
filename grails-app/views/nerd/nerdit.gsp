<html>
<head>
<title>Analyse multimedia using NERD</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<script type="text/javascript">
$(document).ready(function(){
	nerdit("${extractor}","${rn.id}","${rn.extractors[i+1]?rn.extractors[i+1]:null}",nerdit);
});

var nerdit = function(extractor_name,id){
	
	var extract_url = g.createLink({controller:'nerd',action:'extractAjax'});
	$.ajax({
		   type: "GET",
		   url: extract_url,
		   data: {extractor:extractor_name,id:id}, //default language is English
		   timeout:30000,
		   dataType: "json",
		   //Yunjia: Add a beforeSend function to display the loading message
		   success:function(data,textStatus, jqXHR)
		   {
				var ne_td =$("#ne_td_"+extractor_name+"_"+id);
				if(data.error !== undefined)
				{
					//Show error messages
					ne_td.html("<span class='alert alert-error'>"+data.error.description+"</span>").
					return;
				}

				if(data.length == 0)
				{
					ne_td.html("<span class='alert'>No entity is found</span>");
				}
				
				for(var i=0;i<data.length;i++)
				{
					if(i!= 0)
						ne_td.append($("<br/>"));
					ne_td.append($("<span/>",{text:data[i].entity}));
					
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
				if(textStatus == "abort")
				{
					
				}
				else if(textStatus == "timeout")
				{

				}
				else if(textStatus == "error")
				{

				}
				console.log(jqXHR.statusCode());
		   },
		   complete:function(jqXHR,textStatus)
		   {
			   $("#loading_img_"+extractor_name+"_"+id).hide();
			   callback()
		   }
		   
	});				
};
</script>
</head>
<div class="container">
	<div class="row">
		<div class="span2" id="user_nav_div">
			<g:render template="/common/userNav" model="['active':'recordings']"/>
		</div>
		<div class="span10" id="user_content_div">
			<h2 class="hiding">NERD It</h2>
			<h3>Results</h3>
			<g:each in="${resourceList.rows}" var="rn">
				<hr/>
				<div>
					<div class="row" style="padding-bottom:10px;">
						<div class="span6"><b>Content Preview</b><br/>
							${rn.text}
						</div>
						<div class="span3 pull-right">
							<div class="progress progress-striped active">
								<div class="bar" style="width:0%;"></div>
							</div>
							<span class="nerd-entity-count">0%</span>
							<span id="prgs_span_${rn.id}" class="pull-right nerd-entity-count">0 entities found</span>
						</div>
					</div>
					<div>
						<table class="table table-bordered table-striped">
							<colgroup>
								<col class="span2"/><!-- image -->
								<col class="span7"/><!-- named entity & entity type -->
							</colgroup>
							<thead>
								<tr>
									<th>Extractor</th>
									<th>Named Entities and Entity type</th>
								</tr>
							</thead>
							<tbody>
								<g:each in="${resoruceList.extractors}" var="${extractor}" status="i">
								<tr id="tr_${extractor}_${rn.id}">
									<td>
										<img width="120" height="48" alt="${extractor}" src="${resource(dir: 'images/nerd', file: extractor+'.png')}"/>
									</td>
									<td style="vertical-align:middle" id="ne_td_${extractor}_${rn.id}"><img id="loading_img_${extractor}_${rn.id}" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/></td>
								</tr>
								</g:each>
							</tbody>
						</table>
					</div>
				</div>
			</g:each>
		</div>
	</div>
</div>
</body>
</html>