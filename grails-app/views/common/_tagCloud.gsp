<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jqcloud-0.2.4.min.js")}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var listAllTagsURL = g.createLink({controller:'user', action:'listAllTags'});
		
		$("#tag_cloud_div").wijexpander({
			allowExpand:true,
			expanded:false,
			beforeExpand:function(e)
			{
				$.ajax({
					   type: "GET",
					   url: listAllTagsURL,
					   dataType: "json",
					   //Yunjia: Add a beforeSend function to display the loading message
					   success:function(data)
					   {
						   $("#all_tags_div").jQCloud(data);
					   }
				});
			},
			afterCollapse:function(e)
			{
				$("#all_tags_div").empty();
			}
		});
	})
</script>
<div id="tag_cloud_div" class="span-24">
    <h2>Tags Synote users use</h2>
    <div id="all_tags_div" style="height:500px;">
    	
    </div>
</div>
