<html>
<head>
<title>Analyse multimedia using NERD</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<style type="text/css">
	.table td{line-height:24px;}
</style>
<g:urlMappings/>
<script type="text/javascript">
var prgs = 0;
var entityCount = 0;
var extractorCount = ${textResource.extractors?.size()};
var finishedCount = 0;
var nerditone = function(extractor_name,text){
	var finished_extractors = 0;
	var extract_url = g.createLink({controller:'nerd',action:'extractAjax'});
	var ne_td =$("#ne_td_"+extractor_name);
	$.ajax({
		   type: "GET",
		   url: extract_url,
		   data: {extractor:extractor_name,text:text}, //default language is English
		   timeout:60000, // the call will be queued on the server-side, so we need to set it for a longer time
		   dataType: "json",
		   //Yunjia: Add a beforeSend function to display the loading message
		   success:function(data,textStatus, jqXHR)
		   {
				if(data.error !== undefined)
				{
					//Show error messages
					ne_td.html("<span class='alert alert-error'>"+data.error.description+"</span>");
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
					//This is the type from other extractors, we will us nerdType instead
					//var entity_type = data[i].type === undefined ?"unknown type":data[i].type.toLowerCase();
					var entity_type = data[i].nerdType ==""?"Thing":data[i].nerdType.split("#")[data[i].nerdType.split("#").length-1]
					var html='';
					if(data[i].uri == null || data[i].uri == "null"  || data[i].uri =="NORDF")
					{
						html += data[i].entity
					}
					else
					{
						html+="<a target='_blank' href='"+data[i].uri+"' title='"+data[i].entity+
								"'>"+data[i].entity+"<i class='icon-link-small'></i></a> is a(n) "+entity_type;
					}
						
					var entity_span = $("<span/>",{
						html:html
					});
					
					var approve_btn = $("<button/>").addClass("btn btn-success btn-mini btn-nerd-entity-approve").text("approve").attr('title', "approve");
					var reject_btn = $("<button/>").addClass("btn btn-danger btn-mini btn-nerd-entity-reject").text("reject").attr('title','reject');
					entity_span.append(approve_btn);
					entity_span.append(reject_btn);
					ne_td.append(entity_span);
					entityCount++;
				}
		   },
		   error:function(jqXHR,textStatus,errorThrown)
		   {
				if(textStatus == "abort")
				{
					ne_td.html("<span class='alert alert-error'>Request abort</span>");
					return;
				}
				else if(textStatus == "timeout")
				{
					ne_td.html("<span class='alert alert-error'>Request timeout</span>");
					return;
				}
				else if(textStatus == "error")
				{
					ne_td.html("<span class='alert alert-error'>Request error</span>");
					return;
				}
				else
				{
					ne_td.html("<span class='alert alert-error'>Request "+textStatus+"</span>");
					return;
				}
		   },
		   complete:function(jqXHR,textStatus)
		   {
			   $("#loading_img_"+extractor_name).hide();
			   $("#prgs_span").text(entityCount+" entities found");
			   $("#finished_count_span").text(++finishedCount);
			   $("#prgs_div").css("width",100*(finishedCount/extractorCount)+"%");
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
			<hr/>
			<div>
				<div class="row" style="padding-bottom:10px;">
					<div class="span9"><b>Content Preview</b><br/>
						<p id="preview_p">${textResource.text}</p>
					</div>
					<div class="span3 pull-right"><!-- Add this function later -->
						<div class="progress progress-striped">
							<div class="bar" id="prgs_div" style="width:0%;"></div>
						</div>
						<span class="nerd-entity-count"><span id="finished_count_span">0</span> out of ${textResource.extractors?.size()}</span>
						<span id="prgs_span" class="pull-right nerd-entity-count">0 entities found</span>
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
							<g:each in="${textResource.extractors}" var="${extractor}" status="i">
							<tr id="tr_${extractor}">
								<td>
									<img width="120" height="48" alt="${extractor}" src="${resource(dir: 'images/nerd', file: extractor+'.png')}"/>
								</td>
								<td style="vertical-align:middle" id="ne_td_${extractor}"><img id="loading_img_${extractor}" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading"/></td>
							</tr>
							<script type="text/javascript">
							$(document).ready(function(){
								nerditone("${extractor}",$("#preview_p").text());
							});
							</script> 			
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