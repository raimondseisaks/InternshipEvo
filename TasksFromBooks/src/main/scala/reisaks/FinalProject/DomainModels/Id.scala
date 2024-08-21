package reisaks.FinalProject.DomainModels

class Id {
  def StringId : String = java.util.UUID.randomUUID.toString
}
