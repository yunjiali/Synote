databaseChangeLog = {

	changeSet(id: "v5_configuration_modify", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "configuration", columnName: "description")
				}
			}
		}

		addColumn(tableName: "configuration") {
			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "true")
			}
		}

		modifyColumn(tableName: "configuration") {
			column(name: "val", type: "text")
		}
	}

	changeSet(id: "v5_user_group_add_columns", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "user_group", columnName: "description")
				}
			}
		}

		addColumn(tableName: "user_group") {
			column(name: "description", type: "varchar(255)") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "user_group") {
			column(name: "date_created", type: "datetime", defaultValueDate: "2010-01-01T00:00:00") {
				constraints(nullable: "false")
			}
		}

		addColumn(tableName: "user_group") {
			column(name: "last_updated", type: "datetime", defaultValueDate: "2010-01-01T00:00:00") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(id: "v5_configuration_insert_entermedia", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			sqlCheck("SELECT COUNT(0) from configuration\n				where name="org.synote.resource.fileUpload.enabled";", expectedResult: "0")
		}

		sql("INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.resource.fileUpload.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.username","synote",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.password","synote",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.catelog","synote",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.host","http://localhost:8081",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.restapi","/media/services/rest/",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.catalogid","/media/catalogs/synote",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.sourcepath","users/synote/",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.uploadFile","upload.xml",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.login","login.xml",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.entermedia.fileDownload","downloads/originals",now(),now());")
	}

	changeSet(id: "v5_configuration_insert", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			sqlCheck("SELECT COUNT(0) from configuration\n				where name="org.synote.user.forgetPassword.enabled";", expectedResult: "0")
		}

		sql("INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.user.forgetPassword.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.twitter.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.integration.viascribe.xmlUpload.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, date_created,\n			last_updated)\n			Values(0,"org.synote.user.register.captcha.enabled","false",now(),now());\n			INSERT INTO configuration (version, name, val, description, date_created,\n			last_updated)\n			Values(0,"org.synote.metadata.contact.page.content","mw@ecs.soton.ac.uk","The content of contact page if you want to customise the contact page provided by Synote.",now(),now());\n			INSERT INTO configuration (version, name, val, description, date_created,\n			last_updated)\n			Values(0,"org.synote.metadata.contact.page.url","/user/contact","The content of contact page if you want to use external page.",now(),now());\n			INSERT INTO configuration (version, name, val, description, date_created,\n			last_updated)\n			Values(0,"org.synote.metadata.contact.email","admin@synote.org","The email address of the contact",now(),now());\n			INSERT INTO configuration (version, name, val, description,date_created,\n			last_updated)\n			Values(0,"org.synote.metadata.legal.termsAndConditions","Default terms and Conditions", "The terms and conditions page corresponding to the legal link on the front page.",now(),now());\n			INSERT INTO configuration (version, name, val, description,date_created,\n			last_updated)\n			Values(0,"org.synote.search.crawl.snapshot.waitTime","20000", "The waiting time for javascript execution when creating the html snapshot for google crawler.",now(),now());\n			INSERT INTO configuration (version, name, val, description,date_created,\n			last_updated)\n			Values(0,"org.synote.linkeddata.settings.baseURI","http://linkeddata.synote.org/", "includes the protocal or server names for linkeddata URIs",now(),now());\n			INSERT INTO configuration (version, name, val, description,date_created,\n			last_updated)\n			Values(0,"org.synote.linkeddata.settings.serverContext","synote/", "the context path of the application",now(),now());")
	}

	changeSet(id: "v5_permission_modify", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "permission", columnName: "date_created")
				}
			}
		}

		addColumn(tableName: "permission") {
			column(name: "date_created", type: "datetime", defaultValueDate: "2010-01-01T00:00:00") {
				constraints(nullable: "false")
			}
		}

		addColumn(tableName: "permission") {
			column(name: "last_updated", type: "datetime", defaultValueDate: "2010-01-01T00:00:00") {
				constraints(nullable: "false")
			}
		}
	}

	changeSet(id: "v5_views_table_add", author: "Yunjia Li") {
		preConditions {
			and {
				not {
					tableExists(tableName: "views")
				}
			}
		}

		sql("create table views (\n			`id` bigint(20) primary key auto_increment,\n			`version` bigint(20) not null,\n			`user_id` bigint(20) default null,\n			`resource_id` bigint(20) not null,\n			`date_created` datetime not null,\n			`last_updated` datetime not null,\n			foreign key (`user_id`) references `user` (`id`) on delete set null,\n			foreign key (`resource_id`) references `resource` (`id`) on delete cascade\n			) engine=innodb default charset=utf8;")
	}

	changeSet(id: "v5_vw_multimedia_user_permissionvalue_update", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			columnExists(tableName: "permission", columnName: "date_created")

			viewExists(viewName: "vw_multimedia_user_permissionvalue")
		}

		dropView(viewName: "vw_multimedia_user_permissionvalue")

		createView("select r.id as id, r.version as version, u.user_name as owner_name, u.id as owner_id, r.title as title, pv.name as public_perm_name, pv.val as public_perm_val, pv.id as public_perm_id, r.date_created as date_created, r.last_updated as last_updated from resource as r, user as u, permission_value as pv\n			where r.class="org.synote.resource.compound.MultimediaResource" and r.owner_id = u.id and r.perm_id=pv.id", viewName: "vw_multimedia_user_permissionvalue")
	}

	changeSet(id: "v5_vw_multimedia_user_group_member_permission_update", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			and {
				columnExists(tableName: "permission", columnName: "date_created")

				viewExists(viewName: "vw_multimedia_user_group_member_permission")
			}
		}

		dropView(viewName: "vw_multimedia_user_group_member_permission")

		createView("SELECT DISTINCT p.resource_id as resource_id,r.title as title, u.id as user_id,u.user_name as user_name,g.id as group_id, g.name as group_name,pv.id as user_perm_id, pv.name as user_perm_name, pv.val as user_perm_val,r.date_created as date_created, r.last_updated as last_updated, p.date_created as perm_date_created, p.last_updated as perm_last_updated\n			FROM user u, user_group_member m, user_group g, resource r, permission p, permission_value pv \n			WHERE r.class="org.synote.resource.compound.MultimediaResource" AND p.class="org.synote.permission.ResourcePermission" AND \n				u.id = m.user_id AND m.group_id = g.id AND g.id = p.group_id AND p.resource_id = r.id AND r.owner_id != u.id AND pv.id = p.perm_id;", viewName: "vw_multimedia_user_group_member_permission")
	}

	changeSet(id: "v5_resource_add_columns", author: "Yunjia Li", context: "v0.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "resource", columnName: "cue_index")
				}
			}
		}

		addColumn(tableName: "resource") {
			column(name: "cue_index", type: "int(11)") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "cue_settings", type: "text") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "file_header", type: "text") {
				constraints(nullable: "true")
			}
		}
	}

	changeSet(id: "v5_resource_add_columns_2", author: "Yunjia Li", context: "v0.1.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "resource", columnName: "thumbnail")
				}
			}
		}

		addColumn(tableName: "resource") {
			column(name: "thumbnail", type: "varchar(255)") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "real_starttime", type: "datetime") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "real_endtime", type: "datetime") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "duration", type: "bigint(20)", defaultValueNumeric: "-1") {
				constraints(nullable: "true")
			}
		}

		addColumn(tableName: "resource") {
			column(name: "uuid", type: "varchar(255)") {
				constraints(nullable: "true")
			}
		}
	}

	changeSet(id: "v5_resource_update_uuid", author: "Yunjia Li", context: "v0.1.1") {
		preConditions {
			and {
				columnExists(tableName: "resource", columnName: "uuid")
			}
		}

		sql("UPDATE resource SET uuid = UUID() where uuid = NULL AND class="org.synote.resource.compound.MultimediaResource";")
	}

	changeSet(id: "v5_vw_multimedia_user_permissionvalue_update", author: "Yunjia Li", context: "v0.1.1") {
		preConditions {
			and {
				columnExists(tableName: "permission", columnName: "date_created")

				viewExists(viewName: "vw_multimedia_user_permissionvalue")
			}
		}

		dropView(viewName: "vw_multimedia_user_permissionvalue")

		createView("select r.id as id, r.version as version, u.user_name as owner_name, u.id as owner_id, r.title as title, pv.name as public_perm_name, pv.val as public_perm_val, pv.id as public_perm_id, r.date_created as date_created, r.last_updated as last_updated, r.real_starttime as real_starttime, r.real_endtime as real_endtime, r.thumbnail as thumbnail, r.duration as duration, r.uuid as uuid from resource as r, user as u, permission_value as pv\n			where r.class="org.synote.resource.compound.MultimediaResource" and r.owner_id = u.id and r.perm_id=pv.id", viewName: "vw_multimedia_user_permissionvalue")
	}

	changeSet(id: "v5_vw_multimedia_user_group_member_permission_update", author: "Yunjia Li", context: "v0.1.1") {
		preConditions {
			and {
				columnExists(tableName: "permission", columnName: "date_created")

				viewExists(viewName: "vw_multimedia_user_group_member_permission")
			}
		}

		dropView(viewName: "vw_multimedia_user_group_member_permission")

		createView("SELECT DISTINCT p.resource_id as resource_id,r.title as title, u.id as user_id,u.user_name as user_name,g.id as group_id, g.name as group_name,pv.id as user_perm_id, pv.name as user_perm_name, pv.val as user_perm_val,r.date_created as date_created, r.last_updated as last_updated, p.date_created as perm_date_created, p.last_updated as perm_last_updated,r.real_starttime as real_starttime, r.real_endtime as real_endtime, r.thumbnail as thumbnail, r.duration as duration, r.uuid as uuid\n			FROM user u, user_group_member m, user_group g, resource r, permission p, permission_value pv \n			WHERE r.class="org.synote.resource.compound.MultimediaResource" AND p.class="org.synote.permission.ResourcePermission" AND \n				u.id = m.user_id AND m.group_id = g.id AND g.id = p.group_id AND p.resource_id = r.id AND r.owner_id != u.id AND pv.id = p.perm_id;", viewName: "vw_multimedia_user_group_member_permission")
	}

	changeSet(id: "v5_synpoint_add_columns", author: "Yunjia Li", context: "v0.1.1") {
		preConditions {
			and {
				not {
					columnExists(tableName: "synpoint", columnName: "target_xywh")
				}
			}
		}

		addColumn(tableName: "synpoint") {
			column(name: "target_xywh", type: "varchar(255)") {
				constraints(nullable: "true")
			}
		}
	}
}
