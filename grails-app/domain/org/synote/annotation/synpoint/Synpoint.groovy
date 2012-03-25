package org.synote.annotation.synpoint

import org.synote.annotation.ResourceAnnotation

class Synpoint {

	static belongsTo = [annotation: ResourceAnnotation]
	
	Integer sourceStart
	Integer sourceEnd
	Integer targetStart
	Integer targetEnd
	
	static constraints = {
		sourceStart(nullable: true)
		sourceEnd(nullable: true)
		targetStart(nullable: true)
		targetEnd(nullable: true)
	}
	
	String toString()
	{
		return "[$sourceStart, $sourceEnd] -> [$targetStart, $targetEnd]"
	}
}
