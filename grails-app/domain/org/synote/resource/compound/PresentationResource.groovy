package org.synote.resource.compound

import org.synote.resource.single.binary.PresentationSlide

class PresentationResource extends CompoundResource {

   static hasMany = [slides:PresentationSlide]
}
