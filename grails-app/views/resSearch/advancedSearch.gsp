<html>
<head>
<title><g:message
	code="org.synote.search.resource.advancedSearch.title" /></title>
<meta name="layout" content="main" />
<g:javascript library="prototype" />
<g:javascript>
		function addSynmarkFields(e)
		{
			var synmarkSearchableFields = eval("("+e.responseText+")");
			var synmarkChb = document.getElementById('synmark');
			var synmarkDiv = document.getElementById('synmarkDiv');

			var labelSpan = document.createElement('span');
			labelSpan.innerText= '    Please select the Synmark field(s) you want to search:';
			synmarkDiv.appendChild(labelSpan);

			if(synmarkChb.checked)
			{
				for(var i=0;i<synmarkSearchableFields.length ;i++)
				{
					var
		fieldSpan=document.createElement(
		'span');
					fieldSpan.innerText='  '
		+synmarkSearchableFields[i];
					fieldSpan.style.fontStyle='italic'
		;
					synmarkDiv.appendChild(fieldSpan);

					var
		fieldChb=document.createElement(
		'input');
					fieldChb.type='checkbox'
		;
					fieldChb.checked=true; fieldChb.name='synmark_'
		+synmarkSearchableFields[i];
					fieldChb.id='synmark_'
		+synmarkSearchableFields[i];
					synmarkDiv.appendChild(fieldChb);
				}
			}
			else
			{
				synmarkDiv.innerHTML='';
			}
		}
	
</g:javascript>
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1><g:message
	code="org.synote.search.resource.advancedSearch.title" /></h1>
<g:render template="/common/message" model="[bean: multimediaResource]" />
<g:form method="post">
	<div class="dialog">
	<table>
		<tbody>
			<tr>
				<td colspan="2"><b>Find resources that have...</b></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="all">all these words</label></td>
				<td><input type="text" size="60" id="all" name="all"
					value="${params?.all}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="exact">this exact phrase</label></td>
				<td><input type="text" size="60" id="exact" name="exact"
					value="${params?.exact}" /></td>
			</tr>
			<tr class="prop">
				<td class="name"><label>one or more of these words</label></td>
				<td><input type="text" id="oneormore1" name="oneormore1"
					value="${params?.oneormore1}" /> <span>OR</span> <input type="text"
					id="oneormore2" name="oneormore2" value="${params?.oneormore2}" />
				<span>OR</span> <input type="text" id="oneormore3" name="oneormore3"
					value="${params?.oneormore3}" /></td>
			</tr>
			<tr>
				<td colspan="2"><b>Don't show resources that have...</b></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="unwanted">any of these
				unwanted words:</label></td>
				<td><input type="text" size="60" id="unwanted" name="unwanted"
					value="${params?.unwanted}" /></td>
			</tr>
			<tr>
				<td colspan="2"><b>Need to search a certain resource type?</b></td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="multimedia">Multimedia
				Resources</label></td>
				<td><input type="checkbox" id="multimedia" name="multimedia" />
				<div id="multimediaDiv"></div>
				</td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="synmark">Synmark Resources</label></td>
				<td><!-- <g:checkBox id="synmark" name="synmark"
									onclick="${remoteFunction(
														controller:'search',
														action: 'selectSynmarkFields',
														onComplete:'addSynmarkFields(e)')}"/> --> <input
					type="checkbox" id="synmark" name="synmark" />
				<div id="synmarkDiv" style="display: inline;"></div>
				</td>
			</tr>
			<tr class="prop">
				<td class="name"><label for="transcript">Transcript
				Resources</label></td>
				<td><input type="checkbox" id="transcript" name="transcript" />
				<div id="transcriptDiv"></div>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="advancedSearch" value="Advanced Search"
		action="handleAdvancedSearch" title="Advanced Search" /></span></div>
</g:form></div>
</body>
</html>
