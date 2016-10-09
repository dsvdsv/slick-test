package db

sealed trait Entity[T] {
  def id: T
}

case class Bank(id: Long, name: String, parentId: Option[Long])
  extends Entity[Long]

case class Permission(name: String, id: Long)
  extends Entity[Long] {

  def this(id: Long) = this(name = s"Permission$id", id = id)
}

case class Role(
  name: String,
  permissions: Set[Permission] = Set.empty,
  id: Option[Long] = None
) extends Entity[Option[Long]] {

  def this(id: Long) = this(name = s"Role$id", id = Some(id))
}

case class User(
  username: String,
  enabled: Boolean,
  name: String,
  password: String,
  email: String,
  bank: Bank,
  phoneNumber: Option[String],
  additionalInfo: Option[String],
  roles: Set[Role] = Set.empty,
  id: Option[Long] = None
) extends Entity[Option[Long]] {

  def hasPermission(permission: String): Boolean =
    roles.exists(_.permissions.exists(_.name == permission))

}