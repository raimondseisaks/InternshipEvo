package reisaks.FinalProject.domainModels

class Id {
  def StringId : String = java.util.UUID.randomUUID.toString
}
