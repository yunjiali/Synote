function initSynmarkOwnerClickMenu(selector)
{
	//yunjia: have to put control in it. Only owners can edit and delete the synmark
	$.contextMenu({
        selector: selector, 
        trigger: 'right', 
        //ignoreRightClick: true,
        items: {
        	play: {name: "Play", icon: "play", callback: synmarkMenuPlay},
            edit: {name: "Edit", icon: "edit", callback: synmarkMenuEdit},
            url:  {name: "Get URL", icon:"url", callback:synmarkMenuGetURL},
            "delete": {name: "Delete", icon: "delete", callback: synmarkMenuDelete},
            sep1: "---------",
            quit: {name: "Quit", icon: "quit", callback: $.noop}
        }
    });
}

function initSynmarkOtherClickMenu(selector)
{
	//yunjia: have to put control in it. Only owners can edit and delete the synmark
	$.contextMenu({
        selector: selector, 
        trigger: 'right', 
        //ignoreRightClick: true,
        items: {
        	play: {name: "Play", icon: "play", callback: synmarkMenuPlay},
            url:  {name: "Get URL", icon:"url", callback:synmarkMenuGetURL},
            //edit: {name: "Edit", icon: "edit", callback: synmarkMenuEdit},
            sep1: "---------",
            quit: {name: "Quit", icon: "quit", callback: $.noop}
        }
    });
}

function synmarkMenuPlay(key,opt)
{
	var synmark_id_div = (opt.$trigger).attr("id");
	synmark.clickSynmark($("#"+synmark_id_div));
}

function synmarkMenuEdit(key,opt)
{
	var synmark_id = (opt.$trigger).attr("synmark_id");
	var synmarkData = null;
	$.each(synmark.synmarksData,function(i,s){
		if(s.id == synmark_id)
		{
			synmarkData = s;
			return;
		}
	})
	
	//Open the edit widget
	if(synmarkData!=null)
	{
		var tags = "";
		if(synmarkData.tags && synmarkData.tags.length >0)
		{
			
			var separator ="";
			$.each(synmarkData.tags,function(j,t){
				tags+=separator+$.trim(t);
				separator=",";
			});
		}
		$("#errorMsg").hide();
		synmark.fillSynmarkForm(milisecToString(synmarkData.start),
				milisecToString(synmarkData.end),synmarkData.title,tags,synmarkData.note,synmarkData.id);
		
		$("#synmark_create_div").show(200);
		$("html,body").animate({scrollTop:$("#synmarks_div").offset().top},400);
	}
}

function synmarkMenuGetURL(key,opt)
{
	//Yunjia: offer better user interface for get URL
	var synmark_id = (opt.$trigger).attr("synmark_id");
	$("#synmark_url_dialog").dialog("destroy");
	$("#synmark_url_dialog").text(synmark.getURI(synmark_id));
	$("#synmark_url_dialog").wijdialog({
        autoOpen: true,
        height: 180,
        width: 400,
        modal: true,
        buttons: {
            Ok: function () {
                $(this).wijdialog("close");
            }
        },
        captionButtons: {
            pin: { visible: false },
            refresh: { visible: false },
            toggle: { visible: false },
            minimize: { visible: false },
            maximize: { visible: false }
        }
    });
}

function synmarkMenuDelete(key,opt)
{
	var synmark_id = (opt.$trigger).attr("synmark_id")
	if(confirm("Do you want to delete this synmark?"))
	{
		synmark.deleteSynmark(synmark_id);
	}
}