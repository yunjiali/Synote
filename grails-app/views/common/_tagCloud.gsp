<script type="text/javascript" src="${resource(dir:'js/jquery',file:"jqcloud-0.2.4.min.js")}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		var listAllTagsURL = g.createLink({controller:'user', action:'listAllTags'});
		
		$("#tag_cloud_div").on('show',function(){
			
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
		});
	})
</script>

<div>
	<button class="btn" data-toggle="collapse" data-target="#tag_cloud_div" id="tag_button">Show whole tag cloud</button>
	<div id="tag_cloud_div">
	    <div id="all_tags_div" style="height:400px;">
	    	
	    </div>
	</div>
</div>

