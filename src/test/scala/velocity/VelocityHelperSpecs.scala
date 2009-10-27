package velocity

import org.specs.Specification

object VelocityHelperSpecs extends Specification {
	"VelocityHelper" should {
		"produce a non-empty context when passed a valid model" >> {
			val context = VelocityHelper.createVelocityContext(Map("tests" -> List("list test")))
			val keys = context.getKeys
			keys.length mustEqual 1
		}
	}
}
