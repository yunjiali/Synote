package org.synote.user.group

import org.synote.user.User
import org.synote.user.group.UserGroup
import org.synote.user.group.UserGroupMember
import org.synote.user.SynoteAuthBaseWebTest
import org.synote.utils.MessageService

//TODO: No actual test for creating and editing usergroups
class UserGroupWebTests extends SynoteAuthBaseWebTest {
	
	def messageService
	def grailsApplication
	
	void suite()
	{
		testUserGroup()
	}

   void testUserGroup() {
		String userName = grailsApplication.config.synote.test.account.userName
		String password = grailsApplication.config.synote.test.account.password
		String tempUserName = grailsApplication.config.synote.test.create.user.userName
		
		def owner = User.findByUserName(userName)
		def tempUser = getTempUser(tempUserName)
		def ug = UserGroup.buildLazy(owner:owner,shared:false)
		def ugShared = UserGroup.buildLazy(owner:owner,shared:true)
		
		String loginPageTitle = messageService.getMessage("org.synote.user.login.title")
		String homePageTitle = messageService.getMessage("org.synote.home.title")
		String groupListPageTitle = messageService.getMessage("org.synote.user.group.list.title")
		String createGroupPageTitle = messageService.getMessage("org.synote.user.group.create.title")
		String editGroupPageTitle = messageService.getMessage("org.synote.user.group.edit.title")
	    String createMemberPageTitle = messageService.getMessage("org.synote.user.group.createMember.title")
	    String createPermissionPageTitle = messageService.getMessage("org.synote.user.group.createPermission.title")
	    String editMemberPageTitle = messageService.getMessage("org.synote.user.group.editMember.title")
	    String editPermissionPageTitle = messageService.getMessage("org.synote.user.group.editPermission.title")
	    String showGroupPageTitle = messageService.getMessage("org.synote.user.group.show.title")
	
	    webtest("Group operation with anonymous users")
		{
			invoke(url:'userGroup/list')
			verifyTitle(text:groupListPageTitle)
			
			invoke(url:'userGroup/create')
			verifyTitle(text:loginPageTitle)

			invoke(url:'userGroup/show/'+ug.id)
			verifyTitle(text:loginPageTitle)

			invoke(url:'userGroup/show/'+ugShared.id)
			verifyTitle(text:loginPageTitle)
						
			invoke(url:'userGroup/edit/'+ug.id)
			verifyTitle(text:loginPageTitle)
			
			invoke(url:'userGroup/createMember/'+ug.id)
			verifyTitle(text:loginPageTitle)
			
			invoke(url:'userGroup/createPermission/'+ug.id)
			verifyTitle(text:loginPageTitle)
		}
		
	    webtest("Login")
	    {	
	    	login(userName,password)
		}
	    
		webtest("Group List")
		{
			invoke(url:'userGroup/list')
			verifyTitle(text:groupListPageTitle)
		}
		
		webtest("Create Group")
		{
			invoke(url:'userGroup/create')
			verifyTitle(text:createGroupPageTitle)
		}
		
		webtest("Edit Group")
		{
			invoke(url:'userGroup/edit/'+ug.id)
			verifyTitle(text:editGroupPageTitle)
		}
		
		webtest("Show Group")
		{
			invoke(url:'userGroup/show/'+ug.id)
			verifyTitle(text:showGroupPageTitle)
		}
		
		if(UserGroupMember.findByUserAndGroup(tempUser,ug))
		{
			deleteMember(ug,tempUser,editMemberPageTitle,showGroupPageTitle)
		}
		
		webtest("Add memeber")
		{
			clickLink "Add Member"
			setSelectField(name:"memberUserName",value:"${tempUser.userName}")
			//clickButton(name:"_action_saveMember")
			clickButton(label:"Add member")
			verifyTitle(text:createMemberPageTitle)
			verifyText(text:"was successfully added into group")
		}
	
		deleteMember(ug,tempUser,editMemberPageTitle,showGroupPageTitle)
		//TODO: Add more tests: add permission, delete permission etc
	
		webtest('logout')
		{
			logoutClickLink()	
		}
    }

	def deleteMember(UserGroup ug, User tempUser, String editMemberPageTitle, String showGroupPageTitle)
	{
		ant.group(description:'delete user membership'){
			invoke(url:'userGroup/show/'+ug.id)
			clickLink(label:"${tempUser.userName}")
			verifyTitle(text:editMemberPageTitle)
			clickButton(label:"Delete this member")
			expectDialog(dialogType:"confirm", response:true)
			//verityTitle(text:showGroupPageTitle)
			verifyText(text:"Member ${tempUser.userName} was successfully deleted")
		}
		
	}
}