package reisaks.FinalProject.DomainModels

class Id { //Id generator
  def StringId : String = java.util.UUID.randomUUID.toString
}
