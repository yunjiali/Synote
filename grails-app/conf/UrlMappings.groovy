class UrlMappings {
    static mappings = {
		"/resources/$id"{
			controller="linkedData"
			action="resources"	
		}
		"/annotations/$id"{
			controller="linkedData"
			action="annotations"
		}
		"/users/$id"{
			controller="linkedData"
			action="users"
		}
		"/resources/data/$id"{
			controller="linkedData"
			action="resourcesData"
		}
		"/annotations/data/$id"{
			controller="linkedData"
			action="annotationsData"
		}
		"/users/data/$id"{
			controller="linkedData"
			action="usersData"
		}
		"/$controller/$action?/$id?"{
		      constraints {
				 // apply constraints here
			  }
		  }
	      "/"(view:"/index")
	
	      "/recording/$action?/$id?"{
				controller="recording"
				constraints
						{
							//position(matches:/[0-9]+/)
						}
			}
		  "500"(view:'/error')
	}
}
