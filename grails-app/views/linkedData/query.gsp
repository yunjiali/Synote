<html>
<head>
<title>Synote Sparql Query Interface</title>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<meta name="layout" content="main" />
<g:urlMappings/>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.form.js')}"></script>
<script type="text/javascript" src="http://ajax.aspnetcdn.com/ajax/jquery.validate/1.9/jquery.validate.min.js"></script>
<script type="text/javascript">
//create html table from sparql response
function createHTMLTab(jsObj)
{
	var tableStr = "<table class='table'><thead><tr>";
	$.each(jsObj.head.vars,function(i,val){
		tableStr +="<th>"+val+"</th>";
	});
	tableStr+="</tr></thead>";
	tableStr+="<tbody>";
	$.each(jsObj.results.bindings,function(j,data){
		tableStr+="<tr>";
		$.each(data,function(k,bv){
			console.log("data");
			tableStr+="<td>"+bv.value+"</td>";
		});	
		tableStr += "</tr>";
	});
	tableStr+="</tbody></table>";
	return tableStr;
}
$(document).ready(function(){
	$("#query_form_submit").click(function(){
		
		if( $("#query").val() == null || $.trim($("#query").val()).length == 0)
		{
			alert("The query is empty!");
		}
		var queryString = encodeURIComponent($.trim($("#query").val()));
		var query_url = g.createLink({controller:'linkedData',action:'sparql'});
		query_url +="?query="+ queryString+"&output="+$("#output").val();

		$.ajax({
			url:query_url,
			//resetForm:true,
			type:'get',
			dataType:'text',
			beforeSend:function(event)
			{
				$("#queryResults").empty();
				$("#loading_img").show();
				$('html, body').animate({
			         scrollTop: $("#queryResults").offset().top
			    }, 1000);
			},
			success:function(data,textStatus, jqXHR, $form) //
			{
				if($("#output").val() == "htmltab")
				{
					var jsObj;
					try
					{
						jsObj = $.parseJSON(data);
						$("#queryResults").html(createHTMLTab(jsObj));
					}
					catch(err)
					{
						$("#queryResults").html(data);
					}
					
				}
				else //Add more results later
				{
					$("#queryResults").html(data);
				}
			},
			error:function(jqXHR,textStatus,errorThrown)
			{
				$("#queryResults").html(jqXHR.responseText);
			},
			complete:function(jqXHR,textStatus)
			{
				$("#loading_img").hide();
				$("#query_form_submit").button("reset");
			}
		});
		return false;
	});
});
</script>
</head>
<body>
<div class="container">
	<div class="row">
		<form id="query_form" name="query_form" method="GET">
		<fieldset>
			<div class="span7 well">
				<g:render template="/common/message" />
				<div class="control-group">
					<label for="query" class="control-label"><b>Query Text</b></label>
					<div class="controls">
						<textarea id="query" name="query" rows="20" style="width:100%">
${prefixString}
SELECT * WHERE {
	?s ?p ?o .
} LIMIT 10
						</textarea>
					</div>
				</div>
				<div>
					<button id="query_form_submit" type="button" class="btn btn-info" data-loading-text="Querying...">Send Query</button>
					<input id="query_form_reset" type="reset" class="btn" value="Reset"/>
				</div>
			</div>
			<div class="span3 well">
				<div class="control-group">
					<label for="output" class="control-label">Output Format</label>
					<div class="controls">
						<select id="output" name="output">
			              <option value="json">JSON</option>
			              <option value="rdfxml">RDF/XML</option>
			              <option value="htmltab" selected="selected">HTML Table</option>
			            </select>
					</div>	
				</div>
			</div>
		</fieldset>
		</form>
	</div>
	<h3>Query Results</h3>
	<img id="loading_img" src="${resource(dir:'images/skin',file:'loading_64.gif')}" alt="loading" style="display:none"/>
	<div class="row">
		<div class="span12" id="queryResults">
			
		</div>
	</div>
</div>
</body>
</html>
