<html>
<head>
<title><g:message
	code="org.synote.player.server.recording.print.title" /></title>
<meta name="layout" content="main" />
<!-- <link rel="stylesheet" href="${resource(dir: 'css', file: 'print.css')}" />-->
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform', file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform',file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir: 'js/jquery', file: 'jquery.combobox.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		$(".uniForm").uniform();
		$("input[type=text], input[type=password]").wijtextbox();
	});

	function updatePart()
	{
		if(document.form.part.checked)
		{
			$("#time_interval_div").show("fast");
		}
		else
		{
			$("#time_interval_div").hide("fase");
		}
		document.form.from.disabled = !document.form.part.checked;
		document.form.to.disabled = !document.form.part.checked;
	}

	function updateSynmarked()
	{
		document.form.selectSynmarkedAll.disabled = !document.form.synmarked.checked;
		document.form.selectSynmarkedNone.disabled = !document.form.synmarked.checked;
		for (var i = 0; i < document.form.elements.length; i++)
		{	
				if (document.form.elements[i].name.indexOf("synmarked-user-") == 0)
					document.form.elements[i].disabled = !document.form.synmarked.checked;
		}
	}

	function updatePresentation()
	{
		document.form.slideHeight.disabled = !document.form.presentation.checked;
	}
	function updateSynmarks()
	{
		document.form.selectSynmarksAll.disabled = !document.form.synmarks.checked;
		document.form.selectSynmarksNone.disabled = !document.form.synmarks.checked;
		for (var i = 0; i < document.form.elements.length; i++)
		{
			if (document.form.elements[i].name.indexOf("synmarks-user-") == 0)
				document.form.elements[i].disabled = !document.form.synmarks.checked;
		}

		document.form.selectSettingsAll.disabled = !document.form.synmarks.checked;
		document.form.selectSettingsNone.disabled = !document.form.synmarks.checked;
		document.form.synmarkId.disabled = !document.form.synmarks.checked;
		document.form.synmarkTiming.disabled = !document.form.synmarks.checked;
		document.form.synmarkTitle.disabled = !document.form.synmarks.checked;
		document.form.synmarkNote.disabled = !document.form.synmarks.checked;
		document.form.synmarkTags.disabled = !document.form.synmarks.checked;
		document.form.synmarkOwner.disabled = !document.form.synmarks.checked;
		document.form.synmarkNext.disabled = !document.form.synmarks.checked;
	}

	function selectSettings(checked)
	{
		document.form.synmarkId.checked = checked;
		document.form.synmarkTiming.checked = checked;
		document.form.synmarkTitle.checked = checked;
		document.form.synmarkNote.checked = checked;
		document.form.synmarkTags.checked = checked;
		document.form.synmarkOwner.checked = checked;
		document.form.synmarkNext.checked = checked;
	}

	function updateAll()
	{
		updatePart();
		updateSynmarked();
		updatePresentation();
		updateSynmarks();
	}

	window.onload = updateAll;

	function selectSynmarked(checked)
	{
		if (document.form.synmarked.checked)
		{
			for (var i = 0; i < document.form.elements.length; i++)
			{
				if (document.form.elements[i].name.indexOf("synmarked-user-") == 0)
					document.form.elements[i].checked = checked;
			}
		}
	}

	function selectSynmarks(checked)
	{
		if (document.form.synmarks.checked)
		{
			for (var i = 0; i < document.form.elements.length; i++)
			{
				if (document.form.elements[i].name.indexOf("synmarks-user-") == 0)
					document.form.elements[i].checked = checked;
			}
		}
	}
	</script>
</head>
<body>
<syn:isLoggedIn>
<div class="span-24" id="user_nav">
	<g:render template="/common/userNav"/>
</div>
</syn:isLoggedIn>
<div class="span-22 prepend-1 append-1" itemscope="itemscope" itemtype="http://schema.org/Table">
	<h1><g:message code="org.synote.player.server.recording.print.title" /></h1>
	<g:render template="/common/message" /> 
	<g:form name="form" method="get" action="handlePrint" class="uniForm">
		<input type="hidden" name="id" value="${recording?.id}" />
		<div class="ctrlHolder inlineLabels">
			<label for="recordingTitle">Recording Title:</label></td>
			<span class="recordingTitle">${recording.title}</span>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="part">Part of Recording Only:</label>
			<g:checkBox name="part" value="${false}" onclick="updatePart();" />
			<div id="time_interval_div">
				<div class="ctrlHolder inlineLabels">
					<label for="from">From:</label>
					<input name="from" value="${params.from}" class="textInput medium"/> 
					<p class="formHint">Type time in seconds (e.g. 80) or minutes and seconds (e.g. 1:20)</p>
				</div>
				<div class="ctrlHolder inlineLabels">
					<label for="to">To:</label></td>
					<input name="to" value="${params.to}" class="textInput medium"/>
					<p class="formHint">Type time in seconds (e.g. 80) or minutes and seconds (e.g. 1:20)</p>
				</div>
			</div>
		</div>
		<div class="ctrlHolder inlineLabels">
			<label for="synmarked">Synmarked Parts Only:</label>
			<g:checkBox name="synmarked" value="${false}" onclick="updateSynmarked();" />
			<div id="synmark_select_div">
				<div class="ctrlHolder inlineLabels">
					<input type="button" name="selectSynmarkedAll" value="All" onclick="selectSynmarked(true);" />
					<input type="button" name="selectSynmarkedNone" value="None" onclick="selectSynmarked(false);" />
					<table>
						<tbody>
							<g:set var="index" value="${0}"/>
							<g:each var="owner" in="${owners}">
								<g:if test="${index % 5 == 0}">
									<tr></g:if>
									<td class="label"><label for="synmarked-user-${owner.id}"><syn:formatOwner owner="${owner}"/></label></td>
									<td><g:checkBox name="synmarked-user-${owner.id}" value="${false}"/></td>
									<g:set var="index" value="${index + 1}"/>
							</g:each>
						</tbody>
					</table>
				</div>
			</div>
		</div>	
 <!--
			<tr class="prop">
				<td class="name"></td>
				<td class="value">
				<div></div>
				
							</td>
						</tr>
						<tr class="prop">
							<td class="name">
								<label for="transcript">Transcript:</label>
							</td>
							<td class="value">
								<g:checkBox name="transcript" value="${true}"/>
							</td>
						</tr>
						<tr class="prop">
							<td class="name">
								<label for="presentation">Presentation:</label>
							</td>
							<td class="value">
								<g:checkBox name="presentation" value="${true}" onclick="updatePresentation();"/>
								<table>
									<tbody>
										<tr class="prop">
											<td class="name">
												<label for="slideHeight">Slide Height:</label>
											</td>
											<td class="value">
												<g:textField name="slideHeight" value="${params.slideHeight}"/> Type slide height in pixels (e.g. 240) or % (e.g. 50%)
											</td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
						<tr class="prop">
							<td class="name">
								<label for="synmarks">Synmarks:</label>
							</td>
							<td class="value">
								<g:checkBox name="synmarks" value="${true}" onclick="updateSynmarks();"/>
								<div>
									<input type="button" name="selectSynmarksAll" value="All" onclick="selectSynmarks(true);"/>
									<input type="button" name="selectSynmarksNone" value="None" onclick="selectSynmarks(false);"/>
								</div>
								<table>
									<tbody>
										<g:set var="index" value="${0}"/>
										<g:each var="owner" in="${owners}">
											<g:if test="${index % 5 == 0}"><tr></g:if>
											<td class="label"><label for="synmarks-user-${owner.id}"><g:formatOwner owner="${owner}"/></label></td>
											<td><g:checkBox name="synmarks-user-${owner.id}" value="${true}"/></td>
											<g:set var="index" value="${index + 1}"/>
										</g:each>
									</tbody>
								</table>
							</td>
						</tr>
						<tr class="prop">
							<td class="name">Synmark Settings:</td>
							<td class="value">
								<div>
									<input type="button" name="selectSettingsAll" value="All" onclick="selectSettings(true);"/>
									<input type="button" name="selectSettingsNone" value="None" onclick="selectSettings(false);"/>
								</div>
								<table>
									<tbody>
										<tr>
											<td class="label"><label for="synmarkId">ID</label></td>
											<td><g:checkBox name="synmarkId" value="${true}"/></td>
											<td class="label"><label for="synmarkTiming">Timing</label></td>
											<td><g:checkBox name="synmarkTiming" value="${true}"/></td>
											<td class="label"><label for="synmarkTitle">Title</label></td>
											<td><g:checkBox name="synmarkTitle" value="${true}"/></td>
											<td class="label"><label for="synmarkNote">Note</label></td>
											<td><g:checkBox name="synmarkNote" value="${true}"/></td>
											<td class="label"><label for="synmarkTags">Tags</label></td>
											<td><g:checkBox name="synmarkTags" value="${true}"/></td>
											<td class="label"><label for="synmarkOwner">Owner</label></td>
											<td><g:checkBox name="synmarkOwner" value="${true}"/></td>
											<td class="label"><label for="synmarkNext">Next</label></td>
											<td><g:checkBox name="synmarkNext" value="${true}"/></td>
										</tr>
									</tbody>
								</table>
							</td>
						</tr>
					</tbody>
				</table>
			</div>
			<div class="buttons">
				<span class="button">
					<input type="submit" class="preview" value="Preview" title="Print preview"/>
				</span>
			</div>-->
		</g:form>
	</div>
</body>
</html>
