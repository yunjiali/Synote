function initEditMode(recording)
{
	//#######prepare the transcript editing mode
	transcript.initEditing();
	
	$("#edit_transcript_enter_img").click(function(){
		$("#transcript_edit_enter_div").hide();
		$("#transcript_edit_menu_div").show(400);
		transcript.startEditing();
	});
	
	//enable the tooltip
	$("#transcript_edit_wrapper_div").wijtooltip();
	$("#transcript_edit_wrapper_div").wijtooltip("option","triggers","custom");
	$("#transcript_edit_wrapper_div").wijtooltip("option","closeBehavior","sticky");
	$("#transcript_edit_wrapper_div").wijtooltip("option","title","Right click on <br/>text blocks to see more options...");
	$("#transcript_edit_wrapper_div").wijtooltip("option","position",{my:"right bottom", at:"left top"});
}
