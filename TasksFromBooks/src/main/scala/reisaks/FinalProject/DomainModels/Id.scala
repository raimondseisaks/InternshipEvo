package reisaks.FinalProject.DomainModels

import java.util.UUID

class Id {
  private val id: UUID = java.util.UUID.randomUUID
  def getStringId = id.toString
}
