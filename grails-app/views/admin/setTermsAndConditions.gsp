<html>
<head>
<title>Edit Terms and Conditions</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.2/jquery.min.js"></script>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.16/jquery-ui.min.js"></script>
<script type="text/javascript" src="${resource(dir:'js/tiny_mce',file:"jquery.tinymce.js")}"></script>
<meta name="layout" content="admin" />
<script type="text/javascript">
	/*$(document).ready(function(){
		$('#termsAndConditions').tinymce({
				script_url:g.resource({dir:'js/tiny_mce',file:'tiny_mce.js'}),
				mode : "textareas",
				width:"100%",
				plugins:"pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template",
				//plugins : "autolink,lists,pagebreak,style,layer,table,save,advhr,advimage,advlink,emotions,iespell,inlinepopups,insertdatetime,preview,media,searchreplace,print,contextmenu,paste,directionality,fullscreen,noneditable,visualchars,nonbreaking,xhtmlxtras,template,advlist,rdfa",
				valid_elements : "*[*]",
				theme:"advanced",
				theme_advanced_buttons1 : "bold,italic,underline,strikethrough,|,justifyleft,justifycenter,justifyright,justifyfull,|,styleselect,formatselect,fontselect,fontsizeselect",
				theme_advanced_buttons2 : "cut,copy,paste,pastetext,pasteword,|,search,replace,|,bullist,numlist,|,outdent,indent,blockquote,|,undo,redo,|,link,unlink,anchor,cleanup,code,|,forecolor,backcolor",
		        theme_advanced_buttons3 : "",
		        theme_advanced_toolbar_location : "top",
		        theme_advanced_toolbar_align : "left",
		        theme_advanced_statusbar_location : "bottom",
		        theme_advanced_resizing : true,
		});*/
		//$('#termsAndConditions').tinymce().html('${content.encodeAsHTML()}');
		//$('#termsAndConditions').val(cont);
	});
</script>
</head>
<body>
<div class="body">
<h1>Edit Terms and Conditions</h1>
<g:render template="/common/message" /> 
<g:form method="post" controller="admin">
	<div class="dialog">
	<table>
		<tbody>
			<tr class="prop">
				<td class="name"><label for="termsAndConditions">Terms and Conditions</label></td>
				<td class="value">
					<textarea id="termsAndConditions" name="termsAndConditions" class="tinymce">${content}</textarea>
				</td>
			</tr>
		</tbody>
	</table>
	</div>
	<div class="buttons"><span class="button"><g:actionSubmit
		class="save" value="Confirm" action="saveTermsAndConditions"
		title="Confirm" /></span> <span
		class="button"><g:actionSubmit class="cancel" value="Cancel"
		action="cancel" title="Cancel" /></span></div>
</g:form>
</div>
</body>
</html>
