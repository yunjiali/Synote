<script>
var count=0;
$(function(){
	//if($("#add_group_permission_a").click == null)
	//{
	//Yunjia : It will bind twice. Why?
	$("#add_group_permission_a").unbind('click',handler);
	//console.log("function");
	$("#add_group_permission_a").button({
		icons:{
			primary:"ui-icon-plusthick"
		}
	}).bind('click',handler);
	
	$("#group_permission_ul").show();
});

var handler = function addGroupPermission(){
			console.log("click");
			$.ajax({
				type:"GET",
				url:"/synote/multimediaResource/addGroupPermission", 
				dataType:"text",
				success:
			function(dataStr)
			{
				var data = jQuery.parseJSON(dataStr);
				
				//The Div container of for grouplist, permission list and the remove button
		        var group_select_div = $('<div/>',{
		        	class:"inlineLabels"
		        });

		        var combowrap_group_select_div = $('<div/>',{
					class:"combowrap"
			    });
			    
		        var group_select = $('<select />',{
					id:"groupId",
					name:"groupId",
					class:"selectInput small"
				});
				
			    $.each(data, function(i, item) {
					var option = $('<option/>',{
						text: item.name,
						value: item.id
					});
					group_select.append(option);
				});

				combowrap_group_select_div.append(group_select);
				group_select_div.append(combowrap_group_select_div);
				
				//Add permission template
				var combowrap_permission_select_div=$('<div/>',{
					class:"combowrap"
			    });
				var permission_select = $('<select />',{
					id:"groupPermission",
					name:"groupPermission",
					class:"selectInput small"
				});
				var roption = $('<option/>',{
					text: "READ",
					value: "100"
				});
				permission_select.append(roption);
				var aoption = $('<option/>',{
					text: "ANNOTATE",
					value: "200",
					selected: "yes"
				});
				permission_select.append(aoption);
				var woption = $('<option/>',{
					text: "WRITE",
					value: "300"
				});
				permission_select.append(woption);

				combowrap_permission_select_div.append(permission_select);
				group_select_div.append(combowrap_permission_select_div);
				
				//Add remove button
				var group_permission_remove_a = $('<a/>',{
					text:"Remove",
					href:'#group_permission_ul',
					click:	function(){
								group_select_div.fadeOut(300,function(){
									group_select_div.remove();
								});
								console.log("OK");
							}
					}
				);
				
				group_permission_remove_a.button({
					icons:{
						primary: "ui-icon-closethick"
					},
				});
				group_select_div.append(group_permission_remove_a);

				//Add label and div
				var li = $("<li/>");
				li.append(group_select_div);
				$("#group_permission_ul").append(li); 

				$("#groupId").combobox();   
				$("#groupPermission").combobox();   
			}
		});
}



</script>
<ul id="group_permission_ul">
	<li>
		<span>
			<a id="add_group_permission_a" href="#group_permission_ul">Add</a>
		</span>
	</li>
</ul>

