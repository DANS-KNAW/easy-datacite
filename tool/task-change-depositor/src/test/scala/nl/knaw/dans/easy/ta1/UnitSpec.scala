package nl.knaw.dans.easy.ta1

import org.scalatest._

abstract class UnitSpec extends FlatSpec with Matchers with
  OptionValues with Inside with Inspectors with OneInstancePerTest with BeforeAndAfter
