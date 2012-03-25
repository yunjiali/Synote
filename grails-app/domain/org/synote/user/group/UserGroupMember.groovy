package org.synote.user.group

import org.synote.user.User

class UserGroupMember {

	static belongsTo = [user: User, group: UserGroup]
}
