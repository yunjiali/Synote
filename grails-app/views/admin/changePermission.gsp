<html>
<head>
<title>Change Permissions</title>
<meta name="layout" content="admin" />
<g:javascript library="prototype" />
<!--
	<g:javascript>
		function displayMultimediaList(e)
		{
			var multimediaList = eval("("+e.responseText+")");
			var multimediaDiv = document.getElementById("multimediaList");
			if(multimediaList.size() == 0)
			{
				var notFoundText= document.createTextNode("No recording found");
				multimediaDiv.appendChild(notFoundText);
				return;
			}

			alert(multimediaList.size());

			var child;
			while(child=multimediaDiv.firstChild)
					multimediaDiv.removeChild(child);

			var h2 = document.createElement("h2");
			var h2Text = document.createTextNode("Affected Recordings:");
			h2.appendChild(h2Text);
			multimediaDiv.appendChild(h2);

			var tbl = document.createElement("table");

			var tblBody = document.createElement("tbody");
			for(var i=0;i<multimediaList.length;i++)
			{
				var row = document.createElement("tr");

				var cellId = document.createElement("td");
				var cellIdText = document.createTextNode(multimediaList[i].id);
				cellId.appendChild(cellIdText);
				row.appendChild(cellId);

				var cellName= document.createElement("td");
				var cellNameText = document.createTextNode(multimediaList[i].name);
				cellName.appendChild(cellNameText);
				row.appendChild(cellName);

				tblBody.appendChild(row);
			}
			tbl.appendChild(tblBody);
			multimediaDiv.appendChild(tbl);
		}

	</g:javascript>
	-->
</head>
<body>
<div class="nav"></div>
<div class="body">
<h1>Change Permissions</h1>
<g:render template="/common/message" /> <g:form method="post"
	controller="admin">
	<div class="dialog">
	<table>
		<tr>
			<td>Change user <g:select name="userName" from="${userList}"
				value="${user?.userName}" />'s recordings with public
			permission&nbsp; <select name="oldPerm" id="oldPerm"
				title="Select permission">
				<option value="" selected="true">PRIVATE</option>
				<g:each var="permission" in="${PermissionValue.values()}">
					<option value="${permission}">
					${permission}
					</option>
				</g:each>
			</select> &nbsp;to <select name="newPerm" id="newPerm"
				title="Select permission">
				<option value="" selected="true">PRIVATE</option>
				<g:each var="permission" in="${PermissionValue.values()}">
					<option value="${permission}">
					${permission}
					</option>
				</g:each>
			</select> <g:actionSubmit action="changeMultimediaPermission"
				onclick="return confirm('Are you sure?');" value="Update" /></td>
		</tr>
	</table>
	</div>
</g:form></div>
<br />
<div id="multimediaList" class="list"></div>
</body>
</html>
