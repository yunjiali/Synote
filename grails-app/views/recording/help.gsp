<head>
<title><g:message code="org.synote.player.server.recording.help" /></title>
<meta name="layout" content="main" />
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform',  file: 'uni-form.css')}" media="screen" charset="utf-8"/>
<link rel="stylesheet" href="${resource(dir: 'css/jquery/aristo/uniform',  file: 'default.uni-form.css')}" media="screen" charset="utf-8"/>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform', file:'uni-form.jquery.min.js')}"></script>
<script type="text/javascript" src="${resource(dir:'js/jquery/uniform', file:'uni-form-validation.jquery.min.js')}"></script>
<script type="text/javascript">
	$(document).ready(function(){
		
	});
</script>
<style>
	#multimedia_help td, #multimedia_help table
	{
		border:1px solid black;
	}
	#transcript_help td, #transcript_help table
	{
		border:1px solid black;
	}
</style>
</head>

<body>
<h1><g:message code="org.synote.player.server.recording.help" /></h1>
<div class="span-22 prepend-1 append-1" style="border:1px solid black;font-size:1.2em;">
	<span>Content</span>
	<ul>
		<li>
			<a href="#multimedia_help">Multimedia Player</a>
		</li>
		<li>
			<a href="#transcript_help">Transcript</a>
		</li>
		<li>
			<a href="#synmark_help">Synmark</a>
		</li>
		<li>
			<a href="#presentation_help">Presentation</a>
		</li>
	</ul>
</div>
<div class="span-22 prepend-1 append-1 prepend-top append-bottom">
	<div id="multimedia_help">
		<h2>Multimedia Player</h2>
		<div>
			<h3>Supported format and platform</h3>
			<div id="supported_format">
				<table>
					<tr>
						<th>Platform</th>
						<th>Browser</th>
						<th>Media format</th>
						<th>Comments</th>
					</tr>
					<tr>
						<td rowspan="3">Windows</td>
						<td>IE 8 & 9</td>
						<td>Audio: wma, mp3, wav, mid, midi, aac, m4a, ogg<br/>
							Video: avi, wmv, mpg, mpeg, m1v, mp2, mpa, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
						<td>Haven't tested in IE 7 and before</td>
					</tr>
					<tr>
						<td>Firefox</td>
						<td>Audio: wma, mp3, wav, mid, midi, aac, m4a, ogg<br/>
							Video: avi, wmv, mpg, mpeg, m1v, mp2, mpa, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td>Google Chrome</td>
						<td>Audio: wma, mp3, wav, mid, midi, aac, m4a, ogg<br/>
							Video: avi, wmv, mpg, mpeg, m1v, mp2, mpa, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td rowspan="2">Linux</td>
						<td>Firefox</td>
						<td>Audio: wma, mp3, aac, m4a, ogg, wav<br/>
							Video: wmv, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td>Google Chrome</td>
						<td>Audio: wma, mp3, aac, m4a, ogg, wav<br/>
							Video: wmv, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td rowspan="3">Mac OS</td>
						<td>Safari</td>
						<td>Audio: wma, mp3, aac, m4a, ogg, wav<br/>
							Video: wmv, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td>Firefox</td>
						<td>Audio: wma, mp3, aac, m4a, ogg, wav<br/>
							Video: wmv, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
					<tr>
						<td>Google Chrome</td>
						<td>Audio: wma, mp3, aac, m4a, ogg, wav<br/>
							Video: wmv, mp4, mov, f4v, flv, 3gp, 3g2, ogv, webm,  www.youtube.com,  youtu.be
						</td>
					</tr>
				</table>
			</div>
			<div>Please notice that following file format or resources are NOT supported yet:
				<ul>
					<li>Due to the crossdomain problem, youtube short url (youtu.be) sometimes cannot be played.</li>
					<li>Vimeo</li>
					<li>AVI on linux and mac os platform</li>
					<li>IE 7 and before</li>
				</ul>
			</div>
			
		</div>
	</div>
	<div id="transcript_help">
		<h2>Transcript</h2>
		<div>
			<h3 id="transcript_editing_help">Transcript Editing</h3>
			<p>
			Only the owner or the users who have the WRITE permission of the recording can edit the transcript.
			In the Synote player,  you can edit the transcript,  save it as draft for later editing and deleting the whole transcript.
			When the editing is enabled,  you can select one of the actions in the transcript editing menu,  or you can right click on the text block and choose to play,  edit, 
			merge and split the transcript. 
			</p>
			<p>
			If you have the permission to edit the transcript and click the "edit transcript" button,  two action menus will be enabled:
			<a href="#transcript_editng_menu_table">Transcript editing menu</a> and <a href="#transcript_editing_right_click_menu_table">
			Right click menu for transcript editing
			</a>
			</p>
			<p>
			Please notice that the changes to the transcript <b>will NOT be saved unless you click the "save and exit" button</b>. If you leave transcript editing without clicking the "save and exit"
			button,  the old transcript will be restored when you open the page next time. Saving a draft will enable you to restore the transcript from a editing draft, 
			but you still need to use "save and exit" button to replace the old transcript with the edited one.
			To complete delete the transcript,  simply click the "remove all transcript button" and "save and exit".</p>
			<p>
			In the transcript editing mode,  the synchronised displaying of transcript will be disabled. It means the transcript won't be highlighted with the playing of the recording and
			the transcript panel won't be scrolled automatically. But when you click a text block,  the player will still start playing from the start time of the text block.
			</p>
			<div id="transcript_editng_menu_table">
				<table>
					<caption style="text-align:center;font-weight:bold;">Transcript editing menu</caption>
					
						<tr>
							<th>Action</th>
							<th>Explanation</th>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_add_22.png")}" alt="Add new transcript block" id="edit_transcript_add_img" title="Add new transcript block"/>
								Add new transcript block
							</td>
							<td>
								Add a new transcript block to the original transcript. When you click this button,  a form will display. You have to specify the startime and end
								time of the text block,  as well as the transcript text. You can click the submit button in the form to submit the change. The system will automatically
								find the appropriate place to insert the text block depends on the start time.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_import_22.png")}" alt="Import transcript from a file" id="edit_transcript_import_img" title="Import transcript from a file"/>
								Import transcript from a file
							</td>
							<td>
								NOT IMPLEMENTED YET! Import the transcript from a certain format of file.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_revert_22.png")}" alt="Revert transcript from saved draft" id="edit_transcript_revert_img" title="Revert transcript from saved draft"/>
								Restore the transcript from the draft
							</td>
							<td>
								You can restore the transcript from the draft you have saved if there is one. This function is useful when you have made some unwanted changes and want to undo the changes.
								It is also useful when you don't have time to edit all the transcript in one go.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_clear_22.png")}" alt="Remove all the transcripts" id="edit_transcript_clear_img" title="Remove all the transcripts"/>
								Remove all transcript
							</td>
							<td>
								Remove all the current transcripts. All the text block will be removed. But this change will not be saved unless you click "save and exit" button.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_save_draft_22.png")}" alt="Save current transcript as draft" id="edit_transcript_save_draft_img" title="Save current transcript as draft"/>
								Save current transcript as draft
							</td>
							<td>
								Save the current transcript as draft and you can restore the transcript from the draft later.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_save_exit_22.png")}" alt="Save trancript and exit" id="edit_transcript_save_exit_img" title="Save trancript and exit"/>
								Save and exit
							</td>
							<td>
								Clicking this button,  you will save the edited transcript,  i.e. replace the old transcript with the edited one. You will also exit the editing mode by clicking this button.
								The saved draft will also be removed at the same time.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/player', file:"edit_transcript_quit_22.png")}" alt="Quit transcript editing without saving" id="edit_transcript_quit_img" title="Quit transcript editing without saving"/>
								Quit transcript editing
							</td>
							<td>
								By clicking this button,  you will exit the transcript editing mode without saving any of the changes. If you have saved a draft before,  the draft will still be there when next time
								you start transcript editing.
							</td>
						</tr>
				</table>
			</div>
			<br/>
			<div id="transcript_editing_right_click_menu_table">
				<table style="border:1px solid black;">
					<caption style="text-align:center;font-weight:bold;">Right-click menu for transcript editing</caption>
						<tr>
							<th>Action</th>
							<th>Explanation</th>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"play_16.png")}" alt="play transcript" title="play transcript"/>
								Play transcript
							</td>
							<td>
								Click on this item will set the current player time to the start time of this text blcok.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"edit.gif")}" alt="edit transcript" title="edit transcript"/>
								Edit transcript
							</td>
							<td>
								
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"merge_up_16.png")}" alt="merge the text block with the previous one" title="merge the text block with the previous one"/>
								Merge with previous
							</td>
							<td>
								Merge the text block with the previous block. The new text block will have the text of two old text blocks merged together. The start time
								of the new block will be start time of the former text block and the end time will be the end time of the latter text block.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"split_16.png")}" alt="split the text block" title="split the text block"/>
								Split the text block
							</td>
							<td>
								When you click this button,a split form will show. You can choose the text you want to split out from the "Transcript" textarea, and the selected text will automatically displayed 
								in "selected text" area. Then you can specify the start and endtime for the selected text. If you click the "submit" button, a new transcript block will be 
								created with the text you have just selected. the original text block will also be saved, but the text you selected for the new text block will be removed. And the start and end time will 
								both be changed to the endtime of the original text block.
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"delete.png")}" alt="delete transcript block" title="delete transcript block"/>
								Delete transcript block
							</td>
							<td>
								Delete this transcript block
							</td>
						</tr>
						<tr>
							<td>
								<img class="transcript_menu_img" src="${resource(dir:'images/skin', file:"door_16.png")}" alt="quit the right-click menu" title="quit the right-click menu"/>
								Quit the right click menu
							</td>
							<td>
								Quit the right click menu
							</td>
						</tr>
				</table>
			</div>
		</div>
	</div>
	<div id="synmark_help">
		<h2>Synmarks</h2>
	</div>
	<div id="presentation_help">
		<h2>Presentation</h2>
	</div>
</div>
</body>
