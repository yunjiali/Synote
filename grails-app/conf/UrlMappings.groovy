class UrlMappings {
    static mappings = {
		"/sparql"{
			controller="linkedData"
			action="sparql"
		}
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
		"/resources/string/$id"{
			controller="linkedData"
			action="resourcesString"
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
		"/"(view:"/index")
		"/$controller/$action?/$id?"{
		      constraints {
				 // apply constraints here
			  }
		  }
	      
		  "400"(view:'/error/400')
		  "403"(view:'/error/403')
		  "500"(view:'/error')
	}
}
