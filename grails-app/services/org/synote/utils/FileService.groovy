package org.synote.utils

import java.io.File;
import java.util.UUID

class FileService {

    boolean transactional = true

	//TODO: create a job to delete the temp files that are older than say 1 months
	//dirname should omit the end backslash
    def createTempFile(String dirname, String filename) 
	{
		if(dirname!=null)
		{
			File dir = new File(dirname)
			if(!dir.exists())
				dir.mkdir()
		}
			
		File file = new File(dirname+File.separator+new StringBuilder().append(UUID.randomUUID()).append(filename))
		
		return file
    }
}
